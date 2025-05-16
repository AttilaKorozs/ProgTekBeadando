package org.rssreader.models;

import java.time.LocalDateTime;

public class UserArticle {
    private final String username;
    private final Article article;
    private final boolean isFavorite;
    private final boolean isRead;
    private final LocalDateTime updatedAt;

    public UserArticle(String username, Article article, boolean isFavorite, boolean isRead, LocalDateTime updatedAt) {
        this.username = username;
        this.article = article;
        this.isFavorite = isFavorite;
        this.isRead = isRead;
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public Article getArticle() {
        return article;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return username + "-" + article;
    }
}