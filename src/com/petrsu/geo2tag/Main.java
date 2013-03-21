package com.petrsu.geo2tag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;

import com.petrsu.geo2tag.objects.*;

public class Main extends Activity implements AddChannelDialog.AddChannelDialogListener, AuthorizationDialog.AuthorizationDialogListener,
        AddTagDialog.AddTagDialogListener {
    public final static String EXTRA_MESSAGE = "com.petrsu.geo2tag.MESSAGE";
    public final static String SERVER_URL = "http://192.168.112.107/service";
    private User m_user;
    private GoogleMap map;
    private HashMap<Marker, Mark> tagsMarkerMap;
    private MenuItem refreshMenuItem;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tagsMarkerMap = new HashMap<Marker, Mark>();

        m_user = User.getInstance();

//        m_user.setToken("d41d8cd98f00b204e9800998ecf8427e");
        if (m_user.getToken() == null) {
            AuthorizationDialog authorizationDialog = new AuthorizationDialog();
            authorizationDialog.show(getFragmentManager(), "dialog_authorization");
        }

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(61.78, 34.36), 6));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.tag_info_window, null);

                TextView titleView = (TextView) v.findViewById(R.id.title);
                TextView descriptionView = (TextView) v.findViewById(R.id.description);

                titleView.setText(marker.getTitle());
                descriptionView.setText(marker.getSnippet());

                Mark m = tagsMarkerMap.get(marker);
                ImageView imageView = (ImageView) v.findViewById(R.id.image);

                if (m.getBitmap() != null) {
                    imageView.setImageBitmap(m.getBitmap());

                    // set bitmap to null for memory optimization
                    m.setBitmap(null);
                } else if (!m.getLink().isEmpty()) {
                    new DownloadImageTask().execute(marker);
                }

                return v;
            }
        });

        SubscribedRequest subscribedRequest = new SubscribedRequest(m_user.getToken(), SERVER_URL,
                new SubscribedRequestListener());
        subscribedRequest.doRequest();
    }

    /***
     * Downloading images for maps marks using AsyncTask
     */
    public class DownloadImageTask extends AsyncTask<Marker, Void, Bitmap> {
        Mark m_mark = null;
        Marker m_marker = null;

        @Override
        protected Bitmap doInBackground(Marker... markers) {
            m_marker = markers[0];
            m_mark = tagsMarkerMap.get(m_marker);

            Bitmap bitmap = null;
            Log.i("DownloadImageTask", "url: " + m_mark.getLink());

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(m_mark.getLink()).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // update marker if it is shown
            if (m_marker.isInfoWindowShown()) {
                m_mark.setBitmap(result);
                m_marker.showInfoWindow();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        for (int i = 0; i < menu.size(); i++) {
               if (menu.getItem(i).getItemId() == R.id.action_refresh) {
                    refreshMenuItem = menu.getItem(i);
                    break;
                }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_user.getToken() != null) {
            switch (item.getItemId()) {
                case R.id.action_search:
                    Toast.makeText(this, "Menu item 1 selected", Toast.LENGTH_SHORT)
                        .show();
                    break;
                case R.id.action_channels:
                    AvailableChannelsRequest availableChannelsRequest = new AvailableChannelsRequest(m_user.getToken(),
                        SERVER_URL, new AvailableChannelsRequestListener());
                    availableChannelsRequest.doRequest();
                    break;
                case R.id.action_refresh:
                    refreshMenuItem.setActionView(R.layout.refresh_spinner);
                    refreshMenuItem.expandActionView();
                    LoadTagsRequest loadTagsRequest = new LoadTagsRequest(m_user.getToken(),
                        61.78, 34.36, 30000, SERVER_URL, new LoadTagsRequestListener());
                    loadTagsRequest.doRequest();
                    break;
                case R.id.action_add_channel:
                    AddChannelDialog addChannelDialog = new AddChannelDialog();
                    addChannelDialog.show(getFragmentManager(), "add_channel");
                    break;
                case R.id.action_add_tag:
                    AddTagDialog addTagDialog = new AddTagDialog(m_user.getToken(), SERVER_URL);
                    addTagDialog.show(getFragmentManager(), "add_tag");
                    break;
                default:
                    break;
            }
        } else {
            AuthorizationDialog authorizationDialog = new AuthorizationDialog();
            authorizationDialog.show(getFragmentManager(), "dialog_authorization");
        }
        return true;
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

    /***
     * Listeners for handling responses from Geo2Tag server
     ***/
    public class LoginRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i(LISTENER_LOG, "Authentication successfull!");
            try {
                JSONObject responseJSON = new JSONObject(response);
                m_user.setToken(responseJSON.getString("auth_token"));

                refreshMenuItem.setActionView(R.layout.refresh_spinner);
                refreshMenuItem.expandActionView();

                LoadTagsRequest loadTagsRequest = new LoadTagsRequest(m_user.getToken(),
                        61.78, 34.36, 30000, SERVER_URL, new LoadTagsRequestListener());
                loadTagsRequest.doRequest();
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

    public class AddTagRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            Log.i(LISTENER_LOG, "Channel created");
        }
    }

    public class LoadTagsRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(final String response) {
            try {
                JSONArray channelsArray = new JSONObject(response).getJSONObject("rss")
                        .getJSONObject("channels")
                        .getJSONArray("items");

                for (int i = 0; i < channelsArray.length(); i++) {
                    JSONObject channel = channelsArray.getJSONObject(i);

                    JSONArray tags = channel.getJSONArray("items");
                    Log.i(LISTENER_LOG, "" + tags.length());
                    for (int j = 0; j < tags.length(); j++) {
                        JSONObject tag = tags.getJSONObject(j);

                        Mark m = new Mark();
                        m.setTitle(tag.getString("title"));
                        m.setDescription(tag.getString("description"));
                        m.setLatitude(tag.getDouble("latitude"));
                        m.setLongitude(tag.getDouble("longitude"));
                        m.setLink(tag.getString("link"));

                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(m.getPosition())
                                .title(tag.getString("title"))
                                .snippet(tag.getString("description")));

                        tagsMarkerMap.put(marker, m);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshMenuItem.collapseActionView();
            refreshMenuItem.setActionView(null);
            Log.i(LISTENER_LOG, "Tags loaded");
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
    public void onAuthorizationDialogPositiveClick(Bundle dialogResult) {
        if(dialogResult.getString("email").isEmpty()) {
            LoginRequest loginRequest = new LoginRequest(dialogResult.getString("username"),
                    dialogResult.getString("password"),
                    SERVER_URL,
                    new LoginRequestListener());
            loginRequest.doRequest();
        } else {
            RegisterUserRequest registerUserRequest = new RegisterUserRequest(dialogResult.getString("username"),
                    dialogResult.getString("password"),
                    dialogResult.getString("email"),
                    SERVER_URL,
                    new LoginRequestListener());
            registerUserRequest.doRequest();
        }
    }

    @Override
    public void onAddTagDialogPositiveClick(Bundle dialogResult) {
        String tagName = dialogResult.getString("name");
        String tagChannel = dialogResult.getString("channel");
        String tagDescription = dialogResult.getString("description");
        String tagUrl = dialogResult.getString("url");
        String tagLatitude = dialogResult.getString("latitude");
        String tagLongitude = dialogResult.getString("longitude");

        final Calendar c = Calendar.getInstance();
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(c.get(Calendar.MINUTE));
        String second = String.valueOf(c.get(Calendar.SECOND));
        String millisecond = String.valueOf(c.get(Calendar.MILLISECOND));

        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        if (Integer.parseInt(day) < 10) day = "0" + day;
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);
        if (Integer.parseInt(month) < 10) month = "0" + month;
        String year = String.valueOf(c.get(Calendar.YEAR));

        String tagTime = day + " " + month + " " + year + " " + hour + ":" + minute + ":" + second + "." + millisecond;

        if ((!tagName.isEmpty()) && (!tagChannel.isEmpty()) && (tagLatitude.isEmpty()) && (tagLongitude.isEmpty())) {
            AddTagRequest addTagRequest = new AddTagRequest(m_user.getToken(), tagChannel, tagName, tagUrl, tagDescription,
                    Double.parseDouble(tagLatitude), Double.parseDouble(tagLongitude), tagTime, SERVER_URL,
                    new AddTagRequestListener());
            addTagRequest.doRequest();
        }
    }
}
