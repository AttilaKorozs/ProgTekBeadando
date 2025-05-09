package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rssreader.dao.UserDAO;
import org.rssreader.models.User;

public class UserDAOTest {

    private static User TestUser;
    
    @BeforeAll
    static void Setup() {
        TestUser = new User("teszt", "tesztpass", "teszt@teszt.hu");
    }

    @Test
    void TestUserDAO(){
        assertNull(UserDAO.authUser(TestUser));
        UserDAO.addUser(TestUser);
        assertNotNull(UserDAO.authUser(TestUser));
        TestUser=new User("teszt", "masikpass", "masik@teszt.hu");

        UserDAO.modifyUser(TestUser);
        User userToCheck = UserDAO.authUser(TestUser);
        assertEquals(userToCheck, TestUser);

        UserDAO.deleteUser(TestUser);
        assertNull(UserDAO.authUser(TestUser));
    }
}