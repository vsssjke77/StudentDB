package com.example.mobilesusu.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobilesusu.R;
import com.example.mobilesusu.auth.AuthManager;

public class MainActivity extends AppCompatActivity {
    private AuthManager authManager;
    private EditText editTextLogin, editTextPassword;
    private Button buttonLogin, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = AuthManager.getInstance(this);

        // Инициализация элементов интерфейса
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Обработка нажатия на кнопку входа
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();
                attemptLogin(login, password);
            }
        });

        // Обработка нажатия на кнопку регистрации
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);

                // Создание анимации перехода
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        MainActivity.this,
                        R.anim.slide_in_right,  // Анимация входа
                        R.anim.slide_out_left   // Анимация выхода
                );

                // Запуск активности с анимацией
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void attemptLogin(String login, String password) {
        if (login.isEmpty()) {
            Toast.makeText(MainActivity.this, "Введите логин", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isLoggedIn = authManager.login(login, password);
        if (isLoggedIn) {
            // Получаем роль текущего пользователя
            String role = authManager.getCurrentRole();
            Log.d("MainActivity", "Роль пользователя: " + role);

            if (role != null) {
                switch (role) {
                    case "admin": {
                        // Переход на экран администратора
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish(); // Закрыть текущую активность

                        break;
                    }
                    case "student": {
                        // Переход на экран студента
                        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                        startActivity(intent);
                        finish(); // Закрыть текущую активность

                        break;
                    }
                    case "teacher": {
                        // Переход на экран преподавателя
                        Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                        startActivity(intent);
                        finish(); // Закрыть текущую активность

                        break;
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "Ошибка: роль пользователя не определена", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
        }
    }
}