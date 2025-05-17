package org.rssreader.service.filter;

import org.rssreader.models.Article;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordFilter implements FilterStrategy {
    private final String keyword;

    public KeywordFilter(String keyword) {
        this.keyword = keyword == null ? "" : keyword.toLowerCase();
    }

    @Override
    public List<Article> filter(List<Article> articles) {
        if (keyword.isBlank()) return articles;
        return articles.stream()
                .filter(a -> a.getContent() != null
                        && a.getContent().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }
}
