package com.example.fitnessapp2.Fragments.FoodDb;

import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp2.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DatabaseFragment extends Fragment {
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private ArrayList<Food> foodList, filteredList;
    private SearchView searchView;
    private Spinner sortSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.food_database_page, container, false);

        recyclerView = view.findViewById(R.id.rvMain);
        searchView = view.findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sortSpinner = view.findViewById(R.id.sort_spinner);

        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Load the JSON data from the asset
        loadJSONFromAsset();

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // We handle filtering in real-time
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        return view;
    }

    private void filterList(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(foodList);
        } else {
            for (Food food : foodList) {
                if (food.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(food);
                }
            }
        }
        foodAdapter.updateList(filteredList);
    }

    private float extractFloat(String value) {
        try {
            return Float.parseFloat(value.split(" ")[0]); // Extracts only the number
        } catch (Exception e) {
            return 0.0f; // Default if parsing fails
        }
    }

    private void sortList(int position) {
        switch (position) {
            case 0: // Name A-Z
                Collections.sort(filteredList, Comparator.comparing(Food::getName));
                break;
            case 1: // Name Z-A
                Collections.sort(filteredList, (f1, f2) -> f2.getName().compareTo(f1.getName()));
                break;
            case 2: // Protein High to Low
                Collections.sort(filteredList, (f1, f2) -> Float.compare(f2.getProtein(), f1.getProtein()));
                break;
            case 3: // Protein Low to High
                Collections.sort(filteredList, Comparator.comparing(Food::getProtein));
                break;
            case 4: // Carbs High to Low
                Collections.sort(filteredList, (f1, f2) -> Float.compare(f2.getCarbs(), f1.getCarbs()));
                break;
            case 5: // Carbs Low to High
                Collections.sort(filteredList, Comparator.comparing(Food::getCarbs));
                break;
            case 6: // Fats High to Low
                Collections.sort(filteredList, (f1, f2) -> Float.compare(f2.getFats(), f1.getFats()));
                break;
            case 7: // Fats Low to High
                Collections.sort(filteredList, Comparator.comparing(Food::getFats));
                break;
        }
        foodAdapter.updateList(filteredList);
    }

    private void loadJSONFromAsset() {
        try {
           // if (getActivity() != null) { // ✅ Prevents null errors
                InputStream is = getActivity().getAssets().open("summary_nutrients_updated.json");
                JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
                jsonReader.beginObject();

                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    if (name.equals("Foods")) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            jsonReader.beginObject();

                            String fName = null;
                            float protein = 0, carbs = 0, fats = 0;

                            while (jsonReader.hasNext()) {
                                String fieldName = jsonReader.nextName();
                                if (fieldName.equals("name")) {
                                    fName = jsonReader.nextString();
                                } else if (fieldName.equals("nutrients")) {
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()) {
                                        String nutrientName = jsonReader.nextName();
                                        switch (nutrientName) {
                                            case "Protein":
                                                protein = extractFloat(jsonReader.nextString());
                                                break;
                                            case "Carbohydrate, by difference":
                                                carbs = extractFloat(jsonReader.nextString());
                                                break;
                                            case "Total lipid (fat)":
                                                fats = extractFloat(jsonReader.nextString());
                                                break;
                                            default:
                                                jsonReader.skipValue();
                                                break;
                                        }
                                    }
                                    jsonReader.endObject();
                                } else {
                                    jsonReader.skipValue();
                                }
                            }

                            foodList.add(new Food(fName, protein, carbs, fats));
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
                jsonReader.close();

                filteredList.addAll(foodList);

                foodAdapter = new FoodAdapter(getActivity(), filteredList, food -> {
                });

                // Sort foodList before setting the adapter
                Collections.sort(foodList, Comparator.comparing(food -> food.name.toLowerCase()));

                recyclerView.setAdapter(foodAdapter);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
