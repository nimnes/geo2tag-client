package com.petrsu.geo2tag;

import android.util.Log;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.IApplyMark.*;
import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: BrendenHitt
 * Date: 06.03.13
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
public class AddTagRequest extends BaseRequest {
    private String m_authToken;
    private String m_channel;
    private String m_title;
    private String m_link;
    private String m_description;
    private double m_latitude;
    private double m_longitude;
    private String m_time;
    private String m_serverUrl;
    private RequestListener m_listener;

    public AddTagRequest(String authToken, String channel, String title, String link,
                         String description, double latitude, double longitude, String time, String serverUrl, RequestListener listener){
        m_authToken = authToken;
        m_channel = channel;
        m_title = title;
        m_link = link;
        m_description = description;
        m_latitude = latitude;
        m_longitude = longitude;
        m_time = time;
        m_serverUrl = serverUrl;
        m_listener = listener;
    }

    @Override
    public JSONObject getJsonRequest() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(METHOD, REQUEST);

            JSONObject paramsObject = new JSONObject();
            paramsObject.put(AUTH_TOKEN, m_authToken);
            paramsObject.put(TITLE, m_title);
            paramsObject.put(CHANNEL, m_channel);
            paramsObject.put(DESCRIPTION, m_description);
            paramsObject.put(LINK, m_link);
            paramsObject.put(DESCRIPTION, m_description);
            paramsObject.put(LATITUDE, m_latitude);
            paramsObject.put(LONGITUDE, m_longitude);
            paramsObject.put(TIME, m_time);
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
