package com.example.fitnessapp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.ai.client.generativeai.*;
import com.google.ai.client.generativeai.type.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    PreviewView previewView;
    ImageButton captureButton, backButton, galleryButton, flashButton;
    ImageCapture imageCapture;
    FirebaseFirestore db;
    String userId;
    Executor executor = Executors.newSingleThreadExecutor();
    FrameLayout loadingScreen;
    FirebaseStorage storage;
    StorageReference storageRef;
    ImageView gifImageView;
    ProgressBar progressBar;
    private Camera camera;
    private boolean isFlashOn = false;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    startCamera();
                }
            });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backHome();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerax_fragment);

        previewView = findViewById(R.id.camera_preview);
        captureButton = findViewById(R.id.camera_button);
        backButton = findViewById(R.id.back_button);
        galleryButton = findViewById(R.id.gallery_button);
        loadingScreen = findViewById(R.id.loading_screen);
        gifImageView = findViewById(R.id.gif_loading);
        progressBar = findViewById(R.id.progress_bar);
        flashButton = findViewById(R.id.flash_button);

        Glide.with(getApplicationContext())
                .load(R.drawable.food_running) // Replace with your GIF resource
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(gifImageView);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }

        backButton.setOnClickListener(v -> backHome());
        flashButton.setOnClickListener(v -> toggleFlash());

        captureButton.setOnClickListener(v -> takePicture());
        galleryButton.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            Log.e(TAG, "No image selected or operation cancelled.");
            return;
        }

        Uri selectedImageUri = data.getData();
        Bitmap selectedImage = null;

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    Log.e(TAG, "Error loading image", e);
                }
            }

            if (selectedImage != null) {
                loadingScreen.setVisibility(View.VISIBLE);
                processImageWithGemini(selectedImage, selectedImageUri);
                progressBar.setProgress(20);
            }
        }
    }

    private void backHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = listenableFuture.get();
                Preview preview = new Preview.Builder().build();

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        File file = new File(getExternalFilesDir(null), "captured_image.jpg");
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(options, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    //Toast.makeText(CameraActivity.this, "Image captured!", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Uri imageUri = Uri.fromFile(file);
                    loadingScreen.setVisibility(View.VISIBLE);
                    camera.getCameraControl().enableTorch(!isFlashOn);
                    processImageWithGemini(bitmap, imageUri);
                    progressBar.setProgress(20);
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Image capture failed", exception);
            }
        });
    }

    private void toggleFlash() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);
        } else {
            Toast.makeText(this, "Flash not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void processImageWithGemini(Bitmap bitmap, Uri imgUri) {
        GenerativeModel gm = new GenerativeModel("gemini-2.0-flash", BuildConfig.apiKey);
        progressBar.setProgress(50);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

//        String foodJson = loadJSONFile("summary_nutrients_updated.json");
//        if (foodJson == null) {
//            Log.e(TAG, "Failed to load food database.");
//            return;
//        }

        String prompt = """
            Please return JSON describing the food items detected in the image, using the following schema:

            {
                "dish_name": str,
                "Foods": [
                    {
                        "name": str,
                        "macronutrients": {
                            "calories": float,
                            "protein": float,
                            "carbs": float,
                            "fats": float
                        }
                    }
                ]
            }
            
            Foods name first letter should be in capital letter
            Important: Only return a single valid JSON object.
            """;


        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText(prompt)
                .build();

        progressBar.setProgress(70);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    String resultText = result.getText();
                    progressBar.setProgress(100);
                    Log.d(TAG, "Gemini Response: " + resultText);
                    Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                    intent.putExtra("openFragment", "ScanFragment"); // Send flag
                    intent.putExtra("output", resultText);
                    intent.putExtra("imageUri", imgUri.toString()); // Send Image URI
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Gemini API Error", t);
            }
        }, executor);
    }

    //private String loadJSONFile(String filename) {
//        StringBuilder jsonString = new StringBuilder();
//        try (InputStream is = getAssets().open(filename);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonString.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Error reading JSON file: " + filename, e);
//            return null;
//        }
//        return jsonString.toString();
//    }
}
