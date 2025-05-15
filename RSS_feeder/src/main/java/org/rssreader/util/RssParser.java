package org.rssreader.util;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.rssreader.models.Article;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class RssParser {
    public List<Article> parse(String feedUrl, int feedId) throws Exception {
        URL url = new URL(feedUrl);
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(url));
        return feed.getEntries().stream()
                .map(entry -> toArticle(entry, feedId))
                .collect(Collectors.toList());
    }

    private Article toArticle(SyndEntry entry, int feedId) {
        // Először a description-t próbáljuk, ha nincs, akkor a contents listából veszünk értéket
        String content = "";
        if (entry.getDescription() != null && entry.getDescription().getValue() != null) {
            content = entry.getDescription().getValue();
        } else if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            content = entry.getContents().stream()
                    .map(SyndContent::getValue)
                    .collect(Collectors.joining(" "));
        }
        return new Article(
                0,
                feedId,
                entry.getTitle(),
                "www.alma.hu",
                entry.getPublishedDate() != null
                        ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.now(),
                content
        );
    }
}