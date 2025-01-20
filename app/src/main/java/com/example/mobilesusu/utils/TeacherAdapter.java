package com.example.mobilesusu.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;
import com.example.mobilesusu.models.Teacher;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private List<Teacher> teachers;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onEditClick(Teacher teacher);
        void onDeleteClick(Teacher teacher);
    }

    public TeacherAdapter(List<Teacher> teachers, OnTeacherClickListener listener) {
        this.teachers = teachers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        holder.textViewTeacherName.setText(teacher.getName() + " " + teacher.getSurname());
        holder.textViewTeacherEmail.setText(teacher.getEmail());
        holder.textViewTeacherLogin.setText("Логин: " + teacher.getLogin());

        // Логирование для отладки
        Log.d("TeacherAdapter", "Binding teacher: " + teacher.getName());

        // Обработка нажатия на кнопку "Изменить"
        holder.buttonEditTeacher.setOnClickListener(v -> {
            Log.d("TeacherAdapter", "Edit button clicked for: " + teacher.getName());
            if (listener != null) {
                listener.onEditClick(teacher);
            } else {
                Log.e("TeacherAdapter", "Listener is null");
            }
        });

        // Обработка нажатия на кнопку "Удалить"
        holder.buttonDeleteTeacher.setOnClickListener(v -> {
            Log.d("TeacherAdapter", "Delete button clicked for: " + teacher.getName());
            Log.d("TeacherAdapter", "Delete button clicked for id: " + teacher.getId());
            if (listener != null) {
                listener.onDeleteClick(teacher);
            } else {
                Log.e("TeacherAdapter", "Listener is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTeacherName, textViewTeacherEmail, textViewTeacherLogin, textViewTeacherPassword;
        Button buttonEditTeacher, buttonDeleteTeacher;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTeacherName = itemView.findViewById(R.id.textViewTeacherName);
            textViewTeacherEmail = itemView.findViewById(R.id.textViewTeacherEmail);
            textViewTeacherLogin = itemView.findViewById(R.id.textViewTeacherLogin);
            buttonEditTeacher = itemView.findViewById(R.id.buttonEditTeacher);
            buttonDeleteTeacher = itemView.findViewById(R.id.buttonDeleteTeacher);
        }
    }
}