package com.petrsu.geo2tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.CheckBox;
import android.view.View;
import android.widget.CompoundButton;

/**
 * Created with IntelliJ IDEA.
 * User: BrendenHitt
 * Date: 20.02.13
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationDialog extends DialogFragment {
    public interface AuthorizationDialogListener {
        public void onAuthorizationDialogPositiveClick(Bundle dialogResult);
    }

    AuthorizationDialogListener m_listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_authorization, null);

        CheckBox registration = (CheckBox) view.findViewById(R.id.registration);
        registration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ((AlertDialog) getDialog()).setTitle(R.string.registration);
                    ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.register);
                    ((EditText) view.findViewById(R.id.email)).setVisibility(View.VISIBLE);
                } else {
                    ((AlertDialog) getDialog()).setTitle(R.string.login);
                    ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.signin);
                    ((EditText) view.findViewById(R.id.email)).setVisibility(View.GONE);
                }
            }
        });

        builder.setView(view)
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = ((EditText) view.findViewById(R.id.username)).getText().toString();
                        String password = ((EditText) view.findViewById(R.id.password)).getText().toString();
                        String email = "";

                        if (((CheckBox) view.findViewById(R.id.registration)).isChecked())
                            email = ((EditText) view.findViewById(R.id.email)).getText().toString();

                        Bundle dialogResult = new Bundle();
                        dialogResult.putString("username", username);
                        dialogResult.putString("password", password);
                        dialogResult.putString("email", email);
                        m_listener.onAuthorizationDialogPositiveClick(dialogResult);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AuthorizationDialog.this.getDialog().cancel();
                    }
                });

        builder.setTitle(R.string.title_authorization_dialog);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            m_listener = (AuthorizationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement AuthorizationDialogListener");
        }
    }
}

