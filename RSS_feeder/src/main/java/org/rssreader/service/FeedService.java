package org.rssreader.service;

import org.rssreader.dao.FeedDAO;
import org.rssreader.models.Feed;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedService {
    private static final FeedService INSTANCE = new FeedService();

    public static FeedService getInstance() {
        return INSTANCE;
    }

    private List<Feed> feeds = new ArrayList<>();

    public FeedService() {

    }

    public List<Feed> getAllFeeds() {
        feeds.clear();
        feeds.addAll(FeedDAO.getFeedList());
        return Collections.unmodifiableList(feeds);
    }

    public void addFeed(Feed feed) {
        feeds.add(feed);
    }

    public void deleteFeed(URI uri) {
        feeds.removeIf(f -> f.getUri() == uri);
    }

    /** URI alapján visszaadja a pontos Feed példányt */
    public Feed getFeedByUri(URI uri) {
        return getAllFeeds().stream()
                .filter(f -> f.getUri().equals(uri))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown feed URI: " + uri));
    }
}