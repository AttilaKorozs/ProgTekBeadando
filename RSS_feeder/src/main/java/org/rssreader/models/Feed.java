package org.rssreader.models;

public class Feed {
    private final String name;
    private final String url;
    private final int refreshIntervalMin;

    public Feed(String name, String url, int refreshIntervalMin) {
        this.name = name;
        this.url = url;
        this.refreshIntervalMin = refreshIntervalMin;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getRefreshIntervalMin() {
        return refreshIntervalMin;
    }

    @Override
    public String toString() {
        return name;
    }
}