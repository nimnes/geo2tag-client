package com.petrsu.geo2tag;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 20.02.13
 * Time: 18:26
 * Project: com.petrsu.geo2tag
 */
public class MapActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
    }
}
