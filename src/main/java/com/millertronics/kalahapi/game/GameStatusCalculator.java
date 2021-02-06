package com.millertronics.kalahapi.game;

import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Calculates the updated game pits when a player makes a move
 */
@Component
@NoArgsConstructor
public class GameStatusCalculator {

    /**
     * Get the updated pits of a given game after moving the stones from a pit specified by an index.
     * Index must be within the pits array must not be either of the kalah pits or an empty pit
     *
     * @param gameEntity game containing the pits to update
     * @param pitIndex zero-based array index of the game pits from which the stone is moved from
     * @return integer array representing the updated pits
     * @throws IllegalGameMoveException if pitIndex is invalid
     */
    public int[] redistributeStones(final GameEntity gameEntity, final int pitIndex)
            throws IllegalGameMoveException {

        List<Integer> originalPits = gameEntity.getPits();
        final int kalahOne = originalPits.size() / 2 - 1;
        final int kalahTwo = originalPits.size() - 1;

        if (pitIndex < 0 || pitIndex > kalahTwo) {
            throw new IllegalGameMoveException(String.format("Invalid index %d selected (out of %d)", pitIndex, kalahTwo));
        }
        if (pitIndex == kalahOne || pitIndex == kalahTwo) {
            throw new IllegalGameMoveException("Cannot move stones from the kalah pits");
        }
        if (originalPits.get(pitIndex) == 0) {
            throw new IllegalGameMoveException("Cannot move stones from an empty pit");
        }

        final int playerKalahIndex = pitIndex < kalahOne ? kalahOne : kalahTwo;
        final int oppositeKalahIndex = playerKalahIndex == kalahOne ? kalahTwo : kalahOne;

        final int pitStones = originalPits.get(pitIndex);

        int[] gamePits = new int[originalPits.size()];
        List<Integer> updatedIndexes = new ArrayList<>(); // or indices?

        gamePits[pitIndex] = 0;
        updatedIndexes.add(pitIndex);

        int offset = pitIndex;
        for (int i = 1; i <= pitStones; i++) {
            int index = offset + i;

            // skip the kalah pit of the opposite player
            if (index == oppositeKalahIndex) {
                offset++;
                index++;
            }
            // zero the index if we've gone past the edge
            if (index > kalahTwo) {
                offset = -i;
                index = 0;
            }

            // add one stone to the resulting pit - it is updated
            gamePits[index] = originalPits.get(index) + 1;
            updatedIndexes.add(index);

            // if the last stone added was on an empty player-side, non-kalah pit, take all the stones
            // from the pit on the opposite side
            final boolean lastPitAddedWasEmpty = i == pitStones && gamePits[index] == 1;
            final boolean lastPitIsInPlayerSide = pitIsInPlayerSide(index, playerKalahIndex, kalahOne);
            final boolean lastPitIsNonKalah = index != kalahOne && index != kalahTwo;

            if (lastPitAddedWasEmpty && lastPitIsInPlayerSide && lastPitIsNonKalah) {
                final int oppositeIndex = findOppositeIndex(index, originalPits.size());
                gamePits[index] += originalPits.get(oppositeIndex);
                gamePits[oppositeIndex] = 0;
                updatedIndexes.add(oppositeIndex);
            }

        }

        // The pits that were unchanged are set to original value
        for (int i = 0; i < gamePits.length; i++) {
            if (!updatedIndexes.contains(i)) {
                gamePits[i] = originalPits.get(i);
            }
        }

        // If the game has reached its end then collect all the stones to the kalah pits
        return checkEndGame(gamePits);
    }

    /**
     * Checks if a given array index corresponds to a position of any of the pits in the player's side.
     *
     * @param pitIndex array index to check
     * @param playerKalahIndex the index of the player's kalah pit
     * @param lowerKalahIndex the lower of indexes of the two kalah pit in the game
     * @return true if pitIndex lies on the player's side
     */
    private boolean pitIsInPlayerSide(final int pitIndex, final int playerKalahIndex, final int lowerKalahIndex) {
        if (pitIndex <= playerKalahIndex) {
            return playerKalahIndex == lowerKalahIndex || pitIndex > lowerKalahIndex;
        }
        return false;
    }

    /**
     * Returns the array index corresponding to the pit on the opposite side<br />
     *
     * eg: for a 4x4 pit where pit 3 and 7 are kalah pits<br />
     * |7|6|5|4|_|<br />
     * |_|0|1|2|3|<br />
     * opposite index for 0 -> 4, 1 -> 5, 2 -> 6 and vice versa
     * for kalah pits, the index returned corresponds to their opposite kalah pit
     *
     * @param pitIndex zero-based array index
     * @param pitSize size of pit array
     * @return integer index corresponding to the opposite
     */
    private int findOppositeIndex(final int pitIndex, final int pitSize) {
        final int kalahOneIndex = pitSize / 2 - 1;
        final int range = kalahOneIndex - pitIndex;
        // Wouldn't be sensible to use this method for finding out the opposite kalah pit
        // but for completeness
        if (pitIndex == kalahOneIndex) {
            return pitSize - 1;
        } else if (pitIndex == pitSize - 1) {
            return kalahOneIndex;
        }
        return kalahOneIndex + range;
    }

    /**
     * Checks if the current game pits have reached its end condition (i.e. one side has all its non-kalah pits empty).
     * If end condition is met, the stones from the side with non-empty, non-kalah pits are collected an put in their kalah pit.
     * The integer array representing the resulting game pits is returned.
     *
     * @param gamePits integer array representing the current game pits
     * @return integer array represents game status updated with all the stones collected to the kalah pits
     */
    private int[] checkEndGame(final int[] gamePits) {
        final int kalahOne = gamePits.length / 2 - 1;
        final int kalahTwo = gamePits.length - 1;

        int side1Stones = 0;
        int side2Stones = 0;
        for (int i = 0; i < kalahOne; i++) {
            side1Stones += gamePits[i];
        }
        for (int i = kalahOne + 1; i < kalahTwo; i++) {
            side2Stones += gamePits[i];
        }
        if (side1Stones == 0 || side2Stones == 0) {
            int[] endGamePits = new int[gamePits.length];
            Arrays.fill(endGamePits, 0);
            endGamePits[kalahOne] = gamePits[kalahOne] + side1Stones;
            endGamePits[kalahTwo] = gamePits[kalahTwo] + side2Stones;

            return endGamePits;
        }
        return gamePits;
    }
}
