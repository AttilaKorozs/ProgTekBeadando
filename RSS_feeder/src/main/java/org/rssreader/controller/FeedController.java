package org.rssreader.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.rssreader.exception.FeedNotFoundException;
import org.rssreader.dao.FeedDAO;
import org.rssreader.models.Feed;
import org.rssreader.service.FeedService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rssreader.util.Session;

public class FeedController {
    private static final Logger logger = LogManager.getLogger(FeedController.class);
    @FXML private TableView<Feed> feedTable;
    @FXML private TableColumn<Feed, Integer> colId;
    @FXML private TableColumn<Feed, String> colName;
    @FXML private TableColumn<Feed, String> colUrl;
    @FXML private TableColumn<Feed, Integer> colInterval;

    private final FeedService feedService = FeedService.getInstance();

    @FXML public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUrl.setCellValueFactory(new PropertyValueFactory<>("url"));
        colInterval.setCellValueFactory(new PropertyValueFactory<>("refreshIntervalMin"));
        refreshTable();
    }

    private void refreshTable() {
        feedTable.setItems(FXCollections.observableList(feedService.getAllFeeds()));
    }

    @FXML private void onAddFeed() {
        Dialog<Feed> dialog = new Dialog<>();
        dialog.setTitle("Új feed hozzáadása");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField urlField = new TextField();
        Spinner<Integer> intervalSpinner = new Spinner<>();
        intervalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1440, 30));

        grid.add(new Label("Név:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("URL:"), 0, 1);
        grid.add(urlField, 1, 1);
        grid.add(new Label("Frissítési intervallum (perc):"), 0, 2);
        grid.add(intervalSpinner, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new Feed(nameField.getText(), new URI(urlField.getText()), intervalSpinner.getValue());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        Optional<Feed> result = dialog.showAndWait();
        result.ifPresent(feed -> {
            FeedDAO.addFeed(feed);
            feedService.addFeed(feed);
            logger.info("User '{}' added new feed with URL '{}'",
                    Session.getCurrentUser().getUsername(),
                    feed.getUrl());
            refreshTable();
        });
    }

    @FXML private void onDeleteFeed() {
        Feed selected = feedTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FeedDAO.removeFeed(selected);
            feedService.deleteFeed(selected.getUri());
            refreshTable();
        }
        else {
           throw new FeedNotFoundException("Feed not found ");
        }
    }
}