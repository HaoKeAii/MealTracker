package com.example.fitnessapp2.Fragments.Output;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp2.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodList;

    public FoodAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem food = foodList.get(position);
        holder.nameTextView.setText(food.getName());
        holder.caloriesTextView.setText("Calories: " + food.getCalories() + " kcal");
        holder.proteinTextView.setText("Protein: " + food.getProtein() + " g");
        holder.carbsTextView.setText("Carbs: " + food.getCarbs() + " g");
        holder.fatsTextView.setText("Fats: " + food.getFats() + " g");
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, caloriesTextView, proteinTextView, carbsTextView, fatsTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.food_description);
            caloriesTextView = itemView.findViewById(R.id.food_calories);
            proteinTextView = itemView.findViewById(R.id.food_protein);
            carbsTextView = itemView.findViewById(R.id.food_carbs);
            fatsTextView = itemView.findViewById(R.id.food_fats);
        }
    }
}
