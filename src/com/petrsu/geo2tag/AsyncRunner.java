package com.petrsu.geo2tag;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import static com.petrsu.geo2tag.IRequest.ICommon.*;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 18.02.13
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */
public class AsyncRunner extends AsyncTask<JSONObject, Void, JSONObject> {
    public String ASYNC_LOG = "AsyncRunner";
    RequestListener m_listener;
    String m_serverUrl;

    public void setServerUrl(String url) {
        m_serverUrl = url;
    }

    public String getServerUrl() {
        return m_serverUrl;
    }

    public AsyncRunner(RequestListener listener, String serverUrl) {
        m_listener = listener;
        m_serverUrl = serverUrl;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... params) {
        JSONObject responseJSON = null;
        JSONObject requestJSON = params[0];

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(m_serverUrl + requestJSON.getString(METHOD));
            Log.i(ASYNC_LOG, m_serverUrl + requestJSON.getString(METHOD));

            StringEntity entity = new StringEntity(requestJSON.getJSONObject("params").toString());
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, "application/json"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(entity);

            Log.i(ASYNC_LOG, requestJSON.toString());

            HttpResponse response = httpClient.execute(httpPost);

            String responseString = EntityUtils.toString(response.getEntity());
            responseJSON = new JSONObject(responseString);
            responseJSON.put(METHOD, requestJSON.getString(METHOD));

            // add channel name to response for subscribe/unsubscribe requests
            // for defining which channel we subscribed/unsubscribed
            if (requestJSON.getString(METHOD).equals("/subscribe") ||
                    requestJSON.getString(METHOD).equals("/unsubscribe")) {
                responseJSON.put("channel", requestJSON.getJSONObject("params").getString("channel"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(ASYNC_LOG, "Error while executing request");
        }

        return responseJSON;
    }

    protected void onPostExecute(JSONObject result) {
        if (result == null) {
            m_listener.onGeo2TagError(-1);
            return;
        }

        Log.i(ASYNC_LOG, result.toString());

        try {
            int errorCode = result.getInt("errno");
            if (errorCode != 0) {
                m_listener.onGeo2TagError(errorCode);
                return;
            }
        } catch (Exception e) {
            m_listener.onGeo2TagError(-1);
        }

        m_listener.onComplete(result.toString());
    }

    // interface for request handler function
    public static interface RequestListener {
        public void onComplete(final String response);
        public void onGeo2TagError(final int errorCode);
    }
}

