package org.rssreader.service.decorator;

import org.rssreader.dao.UserArticleDAO;
import org.rssreader.models.Article;
import org.rssreader.models.UserArticle;
import org.rssreader.util.Session;

import java.time.LocalDateTime;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CachedReadDecorator extends ArticleDecorator {
    private final Map<Integer,UserArticle> statusMap;
    private static final Logger logger = LogManager.getLogger(CachedReadDecorator.class);

    public CachedReadDecorator(ArticleComponent wrappee,
                               Map<Integer,UserArticle> statusMap) {
        super(wrappee);
        this.statusMap = statusMap;
    }

    @Override
    public boolean isRead() {
        Article a = wrappee.getModel();
        UserArticle ua = statusMap.get(a.getId());
        return ua != null && ua.isRead();
    }

    @Override
    public void setRead(boolean read) {
        Article a = wrappee.getModel();
        UserArticleDAO.setRead(Session.getCurrentUser(), a, read);
        statusMap.compute(a.getId(), (_, ua) -> {
            if (ua == null) {
                ua = new UserArticle(
                        Session.getCurrentUser(), a, false, read, LocalDateTime.now());
            } else {
                if (read) ua.setRead();
                else      ua.unsetRead();
            }
            return ua;
        });
        logger.info("User '{}' set read={} for article '{}'",
                Session.getCurrentUser().getUsername(),
                read,
                a.getTitle());
    }
}
