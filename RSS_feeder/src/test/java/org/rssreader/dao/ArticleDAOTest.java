package org.rssreader.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rssreader.models.Article;

public class ArticleDAOTest {

    private static final Article TestArticle = new Article(1, "Hatalmas Teszt Link", "http://teszt.link", LocalDateTime.now(),"Szuper Content");

    @BeforeAll
    static void removeTestArticles()
    {
        List<Article> testArticles= ArticleDAO.getArticle(1);
        for (Article article : testArticles) {
            ArticleDAO.removeArticle(article);
        }
    }

    @Test
    void testStoreArticle() {
        assertEquals(ArticleDAO.getArticle(1).size(),0);
        ArticleDAO.storeArticle(TestArticle);
        assertEquals(ArticleDAO.getArticle(1).size(),1);
        ArticleDAO.removeArticle(TestArticle);
        assertEquals(ArticleDAO.getArticle(1).size(),0);

    }
}
