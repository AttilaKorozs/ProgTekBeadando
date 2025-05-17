package org.rssreader.models;

import java.time.LocalDateTime;

import org.rssreader.dao.UserArticleDAO;

public class UserArticle {
    private final User user;
    private final Article article;
    private boolean isFavorite;
    private boolean isRead;
    private final LocalDateTime updatedAt;

    public UserArticle(User user, Article article, boolean isFavorite, boolean isRead, LocalDateTime updatedAt) {
        this.user = user;
        this.article = article;
        this.isFavorite = isFavorite;
        this.isRead = isRead;
        this.updatedAt = updatedAt;
    }

    public User getUsername() {
        return user;
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

    public void setRead() {
        if (UserArticleDAO.setRead(user, article))
            isRead=true;
    }

    public void unsetRead() {
        if (UserArticleDAO.setRead(user, article))
            isRead=false;
    }
    public void setFavorite() {
        if (UserArticleDAO.setFavorite(user, article))
            isFavorite=true;
    }

    public void unsetFavorite() {
        if (UserArticleDAO.setFavorite(user, article))
            isFavorite=false   ;
    }

    @Override
    public String toString() {
        return user.getUsername() + "-" + article;
    }
}