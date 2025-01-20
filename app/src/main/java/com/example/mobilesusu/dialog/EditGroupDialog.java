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

public class EditGroupDialog extends DialogFragment {
    private String groupName;
    private EditGroupDialogListener listener;
    private DatabaseHelper dbHelper;

    public interface EditGroupDialogListener {
        void onGroupUpdated(String oldGroupName, String newGroupName);
    }

    public EditGroupDialog(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditGroupDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditGroupDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_group, null);

        // Инициализация DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());

        EditText editTextGroupName = view.findViewById(R.id.editTextGroupName);
        Button buttonSaveGroup = view.findViewById(R.id.buttonSaveGroup);

        editTextGroupName.setText(groupName);

        buttonSaveGroup.setOnClickListener(v -> {
            String newGroupName = editTextGroupName.getText().toString();
            if (!newGroupName.isEmpty()) {
                try {
                    dbHelper.updateGroup(groupName, newGroupName);
                    listener.onGroupUpdated(groupName, newGroupName);
                    dismiss();
                } catch (IllegalArgumentException e) {
                    editTextGroupName.setError(e.getMessage());
                }
            } else {
                editTextGroupName.setError("Введите название группы");
            }
        });

        builder.setView(view);
        return builder.create();
    }
}