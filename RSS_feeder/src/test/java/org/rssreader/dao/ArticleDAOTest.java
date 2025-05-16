package org.rssreader.dao;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rssreader.models.Article;

public class ArticleDAOTest {

    @BeforeAll
    static void AddTestUser() {
        UserDAO.addUser(TestData.TestUser);
        FeedDAO.addFeed(TestData.TestFeed);
    }

    @Test
    void testStoreArticle() {
        assertEquals(ArticleDAO.getArticle(TestData.TestFeed).size(), 0);
        ArticleDAO.storeArticle(TestData.TestArticle);
        assertEquals(ArticleDAO.getArticle(TestData.TestFeed).size(), 1);
        ArticleDAO.removeArticle(TestData.TestArticle);
        assertEquals(ArticleDAO.getArticle(TestData.TestFeed).size(), 0);

    }

    @AfterAll
    static void removeTestData() {
        

        List<Article> testArticles = ArticleDAO.getArticle(TestData.TestFeed);
        for (Article article : testArticles) {
            ArticleDAO.removeArticle(article);
        }

        UserDAO.removeUser(TestData.TestUser);
        FeedDAO.removeFeed(TestData.TestFeed);
    }
}
