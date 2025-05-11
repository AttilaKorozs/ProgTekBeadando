package org.rssreader.service;

import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.util.LogUtil;
import org.rssreader.util.RssParser;

import java.util.Collections;
import java.util.List;

public class ArticleService {
    private final FeedService feedService = new FeedService();
    private final RssParser rssParser = new RssParser();

    public List<Article> getArticlesByFeed(int feedId) {
        Feed feed = feedService.getAllFeeds().stream()
                .filter(f -> f.getId() == feedId)
                .findFirst()
                .orElse(null);
        if (feed == null) {
            LogUtil.getLogger(ArticleService.class)
                    .warn("Feed not found for id: {}", feedId);
            return Collections.emptyList();
        }
        try {
            return rssParser.parse(feed.getUrl(), feedId);
        } catch (Exception e) {
            LogUtil.getLogger(ArticleService.class)
                    .error("Failed to parse RSS feed: {}", feed.getUrl(), e);
            return Collections.emptyList();
        }
    }
}




/*
public List<Article> getArticlesByFeed(int feedId) {
    return List.of(
            new Article(1, feedId, "Első cikk", LocalDateTime.now().minusDays(1), "Ez az első cikk tartalma."),
            new Article(2, feedId, "Második cikk", LocalDateTime.now(), "Ez a második cikk tartalma.")
    );
}*/