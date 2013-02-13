package com.petrsu.geo2tag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.spb.osll.JGeo;
import ru.spb.osll.json.JsonAvailableChannelResponse;
import ru.spb.osll.objects.User;
import ru.spb.osll.json.JsonLoginRequest;
import ru.spb.osll.json.JsonLoginResponse;

import java.util.ArrayList;

public class Main extends Activity {
    public final static String EXTRA_MESSAGE = "com.petrsu.geo2tag.MESSAGE";
    public final static String SERVER_URL = "http://tracks.osll.spb.ru:81/service";
    public User user = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        user = new User();

        ListView listView = (ListView) findViewById(R.id.options_list);
        String[] values = new String[] {"Available channels", "Tags"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, values);
        listView.setAdapter(adapter);

        if (user.getToken() == null) {
            JSONObject result = new JsonLoginRequest("nimnes", "geo2tag", SERVER_URL).doRequest();

            if (result != null) {
                Log.i("GEO2TAG", result.toString());
            } else {
                Log.i("GEO2TAG", "BUGGGG");
            }
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
                Geo2Tag.getInstance().getAvailableChannels(new ChannelsRequestListener());
//                JSONObject result = new JsonAvailableChannelRequest(Geo2Tag.getInstance().getAuthToken(),  "http://tracks.osll.spb.ru:81/service").doRequest();
//                Log.e("JGEO", result.toString());
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
                    for (int i = 0; i < subscribedChannels.size(); i++) {
                        String cn = subscribedChannels.get(i);
                        if (!Geo2Tag.getInstance().subscribedChannels.contains(cn)) {
                            Geo2Tag.getInstance().subscribe(new SubscribeListener(), cn);
                        }
                    }

                    for (int i = 0; i < Geo2Tag.getInstance().subscribedChannels.size(); i++) {
                        String cn = Geo2Tag.getInstance().subscribedChannels.get(i);
                        if (!subscribedChannels.contains(cn)) {
                            Geo2Tag.getInstance().unsubscribe(new UnSubscribeListener(), cn);
                        }
                    }
                }
                break;
        }
    }

    public class AuthenticationListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i("GEO2TAG_API", "Authentication success!");
            try {
                JSONObject responseJSON = new JSONObject(response);
                Geo2Tag.getInstance().setAuthToken(responseJSON.getString("auth_token"));
                Geo2Tag.getInstance().setAuthenticated(true);
                Geo2Tag.getInstance().getSubscribedChannels(new SubscribedChannelsListener());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ChannelsRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i("GEO2TAG_API", "Got available channels.");

            // open activity with list of channels
            Intent intent = new Intent(getApplicationContext(), ChannelsActivity.class);
            intent.putExtra("API_RESPONSE", response.toString());
            startActivityForResult(intent, 1);
        }
    }

    public class SubscribedChannelsListener extends BaseRequestListener {
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

    public class SubscribeListener extends BaseRequestListener {
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

    public class UnSubscribeListener extends BaseRequestListener {
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
