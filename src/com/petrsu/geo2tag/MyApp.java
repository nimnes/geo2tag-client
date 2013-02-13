package com.petrsu.geo2tag;

import android.app.Application;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 30.01.13
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public class MyApp extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }
}
