package org.rssreader.models;

import java.time.LocalDateTime;

public class Article {
    private final int id;
    private final int feedId;
    private final String title;
    private final LocalDateTime publicationDate;
    private final String content;

    public Article(int id, int feedId, String title, LocalDateTime publicationDate, String content) {
        this.id = id;
        this.feedId = feedId;
        this.title = title;
        this.publicationDate = publicationDate;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public int getFeedId() {
        return feedId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return title;
    }
}