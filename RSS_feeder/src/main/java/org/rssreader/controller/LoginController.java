package org.rssreader.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rssreader.dao.UserDAO;
import org.rssreader.models.User;
import org.rssreader.util.Session;

import java.io.IOException;

/**
 * Kezeli a bejelentkezést és regisztrációt.
 */
public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtEmail;

    // a DAO statikus metódusokat használ
    //private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // A DAO authUser(User) metódusát így kell hívni:
        User attempt = new User(username, password, null);
        User user = UserDAO.authUser(attempt);

        if (user != null) {
            // beállítjuk a session-be
            Session.setCurrentUser(user);
            logger.info("User '{}' logged in", user.getUsername());
            // és elindítjuk a főnézetet
            openMainView(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid username or password.");
            logger.warn("Failed login attempt for username '{}'", username);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String email    = txtEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please fill all fields.");
            return;
        }

        // itt az addUser(User) várt paraméterrel
        User newUser = new User(username, password, email);
        boolean ok = UserDAO.addUser(newUser);

        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Registration", "User registered successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration", "Could not register user.");
        }
    }

    private void openMainView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
