package org.rssreader.util;

import org.rssreader.models.Article;

import java.util.List;

public class RssParserTest {
    public static void main(String[] args) {
        RssParser parser = new RssParser();
        try {
            // Tedd be egy valós, élő RSS URL-t
            List<Article> articles = parser.parse("https://news.un.org/feed/subscribe/en/news/all/rss.xml", 1);
            articles.forEach(a -> {
                System.out.println("Cím: " + a.getTitle());
                System.out.println("Dátum: " + a.getPublicationDate());
                System.out.println("-----");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
