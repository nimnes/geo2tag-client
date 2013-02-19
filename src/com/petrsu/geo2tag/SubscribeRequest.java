package com.petrsu.geo2tag;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 18.02.13
 * Time: 20:10
 * To change this template use File | Settings | File Templates.
 */

import android.util.Log;
import org.json.JSONObject;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import static com.petrsu.geo2tag.IRequest.ISubscribeChannel.*;

public class SubscribeRequest extends BaseRequest {
    private String m_authToken;
    private String m_channel;
    private String m_serverUrl;
    private RequestListener m_listener;

    public SubscribeRequest(String authToken, String channel, String serverUrl, RequestListener listener) {
        m_authToken = authToken;
        m_channel = channel;
        m_serverUrl = serverUrl;
        m_listener = listener;
    }

    public JSONObject getJsonRequest() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(METHOD, REQUEST);

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
        new AsyncRunner(m_listener, m_serverUrl).execute(getJsonRequest());
    }
}
