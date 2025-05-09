package org.rssreader.models;

import java.time.LocalDateTime;

public class Article {

    private int id;
    private int feedId;
    private String title;
    private String link;
    private String content;
    private LocalDateTime publicationDate;

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

    public int getFeedId() {
        return feedId;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setId(int id) {
        this.id = id;
    }

}
