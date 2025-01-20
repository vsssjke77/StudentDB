package com.example.mobilesusu.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;
import com.example.mobilesusu.models.Student;

import java.util.List;

public class StudentAdapterForStudent extends RecyclerView.Adapter<StudentAdapterForStudent.StudentViewHolder> {
    private List<Student> students;

    public StudentAdapterForStudent(List<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.textViewStudentName.setText(student.getName() + " " + student.getSurname());
        holder.textViewStudentEmail.setText(student.getEmail());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStudentName, textViewStudentEmail;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewStudentEmail = itemView.findViewById(R.id.textViewStudentEmail);
        }
    }
}