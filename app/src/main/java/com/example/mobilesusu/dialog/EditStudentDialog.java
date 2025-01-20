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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.mobilesusu.R;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Student;
import com.example.mobilesusu.utils.PasswordHasher;

import java.util.List;

public class EditStudentDialog extends DialogFragment {
    private Student student;
    private EditStudentDialogListener listener;
    private String selectedGroup; // Выбранная группа
    private DatabaseHelper dbHelper;

    public interface EditStudentDialogListener {
        void onStudentUpdated(Student student);
    }

    public EditStudentDialog(Student student) {
        this.student = student;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditStudentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditStudentDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_student, null);

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

        // Установка текущей группы студента в Spinner
        int groupId = student.getGroupId();
        String currentGroupName = dbHelper.getGroupNameById(groupId);
        if (currentGroupName != null) {
            int position = groups.indexOf(currentGroupName);
            if (position != -1) {
                spinnerGroup.setSelection(position);
            }
        }

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

        // Предзаполнение полей текущими данными студента
        editTextStudentName.setText(student.getName());
        editTextStudentSurname.setText(student.getSurname());
        editTextStudentEmail.setText(student.getEmail());
        editTextStudentLogin.setText(student.getLogin());
        editTextStudentPassword.setText("");

        // Обработка нажатия на кнопку "Сохранить"
        buttonSaveStudent.setOnClickListener(v -> {
            String name = editTextStudentName.getText().toString();
            String surname = editTextStudentSurname.getText().toString();
            String email = editTextStudentEmail.getText().toString();
            String login = editTextStudentLogin.getText().toString();
            String password = editTextStudentPassword.getText().toString();

            if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && !login.isEmpty() && selectedGroup != null) {
                Student tempStudent = new Student(student);
                tempStudent.setName(name);
                tempStudent.setSurname(surname);
                tempStudent.setEmail(email);
                tempStudent.setLogin(login);

                if (!password.isEmpty()) {
                    String hashedPassword = PasswordHasher.hashPassword(password);
                    tempStudent.setPassword(hashedPassword);
                }

                int newGroupId = dbHelper.getGroupIdByName(selectedGroup);
                tempStudent.setGroupId(newGroupId);

                try {
                    boolean isUpdated = dbHelper.updateStudent(tempStudent);
                    if (isUpdated) {
                        student.setName(name);
                        student.setSurname(surname);
                        student.setEmail(email);
                        student.setLogin(login);
                        if (!password.isEmpty()) {
                            student.setPassword(tempStudent.getPassword());
                        }
                        student.setGroupId(newGroupId);

                        listener.onStudentUpdated(student);
                        dismiss();
                    }
                } catch (IllegalArgumentException e) {
                    editTextStudentLogin.setError(e.getMessage());
                }
            } else {
                Log.e("EditStudentDialog", "Не все поля заполнены");
            }
        });

        builder.setView(view);
        return builder.create();
    }
}