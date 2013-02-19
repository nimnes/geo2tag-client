package com.petrsu.geo2tag;

import android.util.Log;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.IApplyChannel.*;
import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 19.02.13
 * Time: 15:43
 * Project: com.petrsu.geo2tag
 */
public class AddChannelRequest extends BaseRequest {
    private String m_authToken;
    private String m_serverUrl;
    private String m_name;
    private String m_description;
    private String m_url;
    private Integer m_activeRadius;
    private RequestListener m_listener;

    public AddChannelRequest(String authToken, String name, String description, String url, Integer activeRadius,
                             String serverUrl, RequestListener listener) {
        m_authToken = authToken;
        m_name = name;
        m_description = description;
        m_url = url;
        m_activeRadius = activeRadius;
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
            paramsObject.put(NAME, m_name);
            paramsObject.put(DESCRIPTION, m_description);
            paramsObject.put(URL, m_url);
            paramsObject.put(ACTIVE_RADIUS, m_activeRadius);
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
