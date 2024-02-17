package ServiceUnitTest;

import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.model.*;
import com.tpa.gameservice.repository.GameRepository;
import com.tpa.gameservice.service.GameService;
import com.tpa.gameservice.service.WebClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private WebClientService webClientService;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGameTest() {
        // given
        String username1 = "one";
        String username2 = "two";
        NewGameRequest request = new NewGameRequest();
        request.setFirstPlayerUsername(username1);
        request.setSecondPlayerUsername(username2);
        Game expectedGame = createExpectedGame(username1,username2);

        // when
        Game game = this.gameService.createGame(request);

        //then
        ArgumentCaptor<Game> userArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository).save(userArgumentCaptor.capture());
        assertEquals(expectedGame, game);
        // your assertions go here
    }
    @Test
    void getGameTest() {
        // given
        String id = "1";
        String username1 = "one";
        String username2 = "two";
        Game mockGame = createExpectedGame(username1,username2);
        mockGame.setId(id);

        GameResponse expectedDTO = GameResponse.builder()
                .history(mockGame.getHistory())
                .players(mockGame.getPlayers())
                .actualColor(mockGame.getActualColor())
                .build();
        // when
        when(gameRepository.findById(id)).thenReturn(Optional.of(mockGame));

        GameResponse result = this.gameService.getGame(id);
//        // then
        assertEquals(expectedDTO, result);
    }

    private Game createExpectedGame(String username1,String username2){
        List<String> defaultCastleTypes = List.of(CastleType.LONGWHITE.getValue(),
                CastleType.SHORTWHITE.getValue(),
                CastleType.LONGBLACK.getValue(),
                CastleType.SHORTBLACK.getValue());

        String defaultBoardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

        GameState defaultGameState = GameState.builder()
                .boardState(defaultBoardState)
                .status(GameStatus.GAME)
                .castleTypes(defaultCastleTypes)
                .build();

        Player firstPlayer = Player.builder().username(username1).color(PlayerColor.WHITE).build();
        Player secondPlayer = Player.builder().username(username2).color(PlayerColor.BLACK).build();

        return Game.builder()
                .players(new Player[]{firstPlayer, secondPlayer})
                .history(List.of(defaultGameState))
                .actualColor(PlayerColor.WHITE)
                .build();
    }

}
