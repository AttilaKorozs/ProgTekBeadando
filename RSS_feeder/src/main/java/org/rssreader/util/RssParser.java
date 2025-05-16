package org.rssreader.util;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.rssreader.models.Article;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class RssParser {
    public List<Article> parse(URI feedUri) throws Exception {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(feedUri.toURL()));
        return feed.getEntries().stream()
                .map(entry -> toArticle(entry, feedUri))
                .collect(Collectors.toList());
    }

    private Article toArticle(SyndEntry entry, URI feedUri) {
        String content = "";

        if (entry.getDescription() != null && entry.getDescription().getValue() != null) {
            content = entry.getDescription().getValue();
        } else if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            content = entry.getContents().stream()
                    .map(SyndContent::getValue)
                    .collect(Collectors.joining(" "));
        }

        URI linkUri;
        try {
            linkUri = entry.getLink() != null ? new URI(entry.getLink()) : new URI("http://unknown.link");
        } catch (URISyntaxException e) {
            linkUri = URI.create("http://invalid.link");
        }

        return new Article(
                feedUri,
                entry.getTitle(),
                linkUri,
                entry.getPublishedDate() != null
                        ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.now(),
                content
        );
    }

}