package org.rssreader.models;

import java.net.URI;
import java.time.LocalDateTime;

public class Article {
    private int id;
    private final URI feedUri;
    private final String title;
    private final URI link;
    private final LocalDateTime publicationDate;
    private final String content;


    public Article(int id, URI feedUri, String title, URI link, LocalDateTime publicationDate, String content) {
        this.id = id;
        this.feedUri = feedUri;
        this.title = title;
        this.link = link;
        this.publicationDate = publicationDate;
        this.content = content;
    }

    public Article(URI feedUri, String title, URI link, LocalDateTime publicationDate, String content) {
        this(0, feedUri, title, link, publicationDate, content);
    }

    public int getId() {
        return id;
    }

    public URI getFeedUri() {
        return feedUri;
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

    public URI getLink() {
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