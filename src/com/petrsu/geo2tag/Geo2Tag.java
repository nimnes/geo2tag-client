package com.petrsu.geo2tag;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 30.01.13
 * This class implements interaction with Geo2Tag server.
 */
public class Geo2Tag {
    // Singleton class
    private static Geo2Tag instance = null;
    public ArrayList<String> subscribedChannels;

    protected Geo2Tag() {
        subscribedChannels = new ArrayList<String>();
    }

    public static Geo2Tag getInstance() {
        if (instance == null) {
            instance = new Geo2Tag();
        }
        return instance;
    }
}
