package ServiceUnitTest;

import com.tpa.userservice.dto.NewGameRequest;
import com.tpa.userservice.dto.SignUpRequest;
import com.tpa.userservice.dto.UserResponse;
import com.tpa.userservice.exception.UserRequestException;
import com.tpa.userservice.model.User;
import com.tpa.userservice.repostiory.UserRepository;
import com.tpa.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserWithProperValues() {
        // given
        String email = "test@mail.com";
        String username = "test";

        SignUpRequest request = new SignUpRequest();
        request.setEmail(email);
        request.setUsername(username);

        UserResponse user = this.userService.createUser(request);

        // then
        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("username", username);
    }

    @Test
    void shouldNotCreateUserAndThrowUserRequestException() {
        // given
        String email = "test@mail.com";
        String username = "test";

        SignUpRequest request = new SignUpRequest();
        request.setEmail(email);
        request.setUsername(username);

        User mockUser = User.builder()
                .email(email)
                .build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));


        // then
        assertThatThrownBy(() -> this.userService.createUser(request))
                .isInstanceOf(UserRequestException.class)
                .hasMessage("User already exists");
    }

    @Test
    void shouldGetUserByEmailAndReturnUserResponse() {
        // given
        String email = "test@mail.com";
        String username = "test";

        User mockUser = User.builder()
                .email(email)
                .username(username)
                .build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserResponse result = this.userService.getUserByEmail(email);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("username", username);
    }

    @Test
    void shouldNotGetUserAndThrowUserRequestException() {
        // given
        String email = "test@mail.com";

        // then
        assertThatThrownBy(() -> this.userService.getUserByEmail(email))
                .isInstanceOf(UserRequestException.class)
                .hasMessage("User does not exists");
    }

    @Test
    void shouldAddGame() {
        // given
        User firstPlayer = new User();
        String firstPlayerUsername = "Bolek";
        List<String> firstPlayerGameHistory = new ArrayList<>();
        firstPlayer.setGameHistory(firstPlayerGameHistory);
        firstPlayer.setUsername(firstPlayerUsername);

        User secondPlayer = new User();
        String secondPlayerUsername = "Lolek";
        List<String> secondPlayerGameHistory = new ArrayList<>();
        secondPlayer.setGameHistory(secondPlayerGameHistory);
        secondPlayer.setUsername(secondPlayerUsername);

        NewGameRequest request = new NewGameRequest();
        String gameId = "game1";
        request.setGameId(gameId);
        request.setFirstPlayerUsername(firstPlayerUsername);
        request.setSecondPlayerUsername(secondPlayerUsername);

        // when
        when(userRepository.findByUsername(firstPlayerUsername)).thenReturn(Optional.of(firstPlayer));
        when(userRepository.findByUsername(secondPlayerUsername)).thenReturn(Optional.of(secondPlayer));

        this.userService.addGame(request);

        // then
        assertThat(firstPlayer)
                .isNotNull()
                .hasFieldOrPropertyWithValue("gameHistory", List.of(gameId));

        assertThat(secondPlayer)
                .isNotNull()
                .hasFieldOrPropertyWithValue("gameHistory", List.of(gameId));
    }

    @Test
    void shouldNotAddGameAndThrowUserRequestException() {
        // given
        NewGameRequest request = new NewGameRequest();
        String gameId = "game1";
        request.setGameId(gameId);
        request.setFirstPlayerUsername("Bolek");
        request.setSecondPlayerUsername("Lolek");

        // then
        assertThatThrownBy(() -> this.userService.addGame(request))
                .isInstanceOf(UserRequestException.class)
                .hasMessage("User does not exists");

    }

    @Test
    void shouldTradeEloPointsBetweenPlayers() {
        // given
        User winningPlayer = new User();
        String winningPlayerUsername = "Bolek";
        winningPlayer.setEloRating(1200.0);
        winningPlayer.setUsername(winningPlayerUsername);

        User losingPlayer = new User();
        String losingPlayerUsername = "Lolek";
        losingPlayer.setEloRating(1000.0);
        losingPlayer.setUsername(losingPlayerUsername);

        // when
        when(userRepository.findByUsername(winningPlayerUsername)).thenReturn(Optional.of(winningPlayer));
        when(userRepository.findByUsername(losingPlayerUsername)).thenReturn(Optional.of(losingPlayer));

        this.userService.tradeEloPoints(winningPlayerUsername, losingPlayerUsername, true);
        this.userService.tradeEloPoints(winningPlayerUsername, losingPlayerUsername, false);

        Double winningElo = winningPlayer.getEloRating();
        Double losingElo = losingPlayer.getEloRating();

        // then
        assertThat(winningPlayer)
                .isNotNull()
                .hasFieldOrPropertyWithValue("eloRating", winningElo);

        assertThat(losingPlayer)
                .isNotNull()
                .hasFieldOrPropertyWithValue("eloRating", losingElo);
    }
}
