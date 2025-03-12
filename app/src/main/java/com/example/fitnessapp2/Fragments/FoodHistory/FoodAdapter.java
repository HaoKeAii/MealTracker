package com.example.fitnessapp2.Fragments.FoodHistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.example.fitnessapp2.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodList;
    DecimalFormat df;

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
        df = new DecimalFormat("0.00");

        FoodItem foodItem = foodList.get(position);
        holder.dishName.setText("Meal: " + foodItem.getDishName());
        //holder.foodDetails.setVisibility(View.VISIBLE);

        double totalProtein = 0, totalCarbs = 0, totalFats = 0, totalCalories = 0;
        StringBuilder foodDetails = new StringBuilder();

        for (Map<String, Object> food : foodItem.getFoods()) {
            String name = (String) food.get("name");
            Map<String, Object> macros = (Map<String, Object>) food.get("macronutrients");

            double protein = (double) macros.get("protein");
            double carbs = (double) macros.get("carbs");
            double fats = (double) macros.get("fats");
            double calories = (double) macros.get("calories");

            totalProtein += protein;
            totalCarbs += carbs;
            totalFats += fats;
            totalCalories += calories;

            // Append individual food item details
            foodDetails.append("🔹 ").append(name)
                    .append("\n  Calories: ").append(calories).append("kcal")
                    .append("\n  Protein: ").append(protein).append("g")
                    .append("\n  Carbs: ").append(carbs).append("g")
                    .append("\n  Fats: ").append(fats).append("g")
                    .append("\n\n");
        }

        // Set aggregated macronutrient values
        holder.foodCalories.setText("Calories: " + df.format(totalCalories) + "kcal");
        holder.foodProtein.setText("Protein: " + df.format(totalProtein) + "g");
        holder.foodCarbs.setText("Carbs: " + df.format(totalCarbs) + "g");
        holder.foodFats.setText("Fats: " + df.format(totalFats) + "g");

        // Show details of individual food items
        holder.foodDetails.setText(foodDetails.toString());

        boolean isExpanded = foodItem.isExpanded();
        holder.foodDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            foodItem.setExpanded(!foodItem.isExpanded()); // Toggle state
            notifyItemChanged(position);
        });

        if (foodItem.getImageUri() != null && !foodItem.getImageUri().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(foodItem.getImageUri()) // Load the single image URI
                    .placeholder(R.drawable.placeholder) // Default image
                    .error(R.drawable.img_error) // If loading fails
                    .into(holder.imageView);
        }
        else {
            holder.imageView.setImageResource(R.drawable.placeholder); // Default image
        }

        holder.imageView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView dishName, foodDetails, foodProtein, foodCarbs, foodFats, foodCalories;
            ImageView imageView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            dishName = itemView.findViewById(R.id.food_description);
            foodDetails = itemView.findViewById(R.id.food_details);
            foodProtein = itemView.findViewById(R.id.food_protein);
            foodCarbs = itemView.findViewById(R.id.food_carbs);
            foodFats = itemView.findViewById(R.id.food_fats);
            foodCalories = itemView.findViewById(R.id.food_calories);
            imageView = itemView.findViewById(R.id.food_image);
        }
    }
}
