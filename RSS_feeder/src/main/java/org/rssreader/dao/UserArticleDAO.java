package org.rssreader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.models.User;
import org.rssreader.models.UserArticle;

public class UserArticleDAO {
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
        String sql = "SELECT is_favourite, is_read, updated_at, FROM UserArticle WHERE user = ? AND article_id = ?";
        ResultSet rs;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(1, article.getId());
            rs = stmt.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return rs;
    }

    public static boolean setRead(User user, Article article) {
        String selectSql = "SELECT 1 FROM UserArticle WHERE user = ? AND article_id = ?";
        String updateSql = "UPDATE UserArticle SET is_read = 1 WHERE user = ? AND article_id = ?";
        String insertSql = "INSERT INTO UserArticle (user, article_id, is_read, is_favorite) VALUES (?, ?, 1, 0)";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, user.getUsername());
                selectStmt.setInt(2, article.getId());

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, user.getUsername());
                            updateStmt.setInt(2, article.getId());
                            return updateStmt.executeUpdate() > 0;
                        }
                    } else {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, user.getUsername());
                            insertStmt.setInt(2, article.getId());
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

    public static boolean setFavorite(User user, Article article) {
        String selectSql = "SELECT is_favorite FROM UserArticle WHERE user = ? AND article_id = ?";
        String updateSql = "UPDATE UserArticle SET is_favorite = 1 WHERE user = ? AND article_id = ?";
        String insertSql = "INSERT INTO UserArticle (user, article_id, is_read, is_favorite) VALUES (?, ?, 0, 1)";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, user.getUsername());
                selectStmt.setInt(2, article.getId());

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, user.getUsername());
                            updateStmt.setInt(2, article.getId());
                            return updateStmt.executeUpdate() > 0;
                        }
                    } else {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, user.getUsername());
                            insertStmt.setInt(2, article.getId());
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
