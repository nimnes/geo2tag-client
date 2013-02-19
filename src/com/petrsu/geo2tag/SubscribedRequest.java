package com.petrsu.geo2tag;

import android.util.Log;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import static com.petrsu.geo2tag.IRequest.ISubscribedChannel.*;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 18.02.13
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class SubscribedRequest extends BaseRequest {
    private String m_authToken;
    private String m_serverUrl;
    private AsyncRunner.RequestListener m_listener;

    public SubscribedRequest(String authToken, String serverUrl, AsyncRunner.RequestListener listener) {
        m_authToken = authToken;
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
