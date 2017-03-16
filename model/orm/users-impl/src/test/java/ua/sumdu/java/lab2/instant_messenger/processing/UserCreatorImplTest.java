package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.entities.User;

import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.FRIEND;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserCreatorImplTest {

    private static UserCreatorImpl currentCreator;

    @Before
    public void set() {
        currentCreator = UserCreatorImpl.getInstance();
    }

    @Test
    public void validateUsername() {
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
    public void validateEmail() {
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
    public void createUser() throws UnknownHostException {
        User user1= new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        User result = currentCreator.createUser(FRIEND, "user1", "user1@ex.so", InetAddress.getLocalHost(), 8080);
        assertTrue(user1.equals(result));
        User emptyUser = currentCreator.createUser(FRIEND, "user2", "user3", InetAddress.getLocalHost(), 8080);
        assertTrue(new User().equals(emptyUser));
    }
}