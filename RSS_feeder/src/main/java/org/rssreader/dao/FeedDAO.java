package org.rssreader.dao;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rssreader.controller.FeedController;
import org.rssreader.models.Feed;
import org.rssreader.util.Session;

public class FeedDAO {
    private static final Logger logger = LogManager.getLogger(FeedController.class);

    public static void addFeed(Feed feed) {
        String sql = "INSERT INTO Feed (name, url, refresh_interval_min) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, feed.getName());
            stmt.setString(2, feed.getUri().toString());
            stmt.setInt(3, feed.getRefreshIntervalMin());

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.warn("User '{}':Database error while addig feed: '{}'",
                    Session.getCurrentUser().getUsername(),
                    feed.getUrl());
            e.printStackTrace();
        }
    }

    public static List<Feed> getFeedList() {
        String sql = "SELECT * FROM Feed";
        List<Feed> feeds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Feed feed = new Feed(
                        rs.getString("name"),
                        new URI(rs.getString("url")),
                        rs.getInt("refresh_interval_min"));
                feeds.add(feed);
            }

        } catch (Exception e) {
            logger.warn("User '{}':Database error while getting feed list.",
                    Session.getCurrentUser().getUsername());
            e.printStackTrace();
            return null; // vagy üres lista: return Collections.emptyList();
        }

        return feeds;
    }

    public static boolean removeFeed(Feed feed) {
        String sql = "DELETE FROM Feed WHERE url = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, feed.getUri().toString());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            logger.warn("User '{}':Database error while removing feed: '{}'",
                    Session.getCurrentUser().getUsername(),
                    feed.getUrl());
            e.printStackTrace();
            return false;
        }

    }

}
