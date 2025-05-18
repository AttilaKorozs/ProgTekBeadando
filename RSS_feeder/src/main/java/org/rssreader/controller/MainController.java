package org.rssreader.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        onShowFeeds();
    }

    @FXML
    private void onShowFeeds() {
        loadView("/fxml/feed.fxml");
    }

    @FXML
    private void onShowArticles() {
        loadView("/fxml/article.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            logger.error("Nem sikerült betölteni a nézetet: {}", fxmlPath, e);
        }
    }
}