package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

import org.junit.Before;
import org.junit.Test;

import static ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers.FRIEND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserCreatorImplTest {

    private static UserCreatorImpl currentCreator;

    @Before
    public void set() {
        currentCreator = UserCreatorImpl.getInstance();
    }

    @Test
    public void validateUsername() throws Exception {
        String[] correctUsernames = { "ansh", "anshf4", "an-md", "nd_sd"};
        String[] incorrectUsernames = {"aa", "askldjaslfhakjfhuiohvfhgvfsdf", "asg%", "ks&?/"};
        for (String name : correctUsernames) {
            assertTrue(currentCreator.validateUsername(name));
        }
        for (String name : incorrectUsernames) {
            assertFalse(currentCreator.validateUsername(name));
        }
    }

    @Test
    public void validateEmail() throws Exception {
        String[] correctEmails = { "g@i.nv", "hg.fd@as.bg", "an-md@bb.bb", "nd_sd@xx.xxd"};
        String[] incorrectEmails = {"aa", "bb@", "cc@dd.", "ks&?/@dg.rr.cv", "ss@mcnv%x.dd", "as@ss.co-vk", "as@xx.testest"};
        for (String email : correctEmails) {
            assertTrue(currentCreator.validateEmail(email));
        }
        for (String email : incorrectEmails) {
            assertFalse(currentCreator.validateEmail(email));
        }
    }

    @Test
    public void createUser() throws Exception {
        User user1= new User(FRIEND, "user1", "user1@ex.so", 8080, "193.168.1.1");
        User result = currentCreator.createUser(FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        assertEquals(user1, result);
        assertNull(currentCreator.createUser(FRIEND, "user1", "user1", "193.168.1.1", 8080));
    }
}