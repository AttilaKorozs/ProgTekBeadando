package org.rssreader.dao;
import java.time.LocalDateTime;

import org.rssreader.models.*;

public class TestData {
    static User TestUser = new User("teszt", "tesztpass", "teszt@teszt.hu");
    static Feed TestFeed = new Feed("FeedName","http://www.feed.hu",20);
    static final Article TestArticle = new Article(1, "Hatalmas Teszt Link", "http://teszt.link",
            LocalDateTime.now(), "Szuper Content");
}
