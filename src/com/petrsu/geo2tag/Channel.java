package com.petrsu.geo2tag;

/**
 * Created with IntelliJ IDEA.
 * User: nimnes
 * Date: 31.01.13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class Channel {
    String name = null;
    String description = null;
    boolean subscribed = false;

    public Channel(String name, String description, boolean subscribed) {
        super();
        this.name = name;
        this.description = description;
        this.subscribed = subscribed;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSubscribed() {
        return this.subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
