package org.rssreader.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import org.rssreader.dao.ArticleDAO;
import org.rssreader.dao.UserArticleDAO;
import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.models.UserArticle;
import org.rssreader.service.FeedService;
import org.rssreader.service.decorator.ArticleComponent;
import org.rssreader.service.decorator.BasicArticleComponent;
import org.rssreader.service.decorator.CachedFavoriteDecorator;
import org.rssreader.service.decorator.CachedReadDecorator;
import org.rssreader.service.filter.DateFilter;
import org.rssreader.service.filter.FilterStrategy;
import org.rssreader.service.filter.KeywordFilter;
import org.rssreader.service.filter.TitleFilter;
import org.rssreader.util.RssParser;
import org.rssreader.util.Session;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArticleController {

    // --- Szűrési UI ---
    @FXML
    private ChoiceBox<String> choiceFilter;
    @FXML
    private TextField txtKeyword;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button btnApply;

    // --- Fő tartalom ---
    @FXML
    private ListView<Feed> feedList;
    @FXML
    private TableView<ArticleComponent> articleTable;
    @FXML
    private TableColumn<ArticleComponent, String> colTitle;
    @FXML
    private TableColumn<ArticleComponent, LocalDateTime> colDate;
    @FXML
    private TableColumn<ArticleComponent, Boolean> colFavorite;
    @FXML
    private TableColumn<ArticleComponent, Boolean> colRead;
    @FXML
    private TextArea txtContent;

    private final FeedService feedService = FeedService.getInstance();
    private List<ArticleComponent> originalArticles;

    @FXML
    private void onApplyFilter() {
        applyFilter();
    }

    @FXML
    public void initialize() {
        choiceFilter.setItems(FXCollections.observableArrayList("All", "Title", "Date", "Keyword"));
        choiceFilter.getSelectionModel().select("All");
        txtKeyword.setVisible(false);
        datePicker.setVisible(false);
        choiceFilter.getSelectionModel().selectedItemProperty()
                .addListener((_, _, choice) -> {
                    txtKeyword.setVisible("Title".equals(choice) || "Keyword".equals(choice));
                    datePicker.setVisible("Date".equals(choice));
                });
        btnApply.setOnAction(_ -> applyFilter());
        colTitle.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getTitle()));
        colDate.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getPublicationDate()));
        articleTable.setEditable(true);
        colFavorite.setOnEditCommit(e -> {
            ArticleComponent ac = e.getRowValue();
            ac.setFavorite(e.getNewValue());
            articleTable.refresh();
        });
        colRead.setOnEditCommit(e -> {
            ArticleComponent ac = e.getRowValue();
            ac.setRead(e.getNewValue());
            articleTable.refresh();
        });

        colFavorite.setCellValueFactory(cd -> {
            BooleanProperty prop = new SimpleBooleanProperty(cd.getValue().isFavorite());
            prop.addListener((_, _, newVal) -> cd.getValue().setFavorite(newVal));
            return prop;
        });
        colFavorite.setCellFactory(_ -> new CheckBoxTableCell<>());

        colRead.setCellValueFactory(cd -> {
            BooleanProperty prop = new SimpleBooleanProperty(cd.getValue().isRead());
            prop.addListener((_, _, newVal) -> cd.getValue().setRead(newVal));
            return prop;
        });
        colRead.setCellFactory(_ -> new CheckBoxTableCell<>());

        feedList.setItems(FXCollections.observableList(feedService.getAllFeeds()));
        feedList.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newF) -> {
                    if (newF != null)
                        loadArticles(newF);
                });

        if (!feedList.getItems().isEmpty()) {
            feedList.getSelectionModel().selectFirst();
        }

        articleTable.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newA) -> {
                    txtContent.setText(newA != null ? newA.getContent() : "");
                });
    }


    @FXML
    private void loadArticles(Feed feed) {
        btnApply.setDisable(true);
        RssParser rssParser = new RssParser();
        Task<List<Article>> fetchTask = new Task<>() {
            @Override
            protected List<Article> call() throws Exception {
                // ez most már háttérszálon fut!
                return rssParser.parse(feed.getUri());
            }
        };

        fetchTask.setOnSucceeded(_ -> {
            List<Article> parsedArticles = fetchTask.getValue();
            List<UserArticle> storedArticles = UserArticleDAO.getUserArticle(Session.getCurrentUser(),feed);
            List<Article> newArticles = parsedArticles.stream()
                    .filter(pa -> storedArticles.stream()
                            .noneMatch(sa -> sa.getArticle().getTitle().equalsIgnoreCase(pa.getTitle())
                                    && sa.getArticle().getLink().equals(pa.getLink())))
                    .toList();

            newArticles.forEach(ArticleDAO::storeArticle);
            List<UserArticle> articles = UserArticleDAO.getUserArticle(Session.getCurrentUser(),feed);
            Map<Integer, UserArticle> statusMap = articles.stream()
                    .collect(Collectors.toMap(
                            ua -> ua.getArticle().getId(),
                            ua -> ua));

            originalArticles = articles.stream()
                    .map(a -> new BasicArticleComponent(a.getArticle()))
                    .map(c -> new CachedFavoriteDecorator(c, statusMap))
                    .map(c -> new CachedReadDecorator(c, statusMap))
                    .collect(Collectors.toList());

            applyFilter(); 
            btnApply.setDisable(false);
        });

        fetchTask.setOnFailed(_ -> {
            Throwable err = fetchTask.getException();
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR,
                        "Nem sikerült betölteni a feedet:\n" + err.getMessage())
                        .showAndWait();
                btnApply.setDisable(false);
            });
        });

        new Thread(fetchTask, "rss-fetch-thread").start();
    }

    private void applyFilter() {
        String choice = choiceFilter.getValue();
        FilterStrategy<ArticleComponent> strategy;
        switch (choice) {
            case "Title":
                strategy = new TitleFilter(txtKeyword.getText());
                break;
            case "Keyword":
                strategy = new KeywordFilter(txtKeyword.getText());
                break;
            case "Date":
                strategy = new DateFilter(datePicker.getValue());
                break;
            default: // "All"
                strategy = articles -> articles;
        }
        List<ArticleComponent> filtered = strategy.filter(originalArticles);
        articleTable.setItems(FXCollections.observableList(filtered));
        if (!filtered.isEmpty()) {
            articleTable.getSelectionModel().selectFirst();
        }
    }
}
