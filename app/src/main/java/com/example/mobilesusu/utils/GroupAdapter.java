package com.example.mobilesusu.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private final List<String> groups;
    private final OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onEditClick(String groupName);
        void onDeleteClick(String groupName);
    }

    public GroupAdapter(List<String> groups, OnGroupClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groups.get(position);
        holder.textViewGroupName.setText(groupName);

        holder.buttonEditGroup.setOnClickListener(v -> {
            Log.d("GroupAdapter", "Edit button clicked for: " + groupName);
            listener.onEditClick(groupName);
        });

        holder.buttonDeleteGroup.setOnClickListener(v -> {
            Log.d("GroupAdapter", "Delete button clicked for: " + groupName);
            listener.onDeleteClick(groupName);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGroupName;
        Button buttonEditGroup, buttonDeleteGroup;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);
            buttonEditGroup = itemView.findViewById(R.id.buttonEditGroup);
            buttonDeleteGroup = itemView.findViewById(R.id.buttonDeleteGroup);
        }
    }
}