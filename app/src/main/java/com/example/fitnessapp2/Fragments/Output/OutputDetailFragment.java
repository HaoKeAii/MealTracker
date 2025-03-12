package com.example.fitnessapp2.Fragments.Output;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OutputDetailFragment extends Fragment {
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<FoodItem> foodList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meal_output_detailed_page, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.rvMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        foodList = new ArrayList<>();
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            String foodJson = args.getString("food"); // Get JSON string
            Log.d("OutputFragment", foodJson);
            try {
                JSONObject jsonObject = new JSONObject(foodJson);
                JSONArray detectedFoods = jsonObject.getJSONArray("Foods");

                for (int i = 0; i < detectedFoods.length(); i++) {
                    JSONObject foodObject = detectedFoods.getJSONObject(i);
                    String name = foodObject.getString("name");

                    JSONObject macros = foodObject.getJSONObject("macronutrients");
                    double calories = macros.getDouble("calories");
                    double protein = macros.getDouble("protein");
                    double carbs = macros.getDouble("carbs");
                    double fats = macros.getDouble("fats");

                    foodList.add(new FoodItem(name, calories, protein, carbs, fats));

                }
//                Log.d("OutputFragment", "Received food: " + name);
//                Log.d("OutputFragment", "Calories: " + calories + ", Protein: " + protein + ", Carbs: " + carbs + ", Fats: " + fats);
            } catch (JSONException e) {
                Log.e("OutputFragment", "Error parsing JSON", e);
            }
        }
    }

}
