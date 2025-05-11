package org.rssreader.service;
import org.rssreader.models.Feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FeedService {
    private static final FeedService INSTANCE = new FeedService();
    public static FeedService getInstance() { return INSTANCE; }
    private final List<Feed> feeds = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public FeedService() {
        // Kezdeti stub adatok
        feeds.add(new Feed(idCounter.getAndIncrement(), "Example RSS", "https://example.com/rss", 30));
        feeds.add(new Feed(idCounter.getAndIncrement(), "News Feed", "https://news.example.com/rss", 15));
        feeds.add(new Feed(idCounter.getAndIncrement(), "Real feed", "https://news.un.org/feed/subscribe/en/news/all/rss.xml", 15));
    }

    public List<Feed> getAllFeeds() {
        return Collections.unmodifiableList(feeds);
    }

    public void addFeed(Feed feed) {
        int id = idCounter.getAndIncrement();
        feeds.add(new Feed(id, feed.getName(), feed.getUrl(), feed.getRefreshIntervalMin()));
    }

    public void deleteFeed(int id) {
        feeds.removeIf(f -> f.getId() == id);
    }
}