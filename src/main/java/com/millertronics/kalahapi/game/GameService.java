package com.millertronics.kalahapi.game;

import com.millertronics.kalahapi.exceptions.GameNotFoundException;
import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for GameEntity
 */
@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameStatusCalculator gameStatusCalculator;

    /**
     * Initializes a new game and saves to repository
     * @return gameEntity created
     */
    public GameEntity createGame() {
        return gameRepository.save(new GameEntity());
    }

    /**
     * Processes player move on a game.
     * The game's status is updated after calculation.
     * The updated game is saved to repository and is returned.
     * Exceptions are thrown if no game is found by the provided gameId
     * or if the move made using the provided pitIndex is illegal.
     *
     * @param gameId entity ID of the game
     * @param pitIndex zero-based array index of the pit
     * @return gameEntity after status update
     * @throws GameNotFoundException if gameId doesn't match an existing game
     * @throws IllegalGameMoveException if pitIndex is invalid
     */
    public GameEntity makeMove(final int gameId, final int pitIndex) throws GameNotFoundException, IllegalGameMoveException {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found for ID: " + gameId));

        int[] newPits = gameStatusCalculator.redistributeStones(game, pitIndex);
        game.updateStatus(newPits);

        return gameRepository.save(game);
    }


}
