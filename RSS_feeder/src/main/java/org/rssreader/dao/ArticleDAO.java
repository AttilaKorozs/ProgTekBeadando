package org.rssreader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.rssreader.models.Article;

public class ArticleDAO {

    public static void storeArticle(Article article) {
        String sql = "INSERT INTO Article (feed_id, title, link, publication_date, content) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, article.getFeedId());
            stmt.setString(2, article.getTitle());
           // stmt.setString(3, article.getLink());
            stmt.setTimestamp(4, Timestamp.valueOf(article.getPublicationDate()));
            stmt.setString(5, article.getContent());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("A beszúrás nem járt sikerrel, nem hozott létre sort.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
           //         article.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Nem sikerült lekérni a generált kulcsot.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
