package org.rssreader.service.decorator;

import org.rssreader.models.Article;
import org.rssreader.models.UserArticle;
import org.rssreader.util.Session;

import java.time.LocalDateTime;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CachedFavoriteDecorator extends ArticleDecorator {
    private final Map<Integer,UserArticle> statusMap;

    private static final Logger logger = LogManager.getLogger(CachedFavoriteDecorator.class);

    public CachedFavoriteDecorator(ArticleComponent wrappee,
                                   Map<Integer,UserArticle> statusMap) {
        super(wrappee);
        this.statusMap = statusMap;
    }

    @Override
    public boolean isFavorite() {
        Article a = wrappee.getModel();
        UserArticle ua = statusMap.get(a.getId());
        return ua != null && ua.isFavorite();
    }

    @Override
    public void setFavorite(boolean fav) {
        super.setFavorite(fav);  // ez hívja a DAO-t, ír a DB-be
        // és frissítjük a cache-t is:
        Article a = wrappee.getModel();
        statusMap.compute(a.getId(), (id, ua) -> {
            if (ua == null) {
                ua = new UserArticle(
                        Session.getCurrentUser(), a, fav, false, LocalDateTime.now());
            } else {
                if (fav)
                {
                    ua.setFavorite();

                }
                else     ua.unsetFavorite(); // ha implementáltad
            }
            logger.info("User '{}' set favorite={} for article '{}'",
                    Session.getCurrentUser().getUsername(),
                    fav,
                    a.getTitle());
            return ua;
        });


    }
}
