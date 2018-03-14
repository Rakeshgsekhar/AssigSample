package com.webatron.rakesh.assignsample;

/**
 * Created by rakesh on 13/3/18.
 */

public class UserData {
    String name,mob,url;

    public UserData(String name, String mob,String url) {
        this.name = name;
        this.mob = mob;
        this.url = url;

    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

}
