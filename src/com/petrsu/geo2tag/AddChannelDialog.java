package com.petrsu.geo2tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.petrsu.geo2tag.objects.Channel;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 19.02.13
 * Time: 16:59
 * Project: com.petrsu.geo2tag
 */
public class AddChannelDialog extends DialogFragment {
    public interface AddChannelDialogListener {
        public void onAddChannelDialogPositiveClick(Bundle dialogResult);
    }

    AddChannelDialogListener m_listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.add_channel, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String name = ((EditText) view.findViewById(R.id.channelName)).getText().toString();
                String description = ((EditText) view.findViewById(R.id.description)).getText().toString();
                String url = ((EditText) view.findViewById(R.id.url)).getText().toString();
                Integer activeRadius = Integer.parseInt(((EditText) view.findViewById(R.id.activeRadius)).getText().toString());

                Bundle dialogResult = new Bundle();
                dialogResult.putString("name", name);
                dialogResult.putString("description", description);
                dialogResult.putString("url", url);
                dialogResult.putInt("activeRadius", activeRadius);
                m_listener.onAddChannelDialogPositiveClick(dialogResult);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddChannelDialog.this.getDialog().cancel();
            }
        });
        builder.setTitle(R.string.title_add_channel_activity);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            m_listener = (AddChannelDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement AddChannelDialogListener");
        }
    }
}