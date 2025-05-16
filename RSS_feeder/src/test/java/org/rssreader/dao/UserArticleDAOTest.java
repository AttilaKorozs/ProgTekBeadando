package org.rssreader.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rssreader.models.UserArticle;

public class UserArticleDAOTest {
    List<UserArticle> articles;

    @BeforeAll
    static void addTestData() {
        UserDAO.addUser(TestData.TestUser);
        FeedDAO.addFeed(TestData.TestFeed);
        ArticleDAO.storeArticle(TestData.TestArticle);
    }

    @Test
    void testGetUserArticle() {
        articles = UserArticleDAO.getUserArticle(TestData.TestUser, TestData.TestFeed);
        assertFalse(articles.get(0).isFavorite());
        assertFalse(articles.get(0).isRead());
        assertEquals(articles.size(), 1);

        articles.get(0).setRead();
        assertTrue(articles.get(0).isRead());

        articles.get(0).setFavorite();
        assertTrue(articles.get(0).isFavorite());
    }

    @AfterAll
    static void removeTestData() {
        ArticleDAO.removeArticle(TestData.TestArticle);
        UserDAO.removeUser(TestData.TestUser);
        FeedDAO.removeFeed(TestData.TestFeed);
    }
}
