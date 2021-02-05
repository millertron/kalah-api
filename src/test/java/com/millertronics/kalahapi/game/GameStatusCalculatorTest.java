package com.millertronics.kalahapi.game;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class GameStatusCalculatorTest {

    private final GameStatusCalculator gameStatusCalculator = new GameStatusCalculator();

    private GameEntity gameEntity;

    @BeforeEach()
    public void setup() {
        gameEntity = mock(GameEntity.class);
    }


    @Test
    @DisplayName("move should return updated integer array pits")
    public void move_shouldReturn_newPits() throws IllegalGameMoveException {
        when(gameEntity.getPits()).thenReturn(List.of(2, 2, 2, 0, 2, 2, 2, 0));
        int[] newPits = gameStatusCalculator.redistributeStones(gameEntity, 0);
        assertThat(newPits[0], equalTo(0));
        assertThat(newPits[1], equalTo(3));
        assertThat(newPits[2], equalTo(3));
        assertThat(newPits[3], equalTo(0));
        assertThat(newPits[4], equalTo(2));
        assertThat(newPits[5], equalTo(2));
        assertThat(newPits[6], equalTo(2));
        assertThat(newPits[7], equalTo(0));
    }

    @Test
    @DisplayName("move should add to player kalah pit")
    public void move_crossingOwnKalah_should_addToKalah() throws IllegalGameMoveException {
        when(gameEntity.getPits()).thenReturn(List.of(2, 2, 2, 0, 2, 2, 2, 0));
        int[] newPits = gameStatusCalculator.redistributeStones(gameEntity, 2);
        assertThat(newPits[0], equalTo(2));
        assertThat(newPits[1], equalTo(2));
        assertThat(newPits[2], equalTo(0));
        assertThat(newPits[3], equalTo(1));
        assertThat(newPits[4], equalTo(3));
        assertThat(newPits[5], equalTo(2));
        assertThat(newPits[6], equalTo(2));
        assertThat(newPits[7], equalTo(0));

        when(gameEntity.getPits()).thenReturn(List.of(2, 2, 2, 0, 2, 2, 2, 0));
        newPits = gameStatusCalculator.redistributeStones(gameEntity, 6);
        assertThat(newPits[0], equalTo(3));
        assertThat(newPits[1], equalTo(2));
        assertThat(newPits[2], equalTo(2));
        assertThat(newPits[3], equalTo(0));
        assertThat(newPits[4], equalTo(2));
        assertThat(newPits[5], equalTo(2));
        assertThat(newPits[6], equalTo(0));
        assertThat(newPits[7], equalTo(1));
    }

    @Test
    @DisplayName("move should not add to opposite player's kalah")
    public void move_crossingOppositeKalah_should_skip() throws IllegalGameMoveException {
        when(gameEntity.getPits()).thenReturn(List.of(5, 5, 5, 0, 5, 5, 5, 0));
        int[] newPits = gameStatusCalculator.redistributeStones(gameEntity, 2);
        assertThat(newPits[0], equalTo(6));
        assertThat(newPits[1], equalTo(5));
        assertThat(newPits[2], equalTo(0));
        assertThat(newPits[3], equalTo(1));
        assertThat(newPits[4], equalTo(6));
        assertThat(newPits[5], equalTo(6));
        assertThat(newPits[6], equalTo(6));
        assertThat(newPits[7], equalTo(0));

        when(gameEntity.getPits()).thenReturn(List.of(5, 5, 5, 0, 5, 5, 5, 0));
        newPits = gameStatusCalculator.redistributeStones(gameEntity, 6);
        assertThat(newPits[0], equalTo(6));
        assertThat(newPits[1], equalTo(6));
        assertThat(newPits[2], equalTo(6));
        assertThat(newPits[3], equalTo(0));
        assertThat(newPits[4], equalTo(6));
        assertThat(newPits[5], equalTo(5));
        assertThat(newPits[6], equalTo(0));
        assertThat(newPits[7], equalTo(1));
    }

    @Test
    @DisplayName("move with pitIndex provided outside the pit array should throw IllegalGameMoveException")
    public void move_with_pitIndexOutsideArray_shouldThrow_IllegalGameMoveException() {
        when(gameEntity.getPits()).thenReturn(List.of(5, 5, 5, 0, 5, 5, 5, 0));
        assertThrows(IllegalGameMoveException.class, () -> gameStatusCalculator.redistributeStones(gameEntity, -1));
        assertThrows(IllegalGameMoveException.class, () -> gameStatusCalculator.redistributeStones(gameEntity, 8));
    }

    @Test
    @DisplayName("move with pitIndex of kalah pit should throw IllegalGameMoveException")
    public void move_with_kalahPitIndex_shouldThrow_IllegalGameMoveException() {
        when(gameEntity.getPits()).thenReturn(List.of(5, 5, 5, 0, 5, 5, 5, 0));
        assertThrows(IllegalGameMoveException.class, () -> gameStatusCalculator.redistributeStones(gameEntity, 3));
        assertThrows(IllegalGameMoveException.class, () -> gameStatusCalculator.redistributeStones(gameEntity, 7));
    }

    @Test
    @DisplayName("move with pitIndex of an empty pit should throw IllegalGameMoveException")
    public void move_with_pitIndexOfEmptyPit_shouldThrow_IllegalGameMoveException() {
        when(gameEntity.getPits()).thenReturn(List.of(0, 5, 5, 0, 5, 5, 5, 0));
        assertThrows(IllegalGameMoveException.class, () -> gameStatusCalculator.redistributeStones(gameEntity, 0));
    }

    @Test
    @DisplayName("move with last stone added to player's empty pit should add all the stones from opposite pit")
    public void move_stoneToEmptyPlayerSidePit_should_addFromOpposite() throws IllegalGameMoveException {
        when(gameEntity.getPits()).thenReturn(List.of(2, 2, 0, 0, 2, 2, 2, 2));
        int[] newPits = gameStatusCalculator.redistributeStones(gameEntity, 0);
        assertThat(newPits[0], equalTo(0));
        assertThat(newPits[1], equalTo(3));
        assertThat(newPits[2], equalTo(3));
        assertThat(newPits[3], equalTo(0));
        assertThat(newPits[4], equalTo(0));
        assertThat(newPits[5], equalTo(2));
        assertThat(newPits[6], equalTo(2));
        assertThat(newPits[7], equalTo(2));

        when(gameEntity.getPits()).thenReturn(List.of(2, 2, 2, 0, 2, 2, 0, 0));
        newPits = gameStatusCalculator.redistributeStones(gameEntity, 4);
        assertThat(newPits[0], equalTo(0));
        assertThat(newPits[1], equalTo(2));
        assertThat(newPits[2], equalTo(2));
        assertThat(newPits[3], equalTo(0));
        assertThat(newPits[4], equalTo(0));
        assertThat(newPits[5], equalTo(3));
        assertThat(newPits[6], equalTo(3));
        assertThat(newPits[7], equalTo(0));
    }

    @Test
    @DisplayName("move with last stone added to opposition's empty pit should not add all the stones from opposite pit")
    public void move_stoneToEmptyOppositeSidePit_shouldNot_removeFromPlayer() throws IllegalGameMoveException {
        when(gameEntity.getPits()).thenReturn(List.of(2, 2, 2, 0, 0, 2, 2, 2));
        int[] newPits = gameStatusCalculator.redistributeStones(gameEntity, 2);
        assertThat(newPits[0], equalTo(2));
        assertThat(newPits[1], equalTo(2));
        assertThat(newPits[2], equalTo(0));
        assertThat(newPits[3], equalTo(1));
        assertThat(newPits[4], equalTo(1));
        assertThat(newPits[5], equalTo(2));
        assertThat(newPits[6], equalTo(2));
        assertThat(newPits[7], equalTo(2));

        when(gameEntity.getPits()).thenReturn(List.of(0, 2, 2, 0, 2, 2, 2, 2));
        newPits = gameStatusCalculator.redistributeStones(gameEntity, 6);
        assertThat(newPits[0], equalTo(1));
        assertThat(newPits[1], equalTo(2));
        assertThat(newPits[2], equalTo(2));
        assertThat(newPits[3], equalTo(0));
        assertThat(newPits[4], equalTo(2));
        assertThat(newPits[5], equalTo(2));
        assertThat(newPits[6], equalTo(0));
        assertThat(newPits[7], equalTo(3));
    }

    @Test
    @DisplayName("move should collect all stones to kalah pits when endgame condition is met")
    public void move_with_endgameCondition_should_moveAllStonesToKalahPits() throws IllegalGameMoveException {
        when(gameEntity.getPits()).thenReturn(List.of(0, 0, 1, 5, 1, 2, 1, 2));
        int[] newPits = gameStatusCalculator.redistributeStones(gameEntity, 2);
        assertThat(newPits[0], equalTo(0));
        assertThat(newPits[1], equalTo(0));
        assertThat(newPits[2], equalTo(0));
        assertThat(newPits[3], equalTo(6));
        assertThat(newPits[4], equalTo(0));
        assertThat(newPits[5], equalTo(0));
        assertThat(newPits[6], equalTo(0));
        assertThat(newPits[7], equalTo(6));

        when(gameEntity.getPits()).thenReturn(List.of(2, 1, 1, 2, 0, 0, 1, 5));
        newPits = gameStatusCalculator.redistributeStones(gameEntity, 6);
        assertThat(newPits[0], equalTo(0));
        assertThat(newPits[1], equalTo(0));
        assertThat(newPits[2], equalTo(0));
        assertThat(newPits[3], equalTo(6));
        assertThat(newPits[4], equalTo(0));
        assertThat(newPits[5], equalTo(0));
        assertThat(newPits[6], equalTo(0));
        assertThat(newPits[7], equalTo(6));
    }

}
