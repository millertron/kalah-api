package com.millertronics.kalahapi.game;

import com.millertronics.kalahapi.exceptions.GameNotFoundException;
import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIT {

    private final static int GAME_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Mock
    private GameEntity game;

    @BeforeEach
    public void setup() {
        when(game.getId()).thenReturn(GAME_ID);
    }

    @Test
    @DisplayName("POST /games should respond with 204 - payload contains game ID")
    public void createGame_shouldRespondWith_created() throws Exception {
        when(gameService.createGame()).thenReturn(game);
        mockMvc.perform(post("/games"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(String.valueOf(GAME_ID)));
    }

    @Test
    @DisplayName("PUT /games/:gameId/pits/:pitId should respond with 200 - payload contains game ID and status")
    public void play_shouldRespondWith_success() throws Exception {
        Map<String, String> formattedStatus = new HashMap<>();
        formattedStatus.put("1", "0");
        formattedStatus.put("2", "3");
        formattedStatus.put("3", "3");
        formattedStatus.put("4", "1");

        formattedStatus.put("5", "2");
        formattedStatus.put("6", "2");
        formattedStatus.put("7", "2");
        formattedStatus.put("8", "0");

        final int pitId = 9;
        when(gameService.makeMove(GAME_ID, pitId - 1)).thenReturn(game);
        when(game.getFormattedStatus()).thenReturn(formattedStatus);

        mockMvc.perform(put("/games/" + GAME_ID + "/pits/" + pitId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(String.valueOf(GAME_ID)))
                .andExpect(jsonPath("$.status.1").value("0"))
                .andExpect(jsonPath("$.status.2").value("3"))
                .andExpect(jsonPath("$.status.3").value("3"))
                .andExpect(jsonPath("$.status.4").value("1"))
                .andExpect(jsonPath("$.status.5").value("2"))
                .andExpect(jsonPath("$.status.6").value("2"))
                .andExpect(jsonPath("$.status.7").value("2"))
                .andExpect(jsonPath("$.status.8").value("0"));
    }

    @Test
    @DisplayName("PUT /games/:gameId/pits/:pitId should respond with 404 when game is not found")
    public void play_shouldRespondWith_notFound() throws Exception {
        when(gameService.makeMove(GAME_ID, 0)).thenThrow(GameNotFoundException.class);
        mockMvc.perform(put("/games/" + GAME_ID + "/pits/" + 1))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Invalid game ID."));
    }

    @Test
    @DisplayName("PUT /games/:gameId/pits/:pitId should respond with 400 when move is illegal")
    public void play_shouldRespondWith_badRequest() throws Exception {
        when(gameService.makeMove(GAME_ID, 0)).thenThrow(IllegalGameMoveException.class);
        mockMvc.perform(put("/games/" + GAME_ID + "/pits/" + 1))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Illegal game move."));
    }

}
