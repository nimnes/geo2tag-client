package com.petrsu.geo2tag;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 31.01.13
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
// base request listener with simple handling of geo2tag errors
public abstract class BaseRequestListener implements Geo2Tag.RequestListener {
    @Override
    public void onGeo2TagError(final int errorCode) {
        Log.e("GEO2TAG_API", "Error: " + errorCode);
    }
}
