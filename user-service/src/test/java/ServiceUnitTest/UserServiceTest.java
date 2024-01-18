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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String email = "test@mail.com";
    private final String username = "test";
    @Test
    void shouldCreateUserWithProperValues() {
        // given
        SignUpRequest request = initializeSignupRequest();

        UserResponse expectedDTO = UserResponse.builder()
                .username(username)
                .email(email)
                .build();

        UserResponse user = this.userService.createUser(request);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());
        assertEquals(expectedDTO, user);
    }

    @Test
    void shouldNotCreateUserAndThrowUserRequestException() {
        // given
        SignUpRequest request = initializeSignupRequest();

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
        User mockUser = User.builder()
                .email(email)
                .username(username)
                .build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserResponse expectedDTO = UserResponse.builder()
                .username(username)
                .build();

        UserResponse result = this.userService.getUserByEmail(email);

        // then
        assertEquals(expectedDTO, result);
    }

    @Test
    void shouldNotGetUserAndThrowUserRequestException() {
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
        verify(userRepository).saveAll(any(List.class));

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
    void shouldAddEloPointsToWinningPlayer() {
        // given
        User winningPlayer = initializeWinningPlayer();

        User losingPlayer = initializeLosingPlayer();

        // when
        when(userRepository.findByUsername(winningPlayer.getUsername())).thenReturn(Optional.of(winningPlayer));
        when(userRepository.findByUsername(losingPlayer.getUsername())).thenReturn(Optional.of(losingPlayer));

        this.userService.tradeEloPoints(winningPlayer.getUsername(), losingPlayer.getUsername(), true);

        Double result = winningPlayer.getEloRating();

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(winningPlayer)
                .isNotNull()
                .hasFieldOrPropertyWithValue("eloRating", result);

    }

    @Test
    void shouldRemoveEloPointsFromLosingPlayer() {
        User winningPlayer = initializeWinningPlayer();

        User losingPlayer = initializeLosingPlayer();

        // when
        when(userRepository.findByUsername(winningPlayer.getUsername())).thenReturn(Optional.of(winningPlayer));
        when(userRepository.findByUsername(losingPlayer.getUsername())).thenReturn(Optional.of(losingPlayer));

        this.userService.tradeEloPoints(winningPlayer.getUsername(), losingPlayer.getUsername(), false);

        Double result = losingPlayer.getEloRating();

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(losingPlayer)
                .isNotNull()
                .hasFieldOrPropertyWithValue("eloRating", result);
    }
    private SignUpRequest initializeSignupRequest(){
        SignUpRequest request = new SignUpRequest();
        request.setEmail(email);
        request.setUsername(username);
        return request;
    }

    private User initializeWinningPlayer() {
        User winningPlayer = new User();
        String winningPlayerUsername = "Bolek";
        winningPlayer.setEloRating(1200.0);
        winningPlayer.setUsername(winningPlayerUsername);
        return winningPlayer;
    }

    private User initializeLosingPlayer() {
        User losingPlayer = new User();
        String losingPlayerUsername = "Lolek";
        losingPlayer.setEloRating(1000.0);
        losingPlayer.setUsername(losingPlayerUsername);
        return losingPlayer;
    }
}
