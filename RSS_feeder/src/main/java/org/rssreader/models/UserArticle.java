package org.rssreader.models;

import java.time.LocalDateTime;

public class UserArticle {
    private final String username;
    private final int articleId;
    private final boolean isFavorite;
    private final boolean isRead;
    private final LocalDateTime updatedAt;

    public UserArticle(String username, int articleId, boolean isFavorite, boolean isRead, LocalDateTime updatedAt) {
        this.username = username;
        this.articleId = articleId;
        this.isFavorite = isFavorite;
        this.isRead = isRead;
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public int getArticleId() {
        return articleId;
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
        return username + "-" + articleId;
    }
}