package org.rssreader.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FeedDAOTest {

    @Test
    void testAddFeed() {
        int feedCount=FeedDAO.getFeedList().size();
        FeedDAO.addFeed(TestData.TestFeed);
        assertEquals(FeedDAO.getFeedList().size(), feedCount + 1);
    }

    @Test
    void testRemoveFeed() {
        int feedCount=FeedDAO.getFeedList().size();
        FeedDAO.removeFeed(TestData.TestFeed);
        assertEquals(FeedDAO.getFeedList().size(), feedCount - 1);
    }
}
