package com.petrsu.geo2tag;

import android.util.Log;
import org.json.JSONObject;

import static com.petrsu.geo2tag.IRequest.ICommon.METHOD;
import static com.petrsu.geo2tag.IRequest.IRegisterUser.*;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 19.02.13
 * Time: 13:41
 * Project: com.petrsu.geo2tag
 */
public class RegisterUserRequest extends BaseRequest {
    private String m_login;
    private String m_password;
    private String m_serverUrl;
    private String m_email;
    private AsyncRunner.RequestListener m_listener;

    public RegisterUserRequest(String login, String password, String email, String serverUrl, RequestListener listener) {
        m_login = login;
        m_password = password;
        m_email = email;
        m_serverUrl = serverUrl;
        m_listener = listener;
    }

    public JSONObject getJsonRequest() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(METHOD, "registerUser");

            JSONObject paramsObject = new JSONObject();
            paramsObject.put(LOGIN, m_login);
            paramsObject.put(PASSWORD, m_password);
            paramsObject.put(EMAIL, m_email);
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
