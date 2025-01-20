package com.example.mobilesusu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.mobilesusu.R;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Teacher;
import com.example.mobilesusu.utils.PasswordHasher;

public class EditTeacherDialog extends DialogFragment {
    private Teacher teacher;
    private EditTeacherDialogListener listener;
    private DatabaseHelper dbHelper;

    public interface EditTeacherDialogListener {
        void onTeacherUpdated(Teacher teacher);
    }

    public EditTeacherDialog(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditTeacherDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditTeacherDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_teacher, null);

        dbHelper = new DatabaseHelper(getContext());

        // Инициализация элементов интерфейса
        EditText editTextTeacherName = view.findViewById(R.id.editTextTeacherName);
        EditText editTextTeacherSurname = view.findViewById(R.id.editTextTeacherSurname);
        EditText editTextTeacherEmail = view.findViewById(R.id.editTextTeacherEmail);
        EditText editTextTeacherLogin = view.findViewById(R.id.editTextTeacherLogin);
        EditText editTextTeacherPassword = view.findViewById(R.id.editTextTeacherPassword);

        Button buttonSaveTeacher = view.findViewById(R.id.buttonSaveTeacher);

        // Предзаполнение полей текущими данными учителя
        editTextTeacherName.setText(teacher.getName());
        editTextTeacherSurname.setText(teacher.getSurname());
        editTextTeacherEmail.setText(teacher.getEmail());
        editTextTeacherLogin.setText(teacher.getLogin());
        editTextTeacherPassword.setText("");

        // Обработка нажатия на кнопку "Сохранить"
        buttonSaveTeacher.setOnClickListener(v -> {
            String name = editTextTeacherName.getText().toString();
            String surname = editTextTeacherSurname.getText().toString();
            String email = editTextTeacherEmail.getText().toString();
            String login = editTextTeacherLogin.getText().toString();
            String password = editTextTeacherPassword.getText().toString();

            Teacher tempTeacher = new Teacher(teacher);
            tempTeacher.setName(name);
            tempTeacher.setSurname(surname);
            tempTeacher.setEmail(email);
            tempTeacher.setLogin(login);

            if (!password.isEmpty()) {
                String hashedPassword = PasswordHasher.hashPassword(password);
                tempTeacher.setPassword(hashedPassword);
            }

            try {
                boolean isUpdated = dbHelper.updateTeacher(tempTeacher);
                if (isUpdated) {
                    teacher.setName(name);
                    teacher.setSurname(surname);
                    teacher.setEmail(email);
                    teacher.setLogin(login);
                    if (!password.isEmpty()) {
                        teacher.setPassword(tempTeacher.getPassword());
                    }

                    listener.onTeacherUpdated(teacher);
                    dismiss();
                }
            } catch (IllegalArgumentException e) {
                editTextTeacherLogin.setError(e.getMessage());
            }
        });

        builder.setView(view);
        return builder.create();
    }
}