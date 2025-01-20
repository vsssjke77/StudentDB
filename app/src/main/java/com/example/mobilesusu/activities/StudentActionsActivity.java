package com.example.mobilesusu.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.dialog.AddStudentDialog;
import com.example.mobilesusu.dialog.EditStudentDialog;
import com.example.mobilesusu.models.Student;
import com.example.mobilesusu.utils.StudentAdapter;

import java.util.List;

public class StudentActionsActivity extends AppCompatActivity implements StudentAdapter.OnStudentClickListener, AddStudentDialog.AddStudentDialogListener, EditStudentDialog.EditStudentDialogListener {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_actions);

        dbHelper = new DatabaseHelper(this);

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка списка студентов
        loadStudents();

        // Кнопка для добавления студента
        Button buttonAddStudent = findViewById(R.id.buttonAddStudent);
        buttonAddStudent.setOnClickListener(v -> {
            AddStudentDialog dialog = new AddStudentDialog();
            dialog.show(getSupportFragmentManager(), "AddStudentDialog");
        });

        // Поля для фильтрации
        EditText editTextSurnameFilter = findViewById(R.id.editTextSurnameFilter);
        EditText editTextGroupFilter = findViewById(R.id.editTextGroupFilter);

        // Обработка изменений в поле фамилии
        editTextSurnameFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String surnameQuery = s.toString();
                String groupQuery = editTextGroupFilter.getText().toString();
                studentAdapter.filter(surnameQuery, groupQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Обработка изменений в поле группы
        editTextGroupFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String surnameQuery = editTextSurnameFilter.getText().toString();
                String groupQuery = s.toString();
                studentAdapter.filter(surnameQuery, groupQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadStudents() {
        List<Student> students = dbHelper.getAllStudents();
        if (students.isEmpty()) {
            Log.e("StudentActionsActivity", "Students list is empty");
        } else {
            Log.d("StudentActionsActivity", "Students list size: " + students.size());
        }
        studentAdapter = new StudentAdapter(students, this, dbHelper);
        recyclerView.setAdapter(studentAdapter);
    }

    @Override
    public void onEditClick(Student student) {
        // Открыть диалог редактирования студента
        EditStudentDialog dialog = new EditStudentDialog(student);
        dialog.show(getSupportFragmentManager(), "EditStudentDialog");
    }

    @Override
    public void onDeleteClick(Student student) {
        Log.d("StudentActionsActivity", "Deleting student: " + student.getName() + ", ID: " + student.getId());
        boolean isDeleted = dbHelper.deleteStudent(student.getId());
        if (isDeleted) {
            Log.d("StudentActionsActivity", "Student deleted successfully");
        } else {
            Log.e("StudentActionsActivity", "Failed to delete student");
        }
        loadStudents(); // Обновить список
    }

    @Override
    public void onStudentAdded(Student student) {
        loadStudents();

    }

    @Override
    public void onStudentUpdated(Student student) {
        // Обновить данные студента в базе данных
        boolean isUpdated = dbHelper.updateStudent(student);
        if (isUpdated) {
            Log.d("StudentActionsActivity", "Student updated successfully");
            loadStudents();
        } else {
            Log.e("StudentActionsActivity", "Failed to update student");
        }
    }
}