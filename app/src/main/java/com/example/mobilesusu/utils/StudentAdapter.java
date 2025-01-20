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
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> students;
    private List<Student> filteredStudents;

    private OnStudentClickListener listener;
    private DatabaseHelper dbHelper;


    public interface OnStudentClickListener {
        void onEditClick(Student student);
        void onDeleteClick(Student student);
    }

    public StudentAdapter(List<Student> students, OnStudentClickListener listener, DatabaseHelper dbHelper) {
        this.students = students;
        this.filteredStudents = new ArrayList<>(students);
        this.listener = listener;
        this.dbHelper = dbHelper;

    }

    public void filter(String surnameQuery, String groupQuery) {
        filteredStudents.clear();

        if (surnameQuery.isEmpty() && groupQuery.isEmpty()) {
            filteredStudents.addAll(students);
        } else {
            for (Student student : students) {
                boolean matchesSurname = student.getSurname().toLowerCase().contains(surnameQuery.toLowerCase());
                boolean matchesGroup = dbHelper.getGroupNameById(student.getGroupId()).toLowerCase().contains(groupQuery.toLowerCase());

                if (matchesSurname && matchesGroup) {
                    filteredStudents.add(student);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_adm, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = filteredStudents.get(position);
        holder.textViewStudentName.setText(student.getName() + " " + student.getSurname());
        holder.textViewStudentEmail.setText(student.getEmail());
        holder.textViewStudentLogin.setText("Логин: " + student.getLogin());

        // Получаем название группы по её ID
        String groupName = dbHelper.getGroupNameById(student.getGroupId());
        holder.textViewStudentGroup.setText("Группа: " + groupName);

        holder.buttonEditStudent.setOnClickListener(v -> {
            Log.d("StudentAdapter", "Edit button clicked for: " + student.getName());
            listener.onEditClick(student);
        });

        holder.buttonDeleteStudent.setOnClickListener(v -> {
            Log.d("StudentAdapter", "Delete button clicked for: " + student.getName());
            listener.onDeleteClick(student);
        });
    }

    @Override
    public int getItemCount() {
        return filteredStudents.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStudentName, textViewStudentEmail, textViewStudentLogin,textViewStudentGroup;
        Button buttonEditStudent, buttonDeleteStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewStudentEmail = itemView.findViewById(R.id.textViewStudentEmail);
            textViewStudentLogin = itemView.findViewById(R.id.textViewStudentLogin);
            textViewStudentGroup = itemView.findViewById(R.id.textViewStudentGroup);

            buttonEditStudent = itemView.findViewById(R.id.buttonEditStudent);
            buttonDeleteStudent = itemView.findViewById(R.id.buttonDeleteStudent);
        }
    }
}