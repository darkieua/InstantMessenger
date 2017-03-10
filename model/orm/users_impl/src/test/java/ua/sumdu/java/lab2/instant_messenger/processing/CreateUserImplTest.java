package ua.sumdu.java.lab2.instant_messenger.processing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public class CreateUserImplTest {

    CreateUserImpl currentCreator = CreateUserImpl.getInstance();

    @Test
    public void validateUsername() throws Exception {
        String[] correctUsernames = { "ansh", "anshf4", "an-md", "nd_sd"};
        String[] incorrectUsernames = {"aa", "askldjaslfhakjfhuiohvfhgvfsdf", "asg%", "ks&?/"};
        for (String name : correctUsernames) {
            Assert.assertTrue(currentCreator.validateUsername(name));
        }
        for (String name : incorrectUsernames) {
            Assert.assertFalse(currentCreator.validateUsername(name));
        }
    }

    @Test
    public void validateEmail() throws Exception {
        String[] correctEmails = { "g@i.nv", "hg.fd@as.bg", "an-md@bb.bb", "nd_sd@xx.xxd"};
        String[] incorrectEmails = {"aa", "bb@", "cc@dd.", "ks&?/@dg.rr.cv", "ss@mcnv%x.dd", "as@ss.co-vk", "as@xx.testest"};
        for (String email : correctEmails) {
            Assert.assertTrue(currentCreator.validateEmail(email));
        }
        for (String email : incorrectEmails) {
            Assert.assertFalse(currentCreator.validateEmail(email));
        }
    }

    @Test
    public void createUser() throws Exception {
        User user1 = new User();
        user1.update(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User result = currentCreator.createUser(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        Assert.assertEquals(user1, result);
        Assert.assertNull(currentCreator.createUser(CategoryUsers.FRIEND, "user1", "user1", "193.168.1.1", 8080));
    }
}