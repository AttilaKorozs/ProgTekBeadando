package org.rssreader.service.decorator;

import org.junit.jupiter.api.Test;
import org.rssreader.models.Article;
import org.rssreader.models.User;
import org.rssreader.models.UserArticle;
import org.rssreader.util.Session;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArticleComponentTest {

    private Article sampleArticle() throws Exception {
        return new Article(
                42,
                new URI("http://feed.example.com"),
                "Sample",
                new URI("http://link"),
                LocalDateTime.now(),
                "content"
        );
    }

    @Test
    void basicComponentDefaults() throws Exception {
        Article a = sampleArticle();
        BasicArticleComponent bac = new BasicArticleComponent(a);
        assertEquals("Sample", bac.getTitle());
        assertEquals(a.getLink(), bac.getLink());
        assertFalse(bac.isFavorite());
        assertFalse(bac.isRead());
    }

    @Test
    void cachedFavoriteDecoratorRespectsCache() throws Exception {
        Article a = sampleArticle();
        User u = new User("u", "p", "e");
        Session.setCurrentUser(u);
        // előkészítjük a cache-et
        UserArticle ua = new UserArticle(u, a, true, false, LocalDateTime.now());
        Map<Integer, UserArticle> cache = Map.of(a.getId(), ua);

        ArticleComponent comp =
                new CachedFavoriteDecorator(new BasicArticleComponent(a), cache);

        assertTrue(comp.isFavorite());
        assertFalse(comp.isRead());
    }

    @Test
    void cachedReadDecoratorRespectsCache() throws Exception {
        Article a = sampleArticle();
        User u = new User("u", "p", "e");
        Session.setCurrentUser(u);
        UserArticle ua = new UserArticle(u, a, false, true, LocalDateTime.now());
        Map<Integer, UserArticle> cache = Map.of(a.getId(), ua);

        ArticleComponent comp =
                new CachedReadDecorator(new BasicArticleComponent(a), cache);

        assertTrue(comp.isRead());
        assertFalse(comp.isFavorite());
    }
}
