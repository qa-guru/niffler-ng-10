package guru.qa.niffler.test.web;


import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;

@ExtendWith(UsersQueueExtension.class)
public class QueueTest {


    @Test
    public void test1(@UserType(empty = UserType.Type.EMPTY) StaticUser user0,
                      @UserType(empty = UserType.Type.WHITH_FRIEND) StaticUser user1,
                      @UserType(empty = UserType.Type.WITH_OUTCOME_REQUEST) StaticUser user2,
                      @UserType(empty = UserType.Type.WHITH_INCOME_REQUEST) StaticUser user3) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(user0);
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
    }
}
