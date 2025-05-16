package org.rssreader.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.service.ArticleService;
import org.rssreader.service.FeedService;

import java.util.List;

public class ArticleController {
    @FXML private ListView<Feed> feedList;
    @FXML private TableView<Article> articleTable;
    @FXML private TableColumn<Article, String> colTitle;
    @FXML private TableColumn<Article, java.time.LocalDateTime> colDate;
    @FXML private TextArea txtContent;

    private final FeedService feedService = FeedService.getInstance();
    private final ArticleService articleService = new ArticleService();

    @FXML
    public void initialize() {
        // 1. Feed lista feltöltése
        feedList.setItems(FXCollections.observableList(feedService.getAllFeeds()));

        // 2. Táblázat konfigurálása
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDate .setCellValueFactory(new PropertyValueFactory<>("publicationDate"));

        // 3. Lista kiválasztás‐változás listener
        feedList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldF, newF) -> {
                    if (newF != null) loadArticles(newF);
                });

        // 4. Tábla sor kiválasztás‐változás listener
        articleTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldA, newA) -> {
                    if (newA != null) txtContent.setText(newA.getContent());
                });

        // 5. Alapértelmezett első feed kiválasztása
        if (!feedList.getItems().isEmpty()) {
            feedList.getSelectionModel().selectFirst();
        }
    }

    private void loadArticles(Feed feed) {
        List<Article> articles = articleService.getArticlesByFeed(feed);
        articleTable.setItems(FXCollections.observableList(articles));
        txtContent.clear();
        // Opcionálisan az első cikkre is rákattintunk
        if (!articles.isEmpty()) {
            articleTable.getSelectionModel().selectFirst();
        }
            
    }
}
