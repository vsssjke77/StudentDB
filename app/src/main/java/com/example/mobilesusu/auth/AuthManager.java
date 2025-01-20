package com.example.mobilesusu.auth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.utils.PasswordHasher;

public class AuthManager {
    private static AuthManager instance;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private String currentUsername;
    private String currentRole;

    // Приватный конструктор
    private AuthManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        if (database == null) {
            Log.e("AuthManager", "База данных не открыта");
        } else {
            Log.d("AuthManager", "База данных успешно открыта");
        }
    }

    // Метод для получения экземпляра AuthManager
    public static AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    // Метод для регистрации нового пользователя
    public Integer register(String username, String password, String role, int groupId, String firstName, String lastName, String email) {
        if (username == null || password == null || role == null || firstName == null || lastName == null || email == null) {
            Log.e("AuthManager", "Один или несколько параметров равны null");
            return 1;
        }

        // Проверка, существует ли пользователь с таким логином
        if (checkUserExists(username)) {
            Log.e("AuthManager", "Пользователь уже существует: " + username);
            return 2;
        }

        // Хэширование пароля
        String hashedPassword = PasswordHasher.hashPassword(password);

        // Добавление данных в таблицу STUDENT или TEACHER в зависимости от роли
        if ("student".equals(role)) {
            if (groupId == -1) {
                Log.e("AuthManager", "Группа не указана для студента");
                return 3;
            }

            ContentValues studentValues = new ContentValues();
            studentValues.put(DatabaseHelper.getColumnStudentName(), firstName);
            studentValues.put(DatabaseHelper.getColumnStudentSurname(), lastName);
            studentValues.put(DatabaseHelper.getColumnStudentGroupId(), groupId);
            studentValues.put(DatabaseHelper.getColumnStudentEmail(), email);
            studentValues.put(DatabaseHelper.getColumnStudentLogin(), username);
            studentValues.put(DatabaseHelper.getColumnStudentPassword(), hashedPassword);

            long studentResult = database.insert(DatabaseHelper.getTableStudent(), null, studentValues);
            if (studentResult == -1) {
                Log.e("AuthManager", "Не удалось зарегистрировать студента: " + username);
                return 4;
            }
        } else if ("teacher".equals(role)) {
            ContentValues teacherValues = new ContentValues();
            teacherValues.put(DatabaseHelper.getColumnTeacherName(), firstName);
            teacherValues.put(DatabaseHelper.getColumnTeacherSurname(), lastName);
            teacherValues.put(DatabaseHelper.getColumnTeacherEmail(), email);
            teacherValues.put(DatabaseHelper.getColumnTeacherLogin(), username);
            teacherValues.put(DatabaseHelper.getColumnTeacherPassword(), hashedPassword);

            long teacherResult = database.insert(DatabaseHelper.getTableTeacher(), null, teacherValues);
            if (teacherResult == -1) {
                Log.e("AuthManager", "Не удалось зарегистрировать преподавателя: " + username);
                return 5;
            }
        } else {
            Log.e("AuthManager", "Неизвестная роль: " + role);
            return 6;
        }

        return 0; // Регистрация успешна
    }

    // Метод для проверки существования пользователя
    private boolean checkUserExists(String username) {
        if (username == null) {
            return false;
        }

        // Проверка в таблице STUDENT
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableStudent(),
                new String[]{DatabaseHelper.getColumnStudentLogin()},
                DatabaseHelper.getColumnStudentLogin() + " = ?",
                new String[]{username},
                null, null, null
        )) {
            if (cursor.getCount() > 0) {
                return true;
            }
        }

        // Проверка в таблице TEACHER
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableTeacher(),
                new String[]{DatabaseHelper.getColumnTeacherLogin()},
                DatabaseHelper.getColumnTeacherLogin() + " = ?",
                new String[]{username},
                null, null, null
        )) {
            if (cursor.getCount() > 0) {
                return true;
            }
        }

        // Проверка в таблице ADMIN
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableAdmin(),
                new String[]{DatabaseHelper.getColumnAdminLogin()},
                DatabaseHelper.getColumnAdminLogin() + " = ?",
                new String[]{username},
                null, null, null
        )) {
            return cursor.getCount() > 0;
        }
    }

    // Метод для авторизации
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            Log.e("AuthManager", "Логин или пароль равны null");
            return false;
        }

        // Хэширование пароля
        String hashedPassword = PasswordHasher.hashPassword(password);

        // Поиск пользователя в таблице STUDENT
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableStudent(),
                new String[]{DatabaseHelper.getColumnStudentLogin(), DatabaseHelper.getColumnStudentPassword()},
                DatabaseHelper.getColumnStudentLogin() + " = ? AND " + DatabaseHelper.getColumnStudentPassword() + " = ?",
                new String[]{username, hashedPassword},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                currentUsername = username;
                currentRole = "student";
                return true;
            }
        }

        // Поиск пользователя в таблице TEACHER
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableTeacher(),
                new String[]{DatabaseHelper.getColumnTeacherLogin(), DatabaseHelper.getColumnTeacherPassword()},
                DatabaseHelper.getColumnTeacherLogin() + " = ? AND " + DatabaseHelper.getColumnTeacherPassword() + " = ?",
                new String[]{username, hashedPassword},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                currentUsername = username;
                currentRole = "teacher";
                return true;
            }
        }

        // Поиск пользователя в таблице ADMIN
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableAdmin(),
                new String[]{DatabaseHelper.getColumnAdminLogin(), DatabaseHelper.getColumnAdminPassword()},
                DatabaseHelper.getColumnAdminLogin() + " = ? AND " + DatabaseHelper.getColumnAdminPassword() + " = ?",
                new String[]{username, hashedPassword},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                currentUsername = username;
                currentRole = "admin";
                return true;
            }
        }

        return false;
    }

    // Метод для получения логина текущего пользователя
    public String getCurrentUsername() {
        return currentUsername;
    }

    // Метод для получения роли текущего пользователя
    public String getCurrentRole() {
        return currentRole;
    }

    // Метод для проверки, является ли пользователь администратором
    public boolean isAdmin() {
        return currentRole != null && "admin".equals(currentRole);
    }

    // Метод для получения роли пользователя
    public String getCurrentRole(String username) {
        if (username == null) {
            return null;
        }

        // Поиск в таблице STUDENT
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableStudent(),
                new String[]{DatabaseHelper.getColumnStudentLogin()},
                DatabaseHelper.getColumnStudentLogin() + " = ?",
                new String[]{username},
                null, null, null
        )) {
            if (cursor.getCount() > 0) {
                return "student";
            }
        }

        // Поиск в таблице TEACHER
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableTeacher(),
                new String[]{DatabaseHelper.getColumnTeacherLogin()},
                DatabaseHelper.getColumnTeacherLogin() + " = ?",
                new String[]{username},
                null, null, null
        )) {
            if (cursor.getCount() > 0) {
                return "teacher";
            }
        }

        // Поиск в таблице ADMIN
        try (Cursor cursor = database.query(
                DatabaseHelper.getTableAdmin(),
                new String[]{DatabaseHelper.getColumnAdminLogin()},
                DatabaseHelper.getColumnAdminLogin() + " = ?",
                new String[]{username},
                null, null, null
        )) {
            if (cursor.getCount() > 0) {
                return "admin";
            }
        }

        return null;
    }
}