package com.example.fitnessapp2.Fragments.FoodDb;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp2.R;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private ArrayList<Food> foodList;
    private Context context;
    //private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    public FoodAdapter(Context context, ArrayList<Food> foodList ,OnItemClickListener listener) {
        this.foodList = foodList;
        this.context = context;
        //this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food foodItem = foodList.get(position);
        holder.descriptionTextView.setText(foodItem.getName());
        holder.foodProtein.setText(String.format("Protein: %.2f g", foodItem.getProtein()));
        holder.foodCarbs.setText(String.format("Carbs: %.2f g", foodItem.getCarbs()));
        holder.foodFats.setText(String.format("Fats: %.2f g", foodItem.getFats()));

        holder.foodCalories.setVisibility(GONE);

        boolean isExpanded = foodItem.isExpanded();
        holder.macronutrientContainer.setVisibility(isExpanded ? View.VISIBLE : GONE);


        holder.itemView.setOnClickListener(v -> {
            foodItem.setExpanded(!foodItem.isExpanded()); // Toggle state
            notifyItemChanged(position);
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(foodItem);
//            }
        });
    }
    public void updateList(List<Food> newList) {
        this.foodList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView, foodProtein, foodCarbs, foodFats, foodCalories;
        LinearLayout macronutrientContainer;


        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.food_description);
            foodProtein = itemView.findViewById(R.id.food_protein);
            foodCarbs = itemView.findViewById(R.id.food_carbs);
            foodFats = itemView.findViewById(R.id.food_fats);
            macronutrientContainer = itemView.findViewById(R.id.macronutrient_container);
            foodCalories = itemView.findViewById(R.id.food_calories);
        }
    }
}

