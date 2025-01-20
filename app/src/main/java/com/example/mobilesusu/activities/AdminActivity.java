package com.example.mobilesusu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobilesusu.R;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Инициализация кнопок
        Button buttonTeacherActions = findViewById(R.id.buttonTeacherActions);
        Button buttonGroupActions = findViewById(R.id.buttonGroupActions);
        Button buttonStudentActions = findViewById(R.id.buttonStudentActions);

        // Обработка нажатия на кнопку "Действия с преподавателями"
        buttonTeacherActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, TeacherActionsActivity.class);
                startActivity(intent);
            }
        });

        // Обработка нажатия на кнопку "Действия с группами"
        buttonGroupActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, GroupActionsActivity.class);
                startActivity(intent);
            }
        });

        // Обработка нажатия на кнопку "Действия со студентами"
        buttonStudentActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, StudentActionsActivity.class);
                startActivity(intent);
            }
        });
    }
}