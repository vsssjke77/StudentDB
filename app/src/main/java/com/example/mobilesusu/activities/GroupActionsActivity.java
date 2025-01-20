package com.example.mobilesusu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilesusu.R;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.dialog.AddGroupDialog;
import com.example.mobilesusu.dialog.EditGroupDialog;
import com.example.mobilesusu.utils.GroupAdapter;

import java.util.List;

public class GroupActionsActivity extends AppCompatActivity implements GroupAdapter.OnGroupClickListener, AddGroupDialog.AddGroupDialogListener, EditGroupDialog.EditGroupDialogListener {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_actions);

        dbHelper = new DatabaseHelper(this);

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerViewGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка списка групп
        loadGroups();

        // Кнопка для добавления группы
        Button buttonAddGroup = findViewById(R.id.buttonAddGroup);
        buttonAddGroup.setOnClickListener(v -> {
            // Открыть диалог добавления группы
            AddGroupDialog dialog = new AddGroupDialog();
            dialog.show(getSupportFragmentManager(), "AddGroupDialog");
        });
    }

    private void loadGroups() {
        List<String> groups = dbHelper.getGroups();
        if (groups.isEmpty()) {
            Log.e("GroupActionsActivity", "Groups list is empty");
        } else {
            Log.d("GroupActionsActivity", "Groups list size: " + groups.size());
        }
        groupAdapter = new GroupAdapter(groups, this);
        recyclerView.setAdapter(groupAdapter);
    }

    @Override
    public void onEditClick(String groupName) {
        // Открыть диалог редактирования группы
        EditGroupDialog dialog = new EditGroupDialog(groupName);
        dialog.show(getSupportFragmentManager(), "EditGroupDialog");
    }



    @Override
    public void onDeleteClick(String groupName) {
        Log.d("GroupActionsActivity", "Deleting group: " + groupName);

        // Проверяем, есть ли студенты в группе
        if (dbHelper.hasStudentsInGroup(groupName)) {
            // Показываем диалог подтверждения
            showDeleteGroupConfirmation(groupName);
        } else {
            // Если студентов нет, просто удаляем группу
            dbHelper.deleteGroup(groupName);
            loadGroups(); // Обновляем список групп
        }
    }

    private void showDeleteGroupConfirmation(String groupName) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление группы")
                .setMessage("В этой группе есть студенты. Удалить группу вместе со студентами?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Удаляем группу и студентов
                    dbHelper.deleteGroupWithStudents(groupName);
                    loadGroups(); // Обновляем список групп
                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    // Ничего не делаем
                })
                .setNeutralButton("Отмена", null)
                .show();
    }

    @Override
    public boolean onGroupAdded(String groupName) {
        // Добавляем группу в базу данных
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        try {
            boolean isAdded = dbHelper.addGroup(groupName);

            if (isAdded) {
                Log.d("GroupActionsActivity", "Группа добавлена: " + groupName);
                loadGroups(); // Обновляем список групп
                return true; // Успешно добавлено
            } else {
                Log.e("GroupActionsActivity", "Ошибка при добавлении группы");
                Toast.makeText(this, "Ошибка при добавлении группы", Toast.LENGTH_SHORT).show();
                return false; // Не удалось добавить
            }
        } catch (IllegalArgumentException e) {
            // Ловим исключение, если группа с таким названием уже существует
            Log.e("GroupActionsActivity", "Ошибка: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false; // Группа уже существует
        }
    }

    @Override
    public void onGroupUpdated(String oldGroupName, String newGroupName) {
        // Обновляем интерфейс (например, список групп)
        loadGroups();
    }
}