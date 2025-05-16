package org.rssreader.service;

import org.rssreader.models.Feed;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedService {
    private static final FeedService INSTANCE = new FeedService();

    public static FeedService getInstance() {
        return INSTANCE;
    }

    private final List<Feed> feeds = new ArrayList<>();

    public FeedService() {
        // Kezdeti stub adatok
        try {
            feeds.add(new Feed("Example RSS", new URI("https://example.com/rss"), 30));
            feeds.add(new Feed("News Feed", new URI("https://news.example.com/rss"), 15));
            feeds.add(new Feed("Real feed", new URI("https://news.un.org/feed/subscribe/en/news/all/rss.xml"), 15));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public List<Feed> getAllFeeds() {
        return Collections.unmodifiableList(feeds);
    }

    public void addFeed(Feed feed) {
        feeds.add(feed);
    }

    public void deleteFeed(URI uri) {
        feeds.removeIf(f -> f.getUri() == uri);
    }
}