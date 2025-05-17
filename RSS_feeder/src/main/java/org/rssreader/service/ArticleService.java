package org.rssreader.service;

import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.util.LogUtil;
import org.rssreader.util.RssParser;
import org.rssreader.service.decorator.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;




public class ArticleService {
    //private final FeedService feedService = new FeedService();
    private final FeedService feedService = FeedService.getInstance();
    private final RssParser rssParser = new RssParser();

    public List<Article> getArticlesByFeeds(Feed feedToGet) {
        Feed feed = feedService.getAllFeeds().stream()
                .filter(f -> f.getUri() .equals(feedToGet.getUri()))
                .findFirst()
                .orElse(null);
        if (feed == null) {
            LogUtil.getLogger(ArticleService.class)
                    .warn("Feed not found for url: {}", feedToGet.getUri());
            return Collections.emptyList();
        }
        try {
            return rssParser.parse(feed.getUri());
        } catch (Exception e) {
            LogUtil.getLogger(ArticleService.class)
                    .error("Failed to parse RSS feed: {}", feed.getUri(), e);
            return Collections.emptyList();
        }
    }




    public List<ArticleComponent> getArticlesByFeed(Feed feed) {
        List<Article> articles;
        try {
            articles = rssParser.parse(feed.getUri());
        } catch (Exception e) {
            LogUtil.getLogger(ArticleService.class)
                    .error("RSS parse error for URL: {}", feed.getUri(), e);
            return Collections.emptyList();
        }

        // wrappelek BasicArticleComponent‐tel és dekorálok
        return articles.stream()
                .map(a -> new BasicArticleComponent(a))
                .map(ac -> new FavoriteDecorator(ac))
                .map(ac -> new ReadDecorator(ac))
                .collect(Collectors.toList());
    }
}




/*
public List<Article> getArticlesByFeed(int feedId) {
    return List.of(
            new Article(1, feedId, "Első cikk", LocalDateTime.now().minusDays(1), "Ez az első cikk tartalma."),
            new Article(2, feedId, "Második cikk", LocalDateTime.now(), "Ez a második cikk tartalma.")
    );
}*/