package com.example.mobilesusu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobilesusu.R;
import com.example.mobilesusu.auth.AuthManager;
import com.example.mobilesusu.database.DatabaseHelper;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {
    private Spinner spinnerGroup;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextLogin, editTextPassword;
    private Button buttonRegister;
    private AuthManager authManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        authManager = AuthManager.getInstance(this);
        dbHelper = new DatabaseHelper(this);

        // Инициализация элементов интерфейса
        spinnerGroup = findViewById(R.id.spinnerGroup);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Загрузка списка групп в Spinner
        loadGroups();

        // Обработка нажатия на кнопку регистрации
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = spinnerGroup.getSelectedItem().toString();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();
                String login = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();

                if (firstName.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Введите имя", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lastName.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Введите фамилию", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty() || !isValidEmail(email)) {
                    Toast.makeText(RegistrationActivity.this, "Введите корректный email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (login.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Введите логин", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Получаем ID группы по её названию
                int groupId = dbHelper.getGroupIdByName(groupName);
                if (groupId == -1) {
                    Toast.makeText(RegistrationActivity.this, "Группа не найдена", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Регистрация нового студента с передачей ID группы
                Integer isRegistered = authManager.register(login, password, "student", groupId, firstName, lastName, email);
                if (isRegistered == 0) {
                    Toast.makeText(RegistrationActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (isRegistered == 1) {
                    Toast.makeText(RegistrationActivity.this, "Один или несколько параметров равны null", Toast.LENGTH_SHORT).show();
                } else if (isRegistered == 2) {
                    Toast.makeText(RegistrationActivity.this, "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
                } else if (isRegistered == 3) {
                    Toast.makeText(RegistrationActivity.this, "Группа не указана для студента", Toast.LENGTH_SHORT).show();
                } else if (isRegistered == 4) {
                    Toast.makeText(RegistrationActivity.this, "Не удалось зарегистрировать студента", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Загрузка списка групп из базы данных
    private void loadGroups() {
        List<String> groups = dbHelper.getGroups();

        String[] groupsArray = groups.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);
    }

    // Проверка валидности email
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}