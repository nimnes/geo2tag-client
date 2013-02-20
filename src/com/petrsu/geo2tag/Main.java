package com.petrsu.geo2tag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import com.petrsu.geo2tag.objects.*;

public class Main extends FragmentActivity implements AddChannelDialog.AddChannelDialogListener, AuthorizationDialog.AuthorizationDialogListener {
    public final static String EXTRA_MESSAGE = "com.petrsu.geo2tag.MESSAGE";
    public final static String SERVER_URL = "http://192.168.112.107/service";
    private User m_user;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        m_user = User.getInstance();

        ListView listView = (ListView) findViewById(R.id.options_list);
        String[] values = new String[] {"Available channels", "Add channel", "Tags"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, values);
        listView.setAdapter(adapter);

        m_user.setToken("d41d8cd98f00b204e9800998ecf8427e");
        if (m_user.getToken() == null) {
            AuthorizationDialog authorizationDialog = new AuthorizationDialog();
            authorizationDialog.show(getSupportFragmentManager(), "dialog_authorization");
            LoginRequest loginRequest = new LoginRequest("nimnes", "geo2tag", SERVER_URL, new LoginRequestListener());
            loginRequest.doRequest();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onOptionSelected(position);
            }
        });


    }

    private void onOptionSelected(int itemIndex) {
        switch (itemIndex) {
            case 0:
                AvailableChannelsRequest availableChannelsRequest = new AvailableChannelsRequest(m_user.getToken(),
                        SERVER_URL, new AvailableChannelsRequestListener());
                availableChannelsRequest.doRequest();
                break;
            case 1:
                AddChannelDialog addChannelDialog = new AddChannelDialog();
                addChannelDialog.show(getSupportFragmentManager(), "add_channel");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:  // ChannelsActivity
                if (resultCode == RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    ArrayList<String> subscribedChannels = extras.getStringArrayList("SUBSCRIBED_CHANNELS");

                    // compare old subscribed channels list with new
                    // and subscribe/unsubscribe to channels
                    for (String cn : subscribedChannels) {
                        if (!m_user.hasChannel(cn)) {
                            SubscribeRequest subscribeRequest = new SubscribeRequest(m_user.getToken(), cn, SERVER_URL,
                                    new SubscribeRequestListener());
                            subscribeRequest.doRequest();
                        }
                    }

                    for (String cn : m_user.getSubscribedChannels()) {
                        if (!subscribedChannels.contains(cn)) {
                            UnsubscribeRequest unsubscribeRequest = new UnsubscribeRequest(m_user.getToken(), cn,
                                    SERVER_URL, new UnSubscribeRequestListener());
                            unsubscribeRequest.doRequest();
                        }
                    }
                }
                break;
        }
    }

    public class LoginRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i(LISTENER_LOG, "Authentication successfull!");
            try {
                JSONObject responseJSON = new JSONObject(response);
                m_user.setToken(responseJSON.getString("auth_token"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onGeo2TagError(final int errorCode) {
            if (errorCode != 0) {
                User.getInstance().setName("");
                User.getInstance().setPass("");
                User.getInstance().setToken("");
            }
        }
    }
    public class RegisterUserListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i(LISTENER_LOG, "Registration successfull!");
        }
    }

    public class AvailableChannelsRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i(LISTENER_LOG, "Got available channels.");

            // open activity with list of channels
            Intent intent = new Intent(getApplicationContext(), ChannelsActivity.class);
            intent.putExtra("API_RESPONSE", response);
            startActivityForResult(intent, 1);
        }
    }

    public class SubscribedRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            try {
                JSONArray jsonArray = new JSONObject(response).getJSONArray("channels");
                Log.i(LISTENER_LOG, "Got subscribed channels.");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    m_user.addChannel(c.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class SubscribeRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String channelName = jsonObject.getString("channel");
                m_user.addChannel(channelName);

                Log.i(LISTENER_LOG, "Subscribed to channel: " + channelName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class UnSubscribeRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String channelName = jsonObject.getString("channel");
                m_user.removeChannel(channelName);

                Log.i(LISTENER_LOG, "Unsubscribed from channel: " + channelName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class AddChannelRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i(LISTENER_LOG, "Channel created");
        }
    }

    @Override
    public void onAddChannelDialogPositiveClick(Bundle dialogResult) {
        AddChannelRequest addChannelRequest = new AddChannelRequest(m_user.getToken(), dialogResult.getString("name"),
                dialogResult.getString("description"), dialogResult.getString("url"),
                dialogResult.getInt("activeRadius"), SERVER_URL, new AddChannelRequestListener());
        addChannelRequest.doRequest();
    }

    @Override
    public void onAuthorizationDialogPositiveClick(DialogFragment dialog) {
        Log.i("AuthorizationDialog", "test");
    }
}
