package com.petrsu.geo2tag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import com.petrsu.geo2tag.objects.*;

public class Main extends Activity {
    public final static String EXTRA_MESSAGE = "com.petrsu.geo2tag.MESSAGE";
    public final static String SERVER_URL = "http://tracks.osll.spb.ru:81/service";
    public User m_user = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        m_user = new User();

        ListView listView = (ListView) findViewById(R.id.options_list);
        String[] values = new String[] {"Available channels", "Tags"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, values);
        listView.setAdapter(adapter);

        if (m_user.getToken() == null) {
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
                        if (!Geo2Tag.getInstance().subscribedChannels.contains(cn)) {
                            SubscribeRequest subscribeRequest = new SubscribeRequest(m_user.getToken(), cn, SERVER_URL,
                                    new SubscribeRequestListener());
                            subscribeRequest.doRequest();
                        }
                    }

                    for (String cn : Geo2Tag.getInstance().subscribedChannels) {
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
            Log.i("GEO2TAG_API", "Authentication success!");
            try {
                JSONObject responseJSON = new JSONObject(response);
                m_user.setToken(responseJSON.getString("auth_token"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AvailableChannelsRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i("GEO2TAG_API", "Got available channels.");

            // open activity with list of channels
            Intent intent = new Intent(getApplicationContext(), ChannelsActivity.class);
            intent.putExtra("API_RESPONSE", response.toString());
            startActivityForResult(intent, 1);
        }
    }

    public class SubscribedRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i("GEO2TAG_API", "Got subscribed channels.");
            try {
                JSONArray jsonArray = new JSONObject(response).getJSONArray("channels");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    Geo2Tag.getInstance().subscribedChannels.add(c.getString("name"));
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
                Geo2Tag.getInstance().subscribedChannels.add(channelName);
                Log.i("GEO2TAG_API", "Subscribed to channel: " + channelName);
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
                Geo2Tag.getInstance().subscribedChannels.remove(channelName);
                Log.i("GEO2TAG_API", "Unsubscribed from channel: " + channelName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
