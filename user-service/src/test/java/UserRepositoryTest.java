import com.tpa.userservice.UserServiceApplication;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = UserServiceApplication.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        String email = "jk@op.pl";
        String username = "testUsername";
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);

        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            String expectedUsername = userOptional.get().getUsername();
            assertEquals(username, expectedUsername);
        } else {
            fail("This email doesn't exist");
        }
    }
}
