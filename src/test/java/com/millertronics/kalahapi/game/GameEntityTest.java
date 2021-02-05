package com.millertronics.kalahapi.game;

import com.millertronics.kalahapi.exceptions.GameStatusViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameEntityTest {

    @Test
    @DisplayName("constructor should initialize gameEntity with populated pits")
    public void testConstructor() {
        GameEntity gameEntity = new GameEntity();

        final List<Integer> pits = gameEntity.getPits();
        assertThat(pits.size(), equalTo(14));

        assertThat(pits.get(0), equalTo(6));
        assertThat(pits.get(1), equalTo(6));
        assertThat(pits.get(2), equalTo(6));
        assertThat(pits.get(3), equalTo(6));
        assertThat(pits.get(4), equalTo(6));
        assertThat(pits.get(5), equalTo(6));
        assertThat(pits.get(6), equalTo(0));

        assertThat(pits.get(7), equalTo(6));
        assertThat(pits.get(8), equalTo(6));
        assertThat(pits.get(9), equalTo(6));
        assertThat(pits.get(10), equalTo(6));
        assertThat(pits.get(11), equalTo(6));
        assertThat(pits.get(12), equalTo(6));
        assertThat(pits.get(13), equalTo(0));
    }

    @Test
    @DisplayName("updateStatus should succeed and set the new pits")
    public void updateStatus_should_succeed() throws GameStatusViolationException {
        GameEntity gameEntity = new GameEntity();

        int[] input = new int[]{
                0, 7, 7, 7, 7, 7, 1,
                6, 6, 6, 6, 6, 6, 0
            };
        gameEntity.updateStatus(input);

        final List<Integer> pits = gameEntity.getPits();
        assertThat(pits.get(0), equalTo(0));
        assertThat(pits.get(1), equalTo(7));
        assertThat(pits.get(2), equalTo(7));
        assertThat(pits.get(3), equalTo(7));
        assertThat(pits.get(4), equalTo(7));
        assertThat(pits.get(5), equalTo(7));
        assertThat(pits.get(6), equalTo(1));

        assertThat(pits.get(7), equalTo(6));
        assertThat(pits.get(8), equalTo(6));
        assertThat(pits.get(9), equalTo(6));
        assertThat(pits.get(10), equalTo(6));
        assertThat(pits.get(11), equalTo(6));
        assertThat(pits.get(12), equalTo(6));
        assertThat(pits.get(13), equalTo(0));

    }

    @Test
    @DisplayName("updateStatus with input of different size to original status " +
            "should throw a GameStatusViolationException")
    public void updateStatus_withInvalid_input_shouldThrow_GameStatusViolationException() {
        GameEntity gameEntity = new GameEntity();

        int[] input = new int[]{
                0, 7, 7, 7, 7, 7,
                6, 6, 6, 6, 6, 6
        };
        assertThrows(GameStatusViolationException.class,
                () -> gameEntity.updateStatus(input));
    }

    @Test
    @DisplayName("formattedStatus should return a map<String, String>")
    public void formattedStatus_shouldReturn_StringToStringMap() throws GameStatusViolationException {
        GameEntity gameEntity = new GameEntity();
        Map<String, String> formattedStatus = gameEntity.getFormattedStatus();
        assertThat(formattedStatus.get("1"), equalTo("6"));
        assertThat(formattedStatus.get("2"), equalTo("6"));
        assertThat(formattedStatus.get("3"), equalTo("6"));
        assertThat(formattedStatus.get("4"), equalTo("6"));
        assertThat(formattedStatus.get("5"), equalTo("6"));
        assertThat(formattedStatus.get("6"), equalTo("6"));
        assertThat(formattedStatus.get("7"), equalTo("0"));

        assertThat(formattedStatus.get("8"), equalTo("6"));
        assertThat(formattedStatus.get("9"), equalTo("6"));
        assertThat(formattedStatus.get("10"), equalTo("6"));
        assertThat(formattedStatus.get("11"), equalTo("6"));
        assertThat(formattedStatus.get("12"), equalTo("6"));
        assertThat(formattedStatus.get("13"), equalTo("6"));
        assertThat(formattedStatus.get("14"), equalTo("0"));

        int[] input = new int[]{
                0, 7, 7, 7, 7, 7, 1,
                6, 6, 6, 6, 6, 6, 0
        };
        gameEntity.updateStatus(input);
        formattedStatus = gameEntity.getFormattedStatus();
        assertThat(formattedStatus.get("1"), equalTo("0"));
        assertThat(formattedStatus.get("2"), equalTo("7"));
        assertThat(formattedStatus.get("3"), equalTo("7"));
        assertThat(formattedStatus.get("4"), equalTo("7"));
        assertThat(formattedStatus.get("5"), equalTo("7"));
        assertThat(formattedStatus.get("6"), equalTo("7"));
        assertThat(formattedStatus.get("7"), equalTo("1"));

        assertThat(formattedStatus.get("8"), equalTo("6"));
        assertThat(formattedStatus.get("9"), equalTo("6"));
        assertThat(formattedStatus.get("10"), equalTo("6"));
        assertThat(formattedStatus.get("11"), equalTo("6"));
        assertThat(formattedStatus.get("12"), equalTo("6"));
        assertThat(formattedStatus.get("13"), equalTo("6"));
        assertThat(formattedStatus.get("14"), equalTo("0"));
    }
}
