package org.rssreader.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.service.ArticleService;
import org.rssreader.service.filter.*;
import org.rssreader.service.FeedService;

import java.time.LocalDate;
import java.util.List;

public class ArticleController {
    @FXML private ChoiceBox<String> choiceFilter;
    @FXML private TextField txtKeyword;
    @FXML private DatePicker datePicker;

    @FXML private ListView<Feed> feedList;
    @FXML private TableView<Article> articleTable;
    @FXML private TableColumn<Article, String> colTitle;
    @FXML private TableColumn<Article, java.time.LocalDateTime> colDate;
    @FXML private TextArea txtContent;

    private final FeedService feedService = FeedService.getInstance();
    private final ArticleService articleService = new ArticleService();
    private List<Article> originalArticles;  // tábla eredeti tartalma

    @FXML
    public void initialize() {
        // ChoiceBox feltöltése
        choiceFilter.setItems(FXCollections.observableArrayList(
                "All", "Title", "Date", "Keyword"
        ));
        choiceFilter.getSelectionModel().selectFirst();

        // Szűrő mezők elrejtése
        txtKeyword.setVisible(false);
        datePicker.setVisible(false);

        // ChoiceBox váltás hatása
        choiceFilter.getSelectionModel().selectedItemProperty().addListener((obs, old, choice) -> {
            boolean isKeyword = choice.equals("Title") || choice.equals("Keyword");
            txtKeyword.setVisible(isKeyword);
            datePicker.setVisible(choice.equals("Date"));
        });

        // Táblázat oszlopok beállítása
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));

        // Feed lista feltöltése és kiválasztási listener
        feedList.setItems(FXCollections.observableList(feedService.getAllFeeds()));
        feedList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldF, newF) -> {
                    if (newF != null) loadArticles(newF);
                });

        // Alapértelmezett feed kiválasztása
        if (!feedList.getItems().isEmpty()) {
            feedList.getSelectionModel().selectFirst();
        }

        // Sor kiválasztása → tartalom megjelenítés
        articleTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldA, newA) -> {
                    txtContent.setText(newA != null ? newA.getContent() : "");
                });
    }

    /** Betölti a cikkeket és elmenti a teljes listát. */
    private void loadArticles(Feed feed) {
        originalArticles = articleService.getArticlesByFeed(feed);
        applyFilter();
    }

    /** Szűrés lefuttatása a kiválasztott stratégia szerint. */
    @FXML
    private void onApplyFilter() {
        applyFilter();
    }

    private void applyFilter() {
        String choice = choiceFilter.getValue();
        FilterStrategy strategy;

        switch (choice) {
            case "Title":
                strategy = new TitleFilter(txtKeyword.getText());
                break;
            case "Keyword":
                strategy = new KeywordFilter(txtKeyword.getText());
                break;
            case "Date":
                LocalDate d = datePicker.getValue();
                strategy = new DateFilter(d);
                break;
            default:  // "All"
                strategy = articles -> articles;
        }

        List<Article> filtered = strategy.filter(originalArticles);
        articleTable.setItems(FXCollections.observableList(filtered));
        if (!filtered.isEmpty()) {
            articleTable.getSelectionModel().selectFirst();
        }
    }
}
