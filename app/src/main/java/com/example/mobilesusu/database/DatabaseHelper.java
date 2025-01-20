package com.example.mobilesusu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mobilesusu.models.Student;
import com.example.mobilesusu.models.Teacher;
import com.example.mobilesusu.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "students.db";
    private static final int DATABASE_VERSION = 2;

    // Таблица TEACHER
    private static final String TABLE_TEACHER = "teacher";
    private static final String COLUMN_TEACHER_ID = "id";
    private static final String COLUMN_TEACHER_NAME = "name";
    private static final String COLUMN_TEACHER_SURNAME = "surname";
    private static final String COLUMN_TEACHER_EMAIL = "email";
    private static final String COLUMN_TEACHER_LOGIN = "login";
    private static final String COLUMN_TEACHER_PASSWORD = "password";

    // Таблица STUDENT
    private static final String TABLE_STUDENT = "student";
    private static final String COLUMN_STUDENT_ID = "id";
    private static final String COLUMN_STUDENT_GROUP_ID = "group_id";
    private static final String COLUMN_STUDENT_NAME = "name";
    private static final String COLUMN_STUDENT_SURNAME = "surname";
    private static final String COLUMN_STUDENT_EMAIL = "email";
    private static final String COLUMN_STUDENT_LOGIN = "login";
    private static final String COLUMN_STUDENT_PASSWORD = "password";

    // Таблица GROUPS
    private static final String TABLE_GROUPS = "groups";
    private static final String COLUMN_GROUP_ID = "id";
    private static final String COLUMN_GROUP_NAME = "name";

    // Таблица ADMIN
    private static final String TABLE_ADMIN = "admin";
    private static final String COLUMN_ADMIN_ID = "id";
    private static final String COLUMN_ADMIN_LOGIN = "login";
    private static final String COLUMN_ADMIN_PASSWORD = "password";
    private static final String COLUMN_ADMIN_EMAIL = "email";

    // Конструктор
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы TEACHER
        String createTeacherTable = "CREATE TABLE " + TABLE_TEACHER + "("
                + COLUMN_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TEACHER_NAME + " TEXT,"
                + COLUMN_TEACHER_SURNAME + " TEXT,"
                + COLUMN_TEACHER_EMAIL + " TEXT,"
                + COLUMN_TEACHER_LOGIN + " TEXT UNIQUE,"
                + COLUMN_TEACHER_PASSWORD + " TEXT"
                + ")";
        db.execSQL(createTeacherTable);

        // Создание таблицы STUDENT
        String createStudentTable = "CREATE TABLE " + TABLE_STUDENT + "("
                + COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STUDENT_GROUP_ID + " INTEGER,"
                + COLUMN_STUDENT_NAME + " TEXT,"
                + COLUMN_STUDENT_SURNAME + " TEXT,"
                + COLUMN_STUDENT_EMAIL + " TEXT,"
                + COLUMN_STUDENT_LOGIN + " TEXT UNIQUE,"
                + COLUMN_STUDENT_PASSWORD + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_STUDENT_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + ")"
                + ")";
        db.execSQL(createStudentTable);

        // Создание таблицы GROUPS
        String createGroupsTable = "CREATE TABLE " + TABLE_GROUPS + "("
                + COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GROUP_NAME + " TEXT UNIQUE"
                + ")";
        db.execSQL(createGroupsTable);

        // Создание таблицы ADMIN
        String createAdminTable = "CREATE TABLE " + TABLE_ADMIN + "("
                + COLUMN_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ADMIN_LOGIN + " TEXT UNIQUE,"
                + COLUMN_ADMIN_PASSWORD + " TEXT,"
                + COLUMN_ADMIN_EMAIL + " TEXT"
                + ")";
        db.execSQL(createAdminTable);

        // Добавление тестовых данных (опционально)
        db.execSQL("INSERT INTO " + TABLE_GROUPS + " (" + COLUMN_GROUP_NAME + ") VALUES ('Группа 1')");
        db.execSQL("INSERT INTO " + TABLE_GROUPS + " (" + COLUMN_GROUP_NAME + ") VALUES ('Группа 2')");

        // Добавление администратора
        addAdminUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаляем старые таблицы, если они существуют
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);

        // Создаем таблицы заново
        onCreate(db);
    }

    // Метод для добавления администратора
    private void addAdminUser(SQLiteDatabase db) {
        String adminLogin = "admin";
        String adminPassword = PasswordHasher.hashPassword("admroot");
        String adminEmail = "admin@example.com";

        // Проверяем, существует ли уже администратор
        Cursor cursor = db.query(TABLE_ADMIN,
                new String[]{COLUMN_ADMIN_LOGIN},
                COLUMN_ADMIN_LOGIN + " = ?",
                new String[]{adminLogin},
                null, null, null);

        if (cursor.getCount() == 0) {
            // Администратора нет, добавляем его
            ContentValues values = new ContentValues();
            values.put(COLUMN_ADMIN_LOGIN, adminLogin);
            values.put(COLUMN_ADMIN_PASSWORD, adminPassword);
            values.put(COLUMN_ADMIN_EMAIL, adminEmail);

            db.insert(TABLE_ADMIN, null, values);
            Log.d("DatabaseHelper", "Администратор зарегистрирован");
        } else {
            Log.d("DatabaseHelper", "Администратор уже существует");
        }

        cursor.close();
    }

    // Геттеры для таблиц и столбцов

    public static String getTableTeacher() {
        return TABLE_TEACHER;
    }

    public static String getColumnTeacherId() {
        return COLUMN_TEACHER_ID;
    }

    public static String getColumnTeacherName() {
        return COLUMN_TEACHER_NAME;
    }

    public static String getColumnTeacherSurname() {
        return COLUMN_TEACHER_SURNAME;
    }

    public static String getColumnTeacherEmail() {
        return COLUMN_TEACHER_EMAIL;
    }

    public static String getColumnTeacherLogin() {
        return COLUMN_TEACHER_LOGIN;
    }

    public static String getColumnTeacherPassword() {
        return COLUMN_TEACHER_PASSWORD;
    }

    public static String getTableStudent() {
        return TABLE_STUDENT;
    }

    public static String getColumnStudentId() {
        return COLUMN_STUDENT_ID;
    }

    public static String getColumnStudentGroupId() {
        return COLUMN_STUDENT_GROUP_ID;
    }

    public static String getColumnStudentName() {
        return COLUMN_STUDENT_NAME;
    }

    public static String getColumnStudentSurname() {
        return COLUMN_STUDENT_SURNAME;
    }

    public static String getColumnStudentEmail() {
        return COLUMN_STUDENT_EMAIL;
    }

    public static String getColumnStudentLogin() {
        return COLUMN_STUDENT_LOGIN;
    }

    public static String getColumnStudentPassword() {
        return COLUMN_STUDENT_PASSWORD;
    }

    public static String getTableGroups() {
        return TABLE_GROUPS;
    }

    public static String getColumnGroupId() {
        return COLUMN_GROUP_ID;
    }

    public static String getColumnGroupName() {
        return COLUMN_GROUP_NAME;
    }

    public static String getTableAdmin() {
        return TABLE_ADMIN;
    }

    public static String getColumnAdminId() {
        return COLUMN_ADMIN_ID;
    }

    public static String getColumnAdminLogin() {
        return COLUMN_ADMIN_LOGIN;
    }

    public static String getColumnAdminPassword() {
        return COLUMN_ADMIN_PASSWORD;
    }

    public static String getColumnAdminEmail() {
        return COLUMN_ADMIN_EMAIL;
    }

    public boolean isLoginUnique(String login) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Проверка в таблице STUDENT
        Cursor studentCursor = db.query(
                TABLE_STUDENT,
                new String[]{COLUMN_STUDENT_LOGIN},
                COLUMN_STUDENT_LOGIN + " = ?",
                new String[]{login},
                null, null, null
        );

        // Проверка в таблице TEACHER
        Cursor teacherCursor = db.query(
                TABLE_TEACHER,
                new String[]{COLUMN_TEACHER_LOGIN},
                COLUMN_TEACHER_LOGIN + " = ?",
                new String[]{login},
                null, null, null
        );

        // Проверка в таблице ADMIN
        Cursor adminCursor = db.query(
                TABLE_ADMIN,
                new String[]{COLUMN_ADMIN_LOGIN},
                COLUMN_ADMIN_LOGIN + " = ?",
                new String[]{login},
                null, null, null
        );

        // Если логин найден в любой из таблиц, возвращаем false
        boolean isUnique = studentCursor.getCount() == 0 && teacherCursor.getCount() == 0 && adminCursor.getCount() == 0;

        // Закрываем курсоры
        studentCursor.close();
        teacherCursor.close();
        adminCursor.close();

        return isUnique;
    }

    public boolean isLoginUniqueForUpdate(String login, int userId, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Проверка в таблице TEACHER
        Cursor teacherCursor;
        if (Objects.equals(tableName, TABLE_TEACHER)) {
            teacherCursor = db.query(
                    TABLE_TEACHER,
                    new String[]{COLUMN_TEACHER_LOGIN},
                    COLUMN_TEACHER_LOGIN + " = ? AND " + COLUMN_TEACHER_ID + " != ?",
                    new String[]{login, String.valueOf(userId)},
                    null, null, null
            );
        } else {
            teacherCursor = db.query(
                    TABLE_TEACHER,
                    new String[]{COLUMN_TEACHER_LOGIN},
                    COLUMN_TEACHER_LOGIN + " = ?",
                    new String[]{login},
                    null, null, null
            );
        }

        // Проверка в таблице STUDENT
        Cursor studentCursor;
        if (Objects.equals(tableName, TABLE_STUDENT)) {
            studentCursor = db.query(
                    TABLE_STUDENT,
                    new String[]{COLUMN_STUDENT_LOGIN},
                    COLUMN_STUDENT_LOGIN + " = ? AND " + COLUMN_STUDENT_ID + " != ?",
                    new String[]{login, String.valueOf(userId)},
                    null, null, null
            );
        } else {
            studentCursor = db.query(
                    TABLE_STUDENT,
                    new String[]{COLUMN_STUDENT_LOGIN},
                    COLUMN_STUDENT_LOGIN + " = ?",
                    new String[]{login},
                    null, null, null
            );
        }

        // Проверка в таблице ADMIN
        Cursor adminCursor = db.query(
                TABLE_ADMIN,
                new String[]{COLUMN_ADMIN_LOGIN},
                COLUMN_ADMIN_LOGIN + " = ?",
                new String[]{login},
                null, null, null
        );

        boolean isUnique = teacherCursor.getCount() == 0 && studentCursor.getCount() == 0 && adminCursor.getCount() == 0;

        teacherCursor.close();
        studentCursor.close();
        adminCursor.close();

        return isUnique;
    }

    // Метод для получения списка групп
    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_GROUPS,
                    new String[]{COLUMN_GROUP_NAME},
                    null, null, null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnIndex = cursor.getColumnIndex(COLUMN_GROUP_NAME);
                    if (columnIndex != -1) {
                        groups.add(cursor.getString(columnIndex));
                    } else {
                        Log.e("DatabaseHelper", "Column " + COLUMN_GROUP_NAME + " not found in table " + TABLE_GROUPS);
                        return new ArrayList<>();
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while fetching groups", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return groups;
    }

    public boolean isGroupNameUniqueForUpdate(String groupName, int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Проверяем, существует ли группа с таким названием (исключая текущую группу)
        Cursor cursor = db.query(
                TABLE_GROUPS,
                new String[]{COLUMN_GROUP_NAME},
                COLUMN_GROUP_NAME + " = ? AND " + COLUMN_GROUP_ID + " != ?",
                new String[]{groupName, String.valueOf(groupId)},
                null, null, null
        );

        boolean isUnique = cursor.getCount() == 0;
        cursor.close();
        return isUnique;
    }

    public int getGroupIdByName(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int groupId = -1;

        Cursor cursor = db.query(
                TABLE_GROUPS,
                new String[]{COLUMN_GROUP_ID},
                COLUMN_GROUP_NAME + " = ?",
                new String[]{groupName},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            int groupIdIndex = cursor.getColumnIndex(COLUMN_GROUP_ID);
            if (groupIdIndex != -1) {
                groupId = cursor.getInt(groupIdIndex);
            } else {
                Log.e("DatabaseHelper", "Столбец COLUMN_GROUP_ID не найден");
            }
        }

        cursor.close();

        return groupId;
    }

    public String getGroupNameById(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String groupName = null;

        Cursor cursor = db.query(
                TABLE_GROUPS,
                new String[]{COLUMN_GROUP_NAME},
                COLUMN_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            int groupNameIndex = cursor.getColumnIndex(COLUMN_GROUP_NAME);
            if (groupNameIndex != -1) {
                groupName = cursor.getString(groupNameIndex);
            } else {
                Log.e("DatabaseHelper", "Столбец COLUMN_GROUP_NAME не найден");
            }
        }

        cursor.close();
        db.close();
        return groupName != null ? groupName : "Группа не указана";
    }

    // Метод для добавления группы
    public boolean addGroup(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                TABLE_GROUPS,
                new String[]{COLUMN_GROUP_NAME},
                COLUMN_GROUP_NAME + " = ?",
                new String[]{groupName},
                null, null, null
        );

        if (cursor.getCount() > 0) {
            cursor.close();
            throw new IllegalArgumentException("Группа с таким названием уже существует");
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, groupName);

        long result = db.insert(TABLE_GROUPS, null, values);
        return result != -1;
    }

    // Метод для обновления группы
    public void updateGroup(String oldGroupName, String newGroupName) {
        SQLiteDatabase db = this.getWritableDatabase();

        int groupId = getGroupIdByName(oldGroupName);
        if (groupId == -1) {
            throw new IllegalArgumentException("Группа не найдена");
        }

        if (!isGroupNameUniqueForUpdate(newGroupName, groupId)) {
            throw new IllegalArgumentException("Группа с таким названием уже существует");
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, newGroupName);
        db.update(TABLE_GROUPS, values, COLUMN_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});
        db.close();
    }

    public boolean hasStudentsInGroup(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int groupId = getGroupIdByName(groupName);

        if (groupId == -1) {
            return false;
        }

        Cursor cursor = db.query(
                TABLE_STUDENT,
                new String[]{COLUMN_STUDENT_ID},
                COLUMN_STUDENT_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)},
                null, null, null
        );

        boolean hasStudents = cursor.getCount() > 0;
        cursor.close();
        return hasStudents;
    }

    public void deleteGroupWithStudents(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int groupId = getGroupIdByName(groupName);

        if (groupId != -1) {
            try {
                db.beginTransaction();


                db.delete(TABLE_STUDENT, COLUMN_STUDENT_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});

                db.delete(TABLE_GROUPS, COLUMN_GROUP_NAME + " = ?", new String[]{groupName});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        }
    }

    // Метод для удаления группы
    public void deleteGroup(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int groupId = getGroupIdByName(groupName);

        if (groupId != -1) {
            db.delete(TABLE_GROUPS, COLUMN_GROUP_NAME + " = ?", new String[]{groupName});
        }

    }


    public List<Student> getStudentsByGroup(String groupId) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_STUDENT,
                new String[]{COLUMN_STUDENT_NAME, COLUMN_STUDENT_SURNAME, COLUMN_STUDENT_EMAIL},
                COLUMN_STUDENT_GROUP_ID + " = ?",
                new String[]{groupId},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(COLUMN_STUDENT_NAME);
            int surnameIndex = cursor.getColumnIndex(COLUMN_STUDENT_SURNAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_STUDENT_EMAIL);

            if (nameIndex != -1 && surnameIndex != -1 && emailIndex != -1) {
                do {
                    String name = cursor.getString(nameIndex);
                    String surname = cursor.getString(surnameIndex);
                    String email = cursor.getString(emailIndex);
                    students.add(new Student(name, surname, email));
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "Один из столбцов не найден в результате запроса");
            }
        }

        cursor.close();
        db.close();
        return students;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENT, null);

        if (cursor.moveToFirst()) {
            do {
                // Проверка наличия столбцов
                int idIndex = cursor.getColumnIndex(COLUMN_STUDENT_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_STUDENT_NAME);
                int surnameIndex = cursor.getColumnIndex(COLUMN_STUDENT_SURNAME);
                int emailIndex = cursor.getColumnIndex(COLUMN_STUDENT_EMAIL);
                int loginIndex = cursor.getColumnIndex(COLUMN_STUDENT_LOGIN);
                int passwordIndex = cursor.getColumnIndex(COLUMN_STUDENT_PASSWORD);
                int groupIdIndex = cursor.getColumnIndex(COLUMN_STUDENT_GROUP_ID);

                // Если какой-то столбец не найден, выбрасываем исключение или логируем ошибку
                if (idIndex == -1 || nameIndex == -1 || surnameIndex == -1 || emailIndex == -1 || loginIndex == -1 || passwordIndex == -1 || groupIdIndex == -1) {
                    Log.e("DatabaseHelper", "Один или несколько столбцов не найдены в таблице STUDENT");
                    throw new IllegalStateException("Один или несколько столбцов не найдены в таблице STUDENT");
                }

                // Извлечение данных
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String surname = cursor.getString(surnameIndex);
                String email = cursor.getString(emailIndex);
                String login = cursor.getString(loginIndex);
                String password = cursor.getString(passwordIndex);
                int groupId = cursor.getInt(groupIdIndex);

                // Создание объекта Student
                Student student = new Student(name, surname, email, login, password, groupId);
                student.setId(id); // Установка id
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return students;
    }

    public boolean updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!isLoginUniqueForUpdate(student.getLogin(), student.getId(), TABLE_STUDENT)) {
            throw new IllegalArgumentException("Логин уже используется другим пользователем");
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, student.getName());
        values.put(COLUMN_STUDENT_SURNAME, student.getSurname());
        values.put(COLUMN_STUDENT_EMAIL, student.getEmail());
        values.put(COLUMN_STUDENT_LOGIN, student.getLogin());
        values.put(COLUMN_STUDENT_GROUP_ID, student.getGroupId());

        if (!student.getPassword().isEmpty()) {
            values.put(COLUMN_STUDENT_PASSWORD, student.getPassword());
        }

        int rowsUpdated = db.update(TABLE_STUDENT, values, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(student.getId())});
        db.close();
        return rowsUpdated > 0;
    }

    public boolean deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STUDENT, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)}) > 0;
    }


    public boolean addStudent(Student student) {
        if (!isLoginUnique(student.getLogin())) {
            Log.e("DatabaseHelper", "Логин уже используется");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, student.getName());
        values.put(COLUMN_STUDENT_SURNAME, student.getSurname());
        values.put(COLUMN_STUDENT_EMAIL, student.getEmail());
        values.put(COLUMN_STUDENT_LOGIN, student.getLogin());
        values.put(COLUMN_STUDENT_PASSWORD, student.getPassword());
        values.put(COLUMN_STUDENT_GROUP_ID, student.getGroupId());

        long result = db.insert(TABLE_STUDENT, null, values);
        db.close();
        return result != -1;
    }

    public boolean addTeacher(String name, String surname, String email, String login, String password) {
        if (!isLoginUnique(login)) {
            Log.e("DatabaseHelper", "Логин уже используется");
            return false; // Логин не уникальный, возвращаем false
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, name);
        values.put(COLUMN_TEACHER_SURNAME, surname);
        values.put(COLUMN_TEACHER_EMAIL, email);
        values.put(COLUMN_TEACHER_LOGIN, login);
        values.put(COLUMN_TEACHER_PASSWORD, password);

        long result = db.insert(TABLE_TEACHER, null, values);
        db.close();
        return result != -1;
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHER, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_TEACHER_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_TEACHER_NAME);
                int surnameIndex = cursor.getColumnIndex(COLUMN_TEACHER_SURNAME);
                int emailIndex = cursor.getColumnIndex(COLUMN_TEACHER_EMAIL);
                int loginIndex = cursor.getColumnIndex(COLUMN_TEACHER_LOGIN);
                int passwordIndex = cursor.getColumnIndex(COLUMN_TEACHER_PASSWORD);

                if (idIndex == -1 || nameIndex == -1 || surnameIndex == -1 || emailIndex == -1 || loginIndex == -1 || passwordIndex == -1) {
                    Log.e("DatabaseHelper", "Один или несколько столбцов не найдены в таблице TEACHER");
                    throw new IllegalStateException("Один или несколько столбцов не найдены в таблице TEACHER");
                }

                // Извлечение данных
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String surname = cursor.getString(surnameIndex);
                String email = cursor.getString(emailIndex);
                String login = cursor.getString(loginIndex);
                String password = cursor.getString(passwordIndex);

                Teacher teacher = new Teacher(name, surname, email, login, password);
                teacher.setId(id);
                teachers.add(teacher);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return teachers;
    }

    public boolean updateTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!isLoginUniqueForUpdate(teacher.getLogin(), teacher.getId(), TABLE_TEACHER)) {
            throw new IllegalArgumentException("Логин уже используется другим пользователем");
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, teacher.getName());
        values.put(COLUMN_TEACHER_SURNAME, teacher.getSurname());
        values.put(COLUMN_TEACHER_EMAIL, teacher.getEmail());
        values.put(COLUMN_TEACHER_LOGIN, teacher.getLogin());

        if (!teacher.getPassword().isEmpty()) {
            values.put(COLUMN_TEACHER_PASSWORD, teacher.getPassword());
        }

        int rowsUpdated = db.update(TABLE_TEACHER, values, COLUMN_TEACHER_ID + " = ?", new String[]{String.valueOf(teacher.getId())});
        db.close();
        return rowsUpdated > 0;
    }

    public boolean deleteTeacher(int teacherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TEACHER, COLUMN_TEACHER_ID + " = ?", new String[]{String.valueOf(teacherId)}) > 0;
    }


}