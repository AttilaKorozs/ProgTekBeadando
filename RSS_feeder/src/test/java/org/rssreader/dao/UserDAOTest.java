package org.rssreader.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.rssreader.models.User;

public class UserDAOTest {

    
    @Test
    void TestUserDAO(){
        assertNull(UserDAO.authUser(TestData.TestUser));
        UserDAO.addUser(TestData.TestUser);
        assertNotNull(UserDAO.authUser(TestData.TestUser));
        TestData.TestUser=new User("teszt", "masikpass", "masik@teszt.hu");

        UserDAO.modifyUser(TestData.TestUser);
        User userToCheck = UserDAO.authUser(TestData.TestUser);
        assertEquals(userToCheck, TestData.TestUser);

        UserDAO.removeUser(TestData.TestUser);
        assertNull(UserDAO.authUser(TestData.TestUser));
    }
}

