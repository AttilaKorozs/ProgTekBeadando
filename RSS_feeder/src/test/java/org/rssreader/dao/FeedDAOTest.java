package org.rssreader.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FeedDAOTest {

    @Test
    void testAddFeed() {
        assertEquals(FeedDAO.getFeedList().size(), 0);
        FeedDAO.addFeed(TestData.TestFeed);
        assertEquals(FeedDAO.getFeedList().size(), 1);
    }

    @Test
    void testRemoveFeed() {
        assertEquals(FeedDAO.getFeedList().size(), 1);
        FeedDAO.removeFeed(TestData.TestFeed);
        assertEquals(FeedDAO.getFeedList().size(), 0);
    }
}
