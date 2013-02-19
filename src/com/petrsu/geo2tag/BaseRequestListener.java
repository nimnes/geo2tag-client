package com.petrsu.geo2tag;

import android.util.Log;
import com.petrsu.geo2tag.AsyncRunner.RequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 31.01.13
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
// base request listener with simple handling of geo2tag errors
public abstract class BaseRequestListener implements RequestListener {
    public String LISTENER_LOG = "RequestListener";

    public void onGeo2TagError(final int errorCode) {
        Log.e(LISTENER_LOG, "Error: " + errorCode);
    }
}
