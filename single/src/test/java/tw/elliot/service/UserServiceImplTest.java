package tw.elliot.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.elliot.model.User;

@SpringBootTest
public class UserServiceImplTest {

  @Autowired
  private UserService userService;

  @Test
  public void test123() {
    User user = this.userService.findUserById("1");
    Assertions.assertNotNull(user);
  }
}