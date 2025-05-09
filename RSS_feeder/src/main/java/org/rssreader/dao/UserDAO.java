package org.rssreader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.mindrot.jbcrypt.BCrypt;
import org.rssreader.models.User;

public class UserDAO {


    public static void addUser(User user) {
        String sql = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
