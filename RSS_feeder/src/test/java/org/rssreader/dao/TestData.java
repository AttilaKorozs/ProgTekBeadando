package org.rssreader.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.rssreader.models.*;

public class TestData {
    static User TestUser = new User("teszt", "tesztpass", "teszt@teszt.hu");

    static Feed TestFeed = new Feed("FeedName", safeUri("http://www.feed.hu"), 20);
    static final Article TestArticle = new Article(TestFeed.getUri(), "Hatalmas Teszt Link", safeUri("http://teszt.link"),
            LocalDateTime.now(), "Szuper Content");

    private static URI safeUri(String uriStr) {
        try {
            return new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Hibás URI: " + uriStr, e);
        }
    }
}
