package com.millertronics.kalahapi.game;

import com.millertronics.kalahapi.exceptions.GameStatusViolationException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Entity class representing a game
 */
@Entity
public class GameEntity {

    private static final int PIT_SIZE = 14;
    private static final int DEFAULT_STONE_COUNT = 6;

    /**
     * Database ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    /**
     * String representation of the game pits
     */
    @Pattern(regexp = "\\d+(:\\d+)+:\\d+")
    private String status;


    /**
     * Constructor initializes a game with 7x7 pits with 6 stones added to every non-kalah pits
     */
    public GameEntity() {
        int[] pits = new int[PIT_SIZE];
        Arrays.fill(pits, DEFAULT_STONE_COUNT);
        pits[PIT_SIZE / 2 - 1] = 0;
        pits[PIT_SIZE - 1] = 0;
        setStatus(pits);
    }

    /**
     * Converts an integer array representation of pits to colon-delimited string
     * @param pits integer array representation of pits
     */
    private void setStatus(final int[] pits) {
        this.status = Arrays.stream(pits)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(":"));
    }

    /**
     * Validated conversion of an integer array representation of pits to colon-delimited string
     * Throws an exception if the input array is different size to the array representation of the current game pits
     *
     * @param pits integer array representation of pits
     * @throws GameStatusViolationException if pits is different size to the pits in the game
     */
    public void updateStatus(final int[] pits) throws GameStatusViolationException {
        if (status != null && pits.length != getPits().size()) {
            throw new GameStatusViolationException(String.format("Cannot change size of pits: %d to %d", getPits().size(), pits.length));
        }
        setStatus(pits);
    }

    /**
     * Gets integer list representation of game pits
     * @return integer list representation of game pits
     */
    public List<Integer> getPits() {
        return Stream.of(status.split(":"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Formats the status string into a key-value map.
     * The key is string representation of the pit position and is one-based unlike the array index.
     * The value is string representation of the number of stones in each pit.
     *
     * @return Map representation of game pits
     */
    public Map<String, String> getFormattedStatus() {
        Map<String, String> formattedStatus = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));
        String[] pits = status.split(":");
        IntStream.rangeClosed(1, pits.length)
                .forEachOrdered(i -> formattedStatus.put(String.valueOf(i), (pits[i-1])));
        return formattedStatus;
    }
}
