package org.rssreader.service.filter;

import org.rssreader.service.decorator.ArticleComponent;

import java.util.List;
import java.util.stream.Collectors;

public class TitleFilter implements FilterStrategy<ArticleComponent>  {
    private final String keyword;

    public TitleFilter(String keyword) {
        this.keyword = keyword == null ? "" : keyword.toLowerCase();
    }

    @Override
    public List<ArticleComponent> filter(List<ArticleComponent> articles) {
        if (keyword.isBlank()) return articles;
        return articles.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }
}
