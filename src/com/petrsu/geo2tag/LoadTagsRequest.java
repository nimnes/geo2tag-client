package com.petrsu.geo2tag;

import android.util.Log;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.ILoadTags.*;
import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 20.02.13
 * Time: 17:52
 * Project: com.petrsu.geo2tag
 */
public class LoadTagsRequest extends BaseRequest {
    private String m_authToken;
    private String m_serverUrl;
    private double m_latitude;
    private double m_longitude;
    private double m_radius;
    private RequestListener m_listener;

    public LoadTagsRequest(String authToken, double latitude, double longitude, double radius, String serverUrl,
                           RequestListener listener) {
        m_authToken = authToken;
        m_serverUrl = serverUrl;
        m_latitude = latitude;
        m_longitude = longitude;
        m_radius = radius;
        m_listener = listener;
    }

    public JSONObject getJsonRequest() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(METHOD, REQUEST);

            JSONObject paramsObject = new JSONObject();
            paramsObject.put(AUTH_TOKEN, m_authToken);
            paramsObject.put(LATITUDE, m_latitude);
            paramsObject.put(LONGITUDE, m_longitude);
            paramsObject.put(RADIUS, m_radius);
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
