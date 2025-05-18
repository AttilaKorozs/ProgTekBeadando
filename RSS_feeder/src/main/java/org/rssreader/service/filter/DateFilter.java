package org.rssreader.service.filter;

import org.rssreader.service.decorator.ArticleComponent;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DateFilter implements FilterStrategy<ArticleComponent>  {
    private final LocalDate date;

    public DateFilter(LocalDate date) {
        this.date = date;
    }

    @Override
    public List<ArticleComponent>  filter(List<ArticleComponent>  articles) {
        if (date == null) return articles;
        return articles.stream()
                .filter(a -> a.getPublicationDate() != null
                        && a.getPublicationDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
}
