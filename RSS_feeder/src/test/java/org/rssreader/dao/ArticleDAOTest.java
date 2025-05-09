package org.rssreader.dao;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.rssreader.models.Article;

public class ArticleDAOTest {

    private static final Article TestArticle = new Article(1, "tesztTitle", "http://teszt.link", LocalDateTime.now(), "Hatalmas Teszt Content");

    @Test
    void testStoreArticle() {
        ArticleDAO.storeArticle(TestArticle);
    }
}
