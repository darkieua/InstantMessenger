package ua.sumdu.java.lab2.instant_messenger.processing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CreateUserImplTest {

    CreateUserImpl currentCreator = CreateUserImpl.getInstance();

    @Test
    public void validateUsername() throws Exception {
        String[] correctUsername = { "ansh", "anshf4", "an-md", "nd_sd"};
        String[] incorrectUsername = {"aa", "askldjaslfhakjfhuiohvfhgvfsdf", "asg%", "ks&?/"};
        for (String name : correctUsername) {
            Assert.assertTrue(currentCreator.validateUsername(name));
        }
        for (String name : incorrectUsername) {
            Assert.assertFalse(currentCreator.validateUsername(name));
        }
    }

    @Test
    public void validateEmail() throws Exception {
        Assert.assertFalse(true);
    }

    @Test
    public void validatePort() throws Exception {
        Assert.assertFalse(true);
    }

    @Test
    public void createUser() throws Exception {
        Assert.assertFalse(true);
    }

}