package org.rssreader.models;

public class Feed {
    private final int id;
    private final String name;
    private final String url;
    private final int refreshIntervalMin;

    public Feed(int id, String name, String url, int refreshIntervalMin) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.refreshIntervalMin = refreshIntervalMin;
    }

    public int getId() {
        return id;
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