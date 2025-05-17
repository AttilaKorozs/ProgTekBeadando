package org.rssreader.service.filter;

import org.rssreader.models.Article;
import java.util.List;

public interface FilterStrategy {
    /**
     * Visszaadja a bemeneti lista azon elemeit, amelyek megfelelnek a stratégiának.
     */
    List<Article> filter(List<Article> articles);
}
