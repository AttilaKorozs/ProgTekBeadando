package org.rssreader.controller;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        // Esetlegesen betölthetsz itt alap nézetet
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
            e.printStackTrace(); // TODO: cseréld le Log4j2 logolásra
        }
    }
}