package org.rssreader.util;

import org.rssreader.models.User;

/**
 * Egyszerű singleton session-tároló a bejelentkezett felhasználó számára.
 */
public class Session {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
