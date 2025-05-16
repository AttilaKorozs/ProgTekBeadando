package org.rssreader.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rssreader.models.Article;

public class ArticleDAOTest {

    @BeforeAll
    static void AddTestUser() {
        UserDAO.addUser(TestData.TestUser);
    }

    @Test
    void testStoreArticle() {
        assertEquals(ArticleDAO.getArticle(1).size(), 0);
        ArticleDAO.storeArticle(TestData.TestArticle);
        assertEquals(ArticleDAO.getArticle(1).size(), 1);
        ArticleDAO.removeArticle(TestData.TestArticle);
        assertEquals(ArticleDAO.getArticle(1).size(), 0);

    }

    @AfterAll
    static void removeTestData() {
        UserDAO.deleteUser(TestData.TestUser);

        List<Article> testArticles = ArticleDAO.getArticle(1);
        for (Article article : testArticles) {
            ArticleDAO.removeArticle(article);
        }
    }
}
