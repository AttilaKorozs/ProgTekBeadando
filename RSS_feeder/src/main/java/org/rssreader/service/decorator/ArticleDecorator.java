package org.rssreader.service.decorator;

import org.rssreader.models.Article;

import java.net.URI;
import java.time.LocalDateTime;

public abstract class ArticleDecorator implements ArticleComponent {
    protected final ArticleComponent wrappee;

    protected ArticleDecorator(ArticleComponent wrappee) {
        this.wrappee = wrappee;
    }

    @Override public String getTitle()               { return wrappee.getTitle(); }
    @Override public URI getLink()                   { return wrappee.getLink(); }
    @Override public LocalDateTime getPublicationDate() { return wrappee.getPublicationDate(); }
    @Override public String getContent()             { return wrappee.getContent(); }
    @Override public Article getModel()               { return wrappee.getModel(); }

    // Ezekre **nem** kell felülírni semmit, mert az alap wrappee-je
    // (BasicArticleComponent) hamisra állítja őket, ha soha nem hívtad meg:
    @Override public boolean isFavorite()             { return wrappee.isFavorite(); }
    @Override public void setFavorite(boolean fav)    { wrappee.setFavorite(fav); }
    @Override public boolean isRead()                 { return wrappee.isRead(); }
    @Override public void setRead(boolean read)       { wrappee.setRead(read); }
}
