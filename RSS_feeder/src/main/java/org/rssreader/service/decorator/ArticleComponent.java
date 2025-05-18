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
    void setFavorite(boolean fav);
    void setRead(boolean read);
    Article getModel();
}
