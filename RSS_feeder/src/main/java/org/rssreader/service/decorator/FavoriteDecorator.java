package org.rssreader.service.decorator;

import org.rssreader.dao.UserArticleDAO;
import org.rssreader.models.Article;
import org.rssreader.models.Feed;
import org.rssreader.service.FeedService;
import org.rssreader.util.Session;

/**
 * Megjeleníti és kezeli a kedvenc-státuszt.
 */
public class FavoriteDecorator extends ArticleDecorator {
    private final FeedService feedService = FeedService.getInstance();

    public FavoriteDecorator(ArticleComponent wrappee) {
        super(wrappee);
    }

    @Override
    public boolean isFavorite() {
        Article a = wrappee.getModel();
        Feed feed = feedService.getFeedByUri(a.getFeedUri());
        return UserArticleDAO.getUserArticle(Session.getCurrentUser(), feed)
                .stream().anyMatch(ua -> ua.getArticle().getId()==a.getId() && ua.isFavorite());
    }

    @Override
    public void setFavorite(boolean fav) {
        Article a = wrappee.getModel();
        Feed feed = feedService.getFeedByUri(a.getFeedUri());
        if (fav) {
            UserArticleDAO.setFavorite(Session.getCurrentUser(), a);
        } else {
            // implementáld a törlést is, ha kell
        }
    }
}
