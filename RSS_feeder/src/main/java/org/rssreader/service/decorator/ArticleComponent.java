package org.rssreader.service.decorator;

import org.rssreader.models.Article;

import java.net.URI;
import java.time.LocalDateTime;

public interface ArticleComponent {
    String getTitle();
    URI getLink();
    LocalDateTime getPublicationDate();
    String getContent();

    boolean isFavorite();
    boolean isRead();

    /** Beállítja a kedvenc-státuszt (DAO hívás itt történik) */
    void setFavorite(boolean fav);

    /** Beállítja az olvasott-státuszt (DAO hívás itt történik) */
    void setRead(boolean read);

    Article getModel();
}
