package com.example.fitnessapp2.Admin.UserProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp2.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnUserClickListener listener;
    private Context context;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.idTextView.setText(user.getuId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailPage.class);
            intent.putExtra("userId", user.getuId());
            intent.putExtra("name", user.getName());
            intent.putExtra("age", user.getAge());
            intent.putExtra("gender", user.getGender());
            intent.putExtra("height", user.getHeight());
            intent.putExtra("body_weight", user.getBodyWeight());
            intent.putExtra("dietPreference", user.getDietPreference());

            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, idTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_name);
            idTextView = itemView.findViewById(R.id.user_id);
            //emailTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}

