package org.rssreader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.models.User;
import org.rssreader.models.UserArticle;
import org.rssreader.service.decorator.CachedFavoriteDecorator;

public class UserArticleDAO {
    private static final Logger logger = LogManager.getLogger(CachedFavoriteDecorator.class);
    public static List<UserArticle> getUserArticle(User user, Feed feed) {
        List<Article> articles = ArticleDAO.getArticle(feed);
        List<UserArticle> userArticles = new ArrayList<UserArticle>();
        for (Article article : articles) {
            ResultSet rs = getUserData(user, article);
            try {
                userArticles.add(new UserArticle(user,
                        article,
                        rs.getBoolean("is_favorite"),
                        rs.getBoolean("is_read"),
                        (rs.getTimestamp("updated_at") == null)
                                ? LocalDateTime.now()
                                : rs.getTimestamp("updated_at").toLocalDateTime()));
            } catch (Exception e) {
                userArticles.add(new UserArticle(user,
                        article,
                        false,
                        false,
                        LocalDateTime.now()));
            }
        }
        return userArticles;
    }

    private static ResultSet getUserData(User user, Article article) {
        String sql = "SELECT is_favorite, is_read, updated_at FROM UserArticle WHERE user = ? AND article_id = ?";
        ResultSet rs;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, article.getId());
            rs = stmt.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return rs;
    }

    public static boolean setRead(User user, Article article, boolean isRead) {
        logger.info("setRead meghívva");
        String selectSql = "SELECT 1 FROM UserArticle WHERE user = ? AND article_id = ?";
        String updateSql = "UPDATE UserArticle SET is_read = ? WHERE user = ? AND article_id = ?";
        String insertSql = "INSERT INTO UserArticle (user, article_id, is_read, is_favorite) VALUES (?, ?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, user.getUsername());
                selectStmt.setInt(2, article.getId());

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setBoolean(1, isRead);
                            updateStmt.setString(2, user.getUsername());
                            updateStmt.setInt(3, article.getId());
                            return updateStmt.executeUpdate() > 0;
                        }
                    } else {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, user.getUsername());
                            insertStmt.setInt(2, article.getId());
                            insertStmt.setBoolean(3, isRead);
                            return insertStmt.executeUpdate() > 0;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setFavorite(User user, Article article, boolean isFavourite) {
        String selectSql = "SELECT is_favorite FROM UserArticle WHERE user = ? AND article_id = ?";
        String updateSql = "UPDATE UserArticle SET is_favorite = ? WHERE user = ? AND article_id = ?";
        String insertSql = "INSERT INTO UserArticle (user, article_id, is_read, is_favorite) VALUES (?, ?, 0, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, user.getUsername());
                selectStmt.setInt(2, article.getId());

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setBoolean(1, isFavourite);
                            updateStmt.setString(2, user.getUsername());
                            updateStmt.setInt(3, article.getId());
                            return updateStmt.executeUpdate() > 0;
                        }
                    } else {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, user.getUsername());
                            insertStmt.setInt(2, article.getId());
                            insertStmt.setBoolean(3, isFavourite);
                            return insertStmt.executeUpdate() > 0;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
