package com.petrsu.geo2tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import com.petrsu.geo2tag.objects.Channel;
import com.petrsu.geo2tag.objects.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ArrayAdapter;
import android.location.LocationManager;
import android.content.Context;
import android.location.Location;

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

    String m_authToken;
    String m_serverUrl;

    Spinner channelList;

    JSONArray channels = null;
    String[] channelsId;


    public AddTagDialog(String authToken, String serverUrl) {
        m_authToken = authToken;
        m_serverUrl = serverUrl;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.add_tag, null);

        LocationManager lm = (LocationManager)this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            String latitude = String.format("%f",location.getLatitude());
            String longitude = String.format("%f",location.getLongitude());

            ((EditText) view.findViewById(R.id.tagLatitude)).setText(latitude);
            ((EditText) view.findViewById(R.id.tagLongitude)).setText(longitude);
        }

        channelList = (Spinner) view.findViewById(R.id.channelList);
        AvailableChannelsRequest availableChannelsRequest = new AvailableChannelsRequest(m_authToken,
                m_serverUrl, new AvailableChannelsRequestListener());
        availableChannelsRequest.doRequest();

        builder.setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int channelId = (int) channelList.getSelectedItemId();

                        try{
                            String tagName = ((EditText) view.findViewById(R.id.tagName)).getText().toString();
                            String tagChannel = channels.getString(channelId).toString();
                            String tagDescription = ((EditText) view.findViewById(R.id.tagDescription)).getText().toString();
                            String tagUrl = ((EditText) view.findViewById(R.id.tagUrl)).getText().toString();
                            String tagLatitude = ((EditText) view.findViewById(R.id.tagLatitude)).getText().toString();
                            String tagLongitude = ((EditText) view.findViewById(R.id.tagLongitude)).getText().toString();

                            Bundle dialogResult = new Bundle();
                            dialogResult.putString("name", tagName);
                            dialogResult.putString("channel", tagChannel);
                            dialogResult.putString("description", tagDescription);
                            dialogResult.putString("url", tagUrl);
                            dialogResult.putString("latitude", tagLatitude);
                            dialogResult.putString("longitude", tagLongitude);
                            m_listener.onAddTagDialogPositiveClick(dialogResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public class AvailableChannelsRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            try {
                channels = new JSONObject(response).getJSONArray("channels");

                String[] channelNames;
                channelNames = new String[channels.length()];
                channelNames[0] = "Empty";

                for (int i = 0; i < channels.length(); i++) {
                    JSONObject c = channels.getJSONObject(i);

                    channelNames[i] = c.getString("name");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, channelNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                channelList.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
