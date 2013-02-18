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

import static com.petrsu.geo2tag.IRequest.ISubscribeChannel.*;

public class SubscribeRequest extends BaseRequest {
    private String m_authToken;
    private String m_serverUrl;

    public SubscribeRequest(String authToken, String serverUrl) {
        m_authToken = authToken;
        m_serverUrl = serverUrl;
    }

    public JSONObject getJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AUTH_TOKEN, m_authToken);

        } catch (Exception e) {
            Log.e("", e.getLocalizedMessage());
        }
    }
}
