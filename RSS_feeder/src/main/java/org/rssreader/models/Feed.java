package org.rssreader.models;

import java.net.URI;

public class Feed {
    private final String name;
    private final URI uri;
    private final int refreshIntervalMin;

    public Feed(String name, URI uri, int refreshIntervalMin) {
        this.name = name;
        this.uri = uri;
        this.refreshIntervalMin = refreshIntervalMin;
    }

    public String getName() {
        return name;
    }

    public URI getUri() {
        return uri;
    }

    public int getRefreshIntervalMin() {
        return refreshIntervalMin;
    }

    @Override
    public String toString() {
        return name;
    }
}