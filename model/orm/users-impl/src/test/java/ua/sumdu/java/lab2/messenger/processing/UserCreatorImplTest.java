package ua.sumdu.java.lab2.messenger.processing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Test;
import ua.sumdu.java.lab2.messenger.entities.User;

public class UserCreatorImplTest {

    @Test
    public void validateUsername() {
        String[] correctUsernames = { "ansh", "anshf4", "an-md", "nd_sd"};
        String[] incorrectUsernames = {"aa", "askldjaslfhakjfhuiohvfhgvfsdf", "asg%", "ks&?/"};
        for (String name : correctUsernames) {
            assertTrue(UserCreatorImpl.INSTANCE.validateUsername(name));
        }
        for (String name : incorrectUsernames) {
            assertFalse(UserCreatorImpl.INSTANCE.validateUsername(name));
        }
    }

    @Test
    public void validateEmail() {
        String[] correctEmails = { "g@i.nv", "hg.fd@as.bg", "an-md@bb.bb", "nd_sd@xx.xxd"};
        String[] incorrectEmails = {"aa", "bb@", "cc@dd.", "ks&?/@dg.rr.cv", "ss@mcnv%x.dd",
                "as@ss.co-vk", "as@xx.testest"};
        for (String email : correctEmails) {
            assertTrue(UserCreatorImpl.INSTANCE.validateEmail(email));
        }
        for (String email : incorrectEmails) {
            assertFalse(UserCreatorImpl.INSTANCE.validateEmail(email));
        }
    }

    @Test
    public void createUser() throws UnknownHostException {
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        User result = UserCreatorImpl.INSTANCE.createUser(FRIEND, "user1", "user1@ex.so",
                InetAddress.getLocalHost(), 8080);
        assertTrue(user1.equals(result));
        User emptyUser = UserCreatorImpl.INSTANCE.createUser(FRIEND, "user2", "user3",
                InetAddress.getLocalHost(), 8080);
        assertTrue(User.getEmptyUser().equals(emptyUser));
    }
}