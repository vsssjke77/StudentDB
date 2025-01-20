package com.example.mobilesusu.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;
import com.example.mobilesusu.auth.AuthManager;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Student;
import com.example.mobilesusu.utils.StudentAdapterForStudent;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapterForStudent studentAdapterForStudent;
    private DatabaseHelper dbHelper;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Инициализация
        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        authManager = AuthManager.getInstance(this);

        // Получаем данные текущего пользователя
        String currentUsername = authManager.getCurrentUsername();
        String groupId = getGroupIdByUsername(currentUsername);

        // Получаем список студентов в группе
        List<Student> students = dbHelper.getStudentsByGroup(groupId);

        // Настраиваем адаптер
        studentAdapterForStudent = new StudentAdapterForStudent(students);
        recyclerView.setAdapter(studentAdapterForStudent);
    }

    // Метод для получения groupId по username
    private String getGroupIdByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String groupId = null;

        Cursor cursor = db.query(
                DatabaseHelper.getTableStudent(),
                new String[]{DatabaseHelper.getColumnStudentGroupId()},
                DatabaseHelper.getColumnStudentLogin() + " = ?",
                new String[]{username},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            int groupIdIndex = cursor.getColumnIndex(DatabaseHelper.getColumnStudentGroupId());
            if (groupIdIndex != -1) {
                groupId = cursor.getString(groupIdIndex);
            } else {
                Log.e("StudentActivity", "Столбец COLUMN_STUDENT_GROUP_ID не найден");
            }
        }

        cursor.close();
        db.close();
        return groupId;
    }
}