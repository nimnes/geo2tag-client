package com.petrsu.geo2tag;

import android.util.Log;
import android.webkit.JsPromptResult;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.ILogin.*;
import static com.petrsu.geo2tag.IRequest.ICommon.*;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 18.02.13
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class LoginRequest extends BaseRequest {
    private String m_login;
    private String m_password;
    private String m_serverUrl;
    private RequestListener m_listener;

    public LoginRequest(String login, String password, String serverUrl, RequestListener listener) {
        m_login = login;
        m_password = password;
        m_serverUrl = serverUrl;
        m_listener = listener;
    }

    public JSONObject getJsonRequest() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(METHOD, REQUEST);

            JSONObject paramsObject = new JSONObject();
            paramsObject.put(LOGIN, m_login);
            paramsObject.put(PASSWORD, m_password);
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
