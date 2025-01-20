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

public class AddGroupDialog extends DialogFragment {
    private AddGroupDialogListener listener;

    public interface AddGroupDialogListener {
        boolean onGroupAdded(String groupName) throws IllegalArgumentException;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddGroupDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddGroupDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_group, null);

        EditText editTextGroupName = view.findViewById(R.id.editTextGroupName);
        Button buttonSaveGroup = view.findViewById(R.id.buttonSaveGroup);

        buttonSaveGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString();
            if (!groupName.isEmpty()) {
                try {
                    boolean isAdded = listener.onGroupAdded(groupName);
                    if (isAdded) {
                        dismiss();
                    }
                } catch (IllegalArgumentException e) {
                    // Показываем сообщение об ошибке
                    editTextGroupName.setError(e.getMessage());
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }
}