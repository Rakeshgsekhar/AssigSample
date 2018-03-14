package com.webatron.rakesh.assignsample;

/**
 * Created by rakesh on 14/3/18.
 */

public class FileDetails {
    String name,url;

    public FileDetails(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
