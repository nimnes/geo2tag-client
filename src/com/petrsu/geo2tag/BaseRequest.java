package com.petrsu.geo2tag;

import org.json.JSONObject;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 18.02.13
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseRequest {
    private String m_method;
    private RequestListener m_listener;
    public String REQUEST_LOG = "BaseRequest";

    public JSONObject getJsonRequest() {
        JSONObject jsonObject = new JSONObject();
        return null;
    }

    public String getStringRequest() {
        return getJsonRequest().toString();
    }

    public void doRequest() {

    }

    public void setListener(RequestListener listener) {
        m_listener = listener;
    }

    public RequestListener getListener() {
        return m_listener;
    }
}
