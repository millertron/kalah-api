package com.millertronics.kalahapi.game;


import com.millertronics.kalahapi.exceptions.GameNotFoundException;
import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameStatusCalculator gameStatusCalculator;

    @BeforeEach
    public void setup() {
        gameService = new GameService(gameRepository, gameStatusCalculator);
    }

    @Test
    @DisplayName("createGame should return a game entity")
    public void createGame_shouldReturn_game() {
        GameEntity expected = new GameEntity();
        // Might be worth removing new GameEntity() from create method
        // so that I can avoid using Mockito.any here.
        when(gameRepository.save(any(GameEntity.class))).thenReturn(expected);
        GameEntity result = gameService.createGame();

        assertThat(result, equalTo(expected));
        verify(gameRepository).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("makeMove should return a game entity")
    public void makeMove_shouldReturn_game() throws IllegalGameMoveException, GameNotFoundException {
        final int gameId = 7;
        final int pitId = 77;
        GameEntity expected = mock(GameEntity.class);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(expected));
        final int[] pits = new int[4];
        when(gameStatusCalculator.redistributeStones(expected, pitId)).thenReturn(pits);
        when(gameRepository.save(expected)).thenReturn(expected);

        final GameEntity result = gameService.makeMove(gameId, pitId);
        assertThat(result, equalTo(expected));

        verify(gameRepository).findById(gameId);
        verify(gameStatusCalculator).redistributeStones(expected, pitId);
        verify(expected).updateStatus(pits);
    }

    @Test
    @DisplayName("makeMove with invalid gameId should throw a GameNotFoundException")
    public void makeMove_withInvalid_gameId_shouldThrow_GameNotFoundException() {
        final int gameId = 7;
        final int pitId = 77;
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.makeMove(gameId, pitId));

        verify(gameRepository).findById(gameId);
        verifyNoInteractions(gameStatusCalculator);
        verify(gameRepository, never()).save(any(GameEntity.class));
    }

    @Test
    @DisplayName("makeMove with invalid pitId should throw an IllegalGameMoveException")
    public void makeMove_withInvalid_pitId_shouldThrow_IllegalGameMoveException() throws IllegalGameMoveException {
        final int gameId = 7;
        final int pitId = 77;
        GameEntity expected = new GameEntity();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(expected));
        when(gameStatusCalculator.redistributeStones(expected, pitId)).thenThrow(IllegalGameMoveException.class);

        assertThrows(IllegalGameMoveException.class, () -> gameService.makeMove(gameId, pitId));

        verify(gameRepository).findById(gameId);
        verify(gameStatusCalculator).redistributeStones(expected, pitId);
        verify(gameRepository, never()).save(any(GameEntity.class));
    }
}
