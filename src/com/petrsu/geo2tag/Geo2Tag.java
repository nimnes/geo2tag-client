package com.petrsu.geo2tag;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 30.01.13
 * This class implements interaction with Geo2Tag server.
 */
public class Geo2Tag {
    // Singleton class
    private static Geo2Tag instance = null;
    private String authToken;
    private boolean authenticated = false;
    private final static String BASE_URL = "http://tracks.osll.spb.ru:81/service/";
    public ArrayList<String> subscribedChannels;

    protected Geo2Tag() {
        subscribedChannels = new ArrayList<String>();
    }

    public void setAuthToken(String token) {
        authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public static Geo2Tag getInstance() {
        if (instance == null) {
            instance = new Geo2Tag();
        }
        return instance;
    }

    // authentication to Geo2Tag server
    public void authenticate(RequestListener listener) {
        JSONObject requestJSON = new JSONObject();
        JSONObject paramsJSON = new JSONObject();

        try {
            requestJSON.put("method", "login");
            requestJSON.put("params", paramsJSON);
            paramsJSON.put("login", "nimnes");
            paramsJSON.put("password", "geo2tag");
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LongRequest(listener).execute(requestJSON);
    }

    // getting available channels for current location
    public void getAvailableChannels(RequestListener listener) {
        JSONObject requestJSON = new JSONObject();
        JSONObject paramsJSON = new JSONObject();

        try {
            requestJSON.put("method", "channels");
            requestJSON.put("params", paramsJSON);
            paramsJSON.put("auth_token", authToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LongRequest(listener).execute(requestJSON);
    }

    // getting available channels for current location
    public void getSubscribedChannels(RequestListener listener) {
        JSONObject requestJSON = new JSONObject();
        JSONObject paramsJSON = new JSONObject();

        try {
            requestJSON.put("method", "subscribed");
            requestJSON.put("params", paramsJSON);
            paramsJSON.put("auth_token", authToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LongRequest(listener).execute(requestJSON);
    }

    // subscribe to channel
    public void subscribe(RequestListener listener, String channelName) {
        if (subscribedChannels.contains(channelName)) {
            return;
        }

        JSONObject requestJSON = new JSONObject();
        JSONObject paramsJSON = new JSONObject();

        try {
            requestJSON.put("method", "subscribe");
            requestJSON.put("params", paramsJSON);
            paramsJSON.put("auth_token", authToken);
            paramsJSON.put("channel", channelName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LongRequest(listener).execute(requestJSON);
    }

    // subscribe to channel
    public void unsubscribe(RequestListener listener, String channelName) {
        JSONObject requestJSON = new JSONObject();
        JSONObject paramsJSON = new JSONObject();

        try {
            requestJSON.put("method", "unsubscribe");
            requestJSON.put("params", paramsJSON);
            paramsJSON.put("auth_token", authToken);
            paramsJSON.put("channel", channelName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LongRequest(listener).execute(requestJSON);
    }

    // subscribe to list of channels
    public void subscribeToChannels(RequestListener listener, ArrayList<String> channelsList) {
        for (int i = 0; i < channelsList.size(); i++) {
            subscribe(listener, channelsList.get(i));
        }
    }

    // Request are implemented with AsyncTask to avoid freezes in UI
    public class LongRequest extends AsyncTask<JSONObject, Void, JSONObject> {
        RequestListener listener;

        LongRequest(RequestListener listener) {
            this.listener = listener;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject responseJSON;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(BASE_URL + params[0].getString("method"));
                httpPost.setHeader("content-type", "application/json");

                StringEntity entity = new StringEntity(params[0].get("params").toString());
                httpPost.setEntity(entity);

                Log.i("LongRequest", params[0].get("params").toString());

                HttpResponse response = httpClient.execute(httpPost);

                String responseString = EntityUtils.toString(response.getEntity());
                responseJSON = new JSONObject(responseString);
                responseJSON.put("method", params[0].get("method"));

                if (params[0].getString("method").equals("subscribe") ||
                        params[0].getString("method").equals("unsubscribe")) {
                    responseJSON.put("channel", params[0].getJSONObject("params").getString("channel"));
                }
            } catch (Exception e) {
                Log.e("LongRequest", e.getLocalizedMessage());
                return null;
            }

            return responseJSON;
        }

        protected void onPostExecute(JSONObject result) {
            if (result == null) {
                listener.onGeo2TagError(-1);
                return;
            }

            try {
                int errorCode = result.getInt("errno");
                if (errorCode != 0) {
                    listener.onGeo2TagError(errorCode);
                    return;
                }
            } catch (Exception e) {
                listener.onGeo2TagError(-1);
            }

            listener.onComplete(result.toString());
        }
    }

    // interface for request handler function
    public static interface RequestListener {
        public void onComplete(final String response);
        public void onGeo2TagError(final int errorCode);
    }
}
