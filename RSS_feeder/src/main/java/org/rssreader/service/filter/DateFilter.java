package org.rssreader.service.filter;

import org.rssreader.models.Article;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DateFilter implements FilterStrategy {
    private final LocalDate date;

    public DateFilter(LocalDate date) {
        this.date = date;
    }

    @Override
    public List<Article> filter(List<Article> articles) {
        if (date == null) return articles;
        return articles.stream()
                .filter(a -> a.getPublicationDate() != null
                        && a.getPublicationDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
}
