package org.rssreader.service.decorator;

import org.rssreader.dao.UserArticleDAO;
import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.service.FeedService;
import org.rssreader.util.Session;

public class ReadDecorator extends ArticleDecorator {
    private final FeedService feedService = FeedService.getInstance();

    public ReadDecorator(ArticleComponent wrappee) {
        super(wrappee);
    }

    @Override
    public boolean isRead() {
        Article a = wrappee.getModel();
        Feed feed = feedService.getFeedByUri(a.getFeedUri());
        return UserArticleDAO.getUserArticle(Session.getCurrentUser(), feed)
                .stream().anyMatch(ua -> ua.getArticle().getId()==a.getId() && ua.isRead());
    }

    @Override
    public void setRead(boolean read) {
        Article a = wrappee.getModel();
        if (read) {
            UserArticleDAO.setRead(Session.getCurrentUser(), a);
        } else {
            // implementáld az olvasott jelölés törlését is, ha szükséges
        }
    }
}