package org.rssreader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.rssreader.models.Article;

public class ArticleDAO {

    public static void storeArticle(Article article) {
        String sql = "INSERT INTO Article (feed_id, title, link, publication_date, content) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, article.getFeedId());
            stmt.setString(2, article.getTitle());
            stmt.setString(3, article.getLink());
            stmt.setTimestamp(4, Timestamp.valueOf(article.getPublicationDate()));
            stmt.setString(5, article.getContent());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("A beszúrás nem járt sikerrel, nem hozott létre sort.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Nem sikerült lekérni a generált kulcsot.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Article> getArticle(int feedId) {
        String sql = "SELECT id, feed_id, title, link, publication_date, content FROM Article WHERE feed_id = ?";
        List<Article> articles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, feedId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article(rs.getInt("id"),
                            rs.getInt("feed_id"),
                            rs.getString("title"),
                            rs.getString("link"),
                            rs.getTimestamp("publication_date").toLocalDateTime(),
                            rs.getString("content"));
                    articles.add(article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return articles;
    }

    public static void removeArticle(Article article) {
        String sql = "DELETE FROM Article WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, article.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Nem található cikk ezzel az ID-val: " + article.getId());
            } else {
                System.out.println("Cikk sikeresen törölve. ID: " + article.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
