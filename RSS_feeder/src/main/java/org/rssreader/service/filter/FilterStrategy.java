package org.rssreader.service.filter;
import java.util.List;

public interface FilterStrategy<T>{
    /**
     * Visszaadja a bemeneti lista azon elemeit, amelyek megfelelnek a stratégiának.
     */
   // List<Article> filter(List<Article> articles);


        List<T> filter(List<T> items);

}
