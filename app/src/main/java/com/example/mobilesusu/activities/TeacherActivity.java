package com.example.mobilesusu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobilesusu.R;

public class TeacherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // Инициализация кнопок
        Button buttonGroupActions = findViewById(R.id.buttonGroupActions);
        Button buttonStudentActions = findViewById(R.id.buttonStudentActions);

        // Обработка нажатия на кнопку "Действия с группами"
        buttonGroupActions.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, GroupActionsActivity.class);
            startActivity(intent);
        });

        // Обработка нажатия на кнопку "Действия со студентами"
        buttonStudentActions.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, StudentActionsActivity.class);
            startActivity(intent);
        });
    }
}