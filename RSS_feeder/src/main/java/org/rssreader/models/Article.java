package org.rssreader.models;

import java.time.LocalDateTime;

public class Article {
    private int id;
    private final int feedId;
    private final String title;
    private final String link;
    private final LocalDateTime publicationDate;
    private final String content;


    public Article(int id, int feedId, String title, String link, LocalDateTime publicationDate, String content) {
        this.id = id;
        this.feedId = feedId;
        this.title = title;
        this.link = link;
        this.publicationDate = publicationDate;
        this.content = content;
    }

    public Article(int feedId, String title, String link, LocalDateTime publicationDate, String content) {
        this(0, feedId, title, link, publicationDate, content);
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

    public String getLink() {
        return link;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return title;
    }
}