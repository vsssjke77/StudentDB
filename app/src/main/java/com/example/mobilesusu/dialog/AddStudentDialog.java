package com.example.mobilesusu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.mobilesusu.R;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Student;
import com.example.mobilesusu.utils.PasswordHasher;

import java.util.List;

public class AddStudentDialog extends DialogFragment {
    private AddStudentDialogListener listener;
    private String selectedGroup; // Выбранная группа
    private DatabaseHelper dbHelper;

    public interface AddStudentDialogListener {
        void onStudentAdded(Student student);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddStudentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddStudentDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_student, null);

        // Инициализация элементов
        EditText editTextStudentName = view.findViewById(R.id.editTextStudentName);
        EditText editTextStudentSurname = view.findViewById(R.id.editTextStudentSurname);
        EditText editTextStudentEmail = view.findViewById(R.id.editTextStudentEmail);
        EditText editTextStudentLogin = view.findViewById(R.id.editTextStudentLogin);
        EditText editTextStudentPassword = view.findViewById(R.id.editTextStudentPassword);
        Spinner spinnerGroup = view.findViewById(R.id.spinnerGroup);
        Button buttonSaveStudent = view.findViewById(R.id.buttonSaveStudent);

        dbHelper = new DatabaseHelper(getContext());

        // Загрузка списка групп в Spinner
        List<String> groups = dbHelper.getGroups();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                groups
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);

        // Обработка выбора группы
        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Обработка нажатия на кнопку "Сохранить"
        buttonSaveStudent.setOnClickListener(v -> {
            String name = editTextStudentName.getText().toString();
            String surname = editTextStudentSurname.getText().toString();
            String email = editTextStudentEmail.getText().toString();
            String login = editTextStudentLogin.getText().toString();
            String password = editTextStudentPassword.getText().toString();
            String hashedPassword = PasswordHasher.hashPassword(password);

            if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && !login.isEmpty() && !password.isEmpty() && selectedGroup != null) {
                int groupId = dbHelper.getGroupIdByName(selectedGroup);

                Student student = new Student(name, surname, email, login, hashedPassword, groupId);

                boolean result = dbHelper.addStudent(student);
                if (result) {
                    listener.onStudentAdded(student);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Логин уже используется", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("AddStudentDialog", "Не все поля заполнены");
            }
        });

        builder.setView(view);
        return builder.create();
    }
}