package com.example.mobilesusu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.mobilesusu.R;
import com.example.mobilesusu.database.DatabaseHelper;
import com.example.mobilesusu.models.Teacher;
import com.example.mobilesusu.utils.PasswordHasher;

public class AddTeacherDialog extends DialogFragment {
    private DatabaseHelper dbHelper;

    public interface AddTeacherDialogListener {
        void onTeacherAdded(Teacher teacher);
    }

    private AddTeacherDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddTeacherDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddTeacherDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_teacher, null);

        // Инициализация элементов интерфейса
        EditText editTextTeacherName = view.findViewById(R.id.editTextTeacherName);
        EditText editTextTeacherSurname = view.findViewById(R.id.editTextTeacherSurname);
        EditText editTextTeacherEmail = view.findViewById(R.id.editTextTeacherEmail);
        EditText editTextTeacherLogin = view.findViewById(R.id.editTextTeacherLogin);
        EditText editTextTeacherPassword = view.findViewById(R.id.editTextTeacherPassword);
        Button buttonSaveTeacher = view.findViewById(R.id.buttonSaveTeacher);

        dbHelper = new DatabaseHelper(getActivity());

        // Обработка нажатия на кнопку "Сохранить"
        buttonSaveTeacher.setOnClickListener(v -> {
            String name = editTextTeacherName.getText().toString();
            String surname = editTextTeacherSurname.getText().toString();
            String email = editTextTeacherEmail.getText().toString();
            String login = editTextTeacherLogin.getText().toString();
            String password = editTextTeacherPassword.getText().toString();
            String hashedPassword = PasswordHasher.hashPassword(password);

            boolean result = dbHelper.addTeacher(name, surname, email, login, hashedPassword);
            if (result) {
                listener.onTeacherAdded(new Teacher(name, surname, email, login, password));
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Логин уже используется", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}