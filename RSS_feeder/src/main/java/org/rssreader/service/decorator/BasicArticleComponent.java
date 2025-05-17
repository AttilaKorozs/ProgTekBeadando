package org.rssreader.service.decorator;

import org.rssreader.models.Article;
import java.net.URI;
import java.time.LocalDateTime;

public class BasicArticleComponent implements ArticleComponent {
    private final Article article;

    public BasicArticleComponent(Article article) {
        this.article = article;
    }

    @Override public String getTitle()               { return article.getTitle(); }
    @Override public URI getLink()                   { return article.getLink(); }
    @Override public LocalDateTime getPublicationDate() { return article.getPublicationDate(); }
    @Override public String getContent()             { return article.getContent(); }

    /** alapból nincs státusz, dekorátorokban lesz felülírva */
    @Override public boolean isFavorite()            { return false; }
    @Override public boolean isRead()                { return false; }

    @Override public void setFavorite(boolean fav)   { /* noop */ }
    @Override public void setRead(boolean read)      { /* noop */ }

    @Override public Article getModel()              { return article; }
}
