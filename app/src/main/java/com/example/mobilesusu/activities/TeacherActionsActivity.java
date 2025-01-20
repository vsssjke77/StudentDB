package com.example.mobilesusu.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;
import com.example.mobilesusu.dialog.AddTeacherDialog;
import com.example.mobilesusu.dialog.EditTeacherDialog;
import com.example.mobilesusu.utils.TeacherAdapter;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Teacher;

import java.util.List;

public class TeacherActionsActivity extends AppCompatActivity implements TeacherAdapter.OnTeacherClickListener, AddTeacherDialog.AddTeacherDialogListener,
        EditTeacherDialog.EditTeacherDialogListener {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TeacherAdapter teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_actions);

        dbHelper = new DatabaseHelper(this);

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTeachers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка списка преподавателей
        loadTeachers();

        // Кнопка для добавления преподавателя
        Button buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        buttonAddTeacher.setOnClickListener(v -> {
            // Открыть диалог добавления преподавателя
            AddTeacherDialog dialog = new AddTeacherDialog();
            dialog.show(getSupportFragmentManager(), "AddTeacherDialog");
        });
    }

    private void loadTeachers() {
        List<Teacher> teachers = dbHelper.getAllTeachers();
        if (teachers.isEmpty()) {
            Log.e("TeacherActionsActivity", "Teachers list is empty");
        } else {
            Log.d("TeacherActionsActivity", "Teachers list size: " + teachers.size());
        }
        teacherAdapter = new TeacherAdapter(teachers, this);
        recyclerView.setAdapter(teacherAdapter);
    }

    @Override
    public void onEditClick(Teacher teacher) {
        // Открыть диалог редактирования преподавателя
        EditTeacherDialog dialog = new EditTeacherDialog(teacher);
        dialog.show(getSupportFragmentManager(), "EditTeacherDialog");
    }

    @Override
    public void onDeleteClick(Teacher teacher) {
        Log.d("TeacherActionsActivity", "Deleting teacher: " + teacher.getName() + ", ID: " + teacher.getId());
        boolean isDeleted = dbHelper.deleteTeacher(teacher.getId());
        if (isDeleted) {
            Log.d("TeacherActionsActivity", "Teacher deleted successfully");
        } else {
            Log.e("TeacherActionsActivity", "Failed to delete teacher");
        }
        loadTeachers();
    }

    @Override
    public void onTeacherAdded(Teacher teacher) {
        // Обновить список после добавления преподавателя
        loadTeachers();
    }

    @Override
    public void onTeacherUpdated(Teacher teacher) {
        // Обновить данные преподавателя в базе данных
        boolean isUpdated = dbHelper.updateTeacher(teacher);
        if (isUpdated) {
            Log.d("TeacherActionsActivity", "Teacher updated successfully");
            loadTeachers(); // Обновить список
        } else {
            Log.e("TeacherActionsActivity", "Failed to update teacher");
        }
    }
}