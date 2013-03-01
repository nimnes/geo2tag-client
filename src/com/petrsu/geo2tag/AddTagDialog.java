package com.petrsu.geo2tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: BrendenHitt
 * Date: 01.03.13
 * Time: 3:15
 * To change this template use File | Settings | File Templates.
 */
public class AddTagDialog extends DialogFragment {
    public interface AddTagDialogListener {
        public void onAddTagDialogPositiveClick(Bundle dialogResult);
    }

    AddTagDialogListener m_listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.add_tag, null);

        builder.setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tagName = ((EditText) view.findViewById(R.id.tagName)).getText().toString();
                        String tagDescription = ((EditText) view.findViewById(R.id.tagDescription)).getText().toString();
                        String tagUrl = ((EditText) view.findViewById(R.id.tagUrl)).getText().toString();
                        String tagLatitude = ((EditText) view.findViewById(R.id.tagLatitude)).getText().toString();
                        String tagLongitude = ((EditText) view.findViewById(R.id.tagLongitude)).getText().toString();

                        Bundle dialogResult = new Bundle();
                        dialogResult.putString("name", tagName);
                        dialogResult.putString("description", tagDescription);
                        dialogResult.putString("url", tagUrl);
                        dialogResult.putString("latitude", tagLatitude);
                        dialogResult.putString("longitude", tagLongitude);
                        m_listener.onAddTagDialogPositiveClick(dialogResult);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddTagDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            m_listener = (AddTagDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement AddTagDialogListener");
        }
    }
}
