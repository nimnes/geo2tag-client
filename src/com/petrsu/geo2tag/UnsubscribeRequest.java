package com.petrsu.geo2tag;

import android.util.Log;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import static com.petrsu.geo2tag.IRequest.ISubscribeChannel.AUTH_TOKEN;
import static com.petrsu.geo2tag.IRequest.ISubscribeChannel.CHANNEL;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 18.02.13
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public class UnsubscribeRequest extends BaseRequest {
    private String m_authToken;
    private String m_channel;
    private String m_serverUrl;
    private AsyncRunner.RequestListener m_listener;

    public UnsubscribeRequest(String authToken, String channel, String serverUrl, AsyncRunner.RequestListener listener) {
        m_authToken = authToken;
        m_channel = channel;
        m_serverUrl = serverUrl;
        m_listener = listener;
    }

    public JSONObject getJsonRequest() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(METHOD, "unsubscribe");

            JSONObject paramsObject = new JSONObject();
            paramsObject.put(AUTH_TOKEN, m_authToken);
            paramsObject.put(CHANNEL, m_channel);
            jsonObject.put("params", paramsObject);
        } catch (Exception e) {
            Log.e(REQUEST_LOG, e.getLocalizedMessage());
        }

        return jsonObject;
    }

    public void doRequest() {
        AsyncRunner asyncRunner = new AsyncRunner(m_listener, m_serverUrl);
        asyncRunner.doInBackground(getJsonRequest());
    }
}
