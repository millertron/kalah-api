package com.millertronics.kalahapi.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.millertronics.kalahapi.exceptions.GameNotFoundException;
import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;

/**
 * Rest controller for the Game domain
 */
@RestController
@AllArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles POST request to create a new game
     *
     * @param request incoming HttpServletRequest
     * @return Json response containing id and url
     */
    @Operation(summary = "Create a new game")
    @ApiResponse(responseCode = "201")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createGame(final HttpServletRequest request) {
        GameEntity game =  gameService.createGame();

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("id", String.valueOf(game.getId()));
        jsonNode.put("url", request.getRequestURL().toString());

        return new ResponseEntity<>(jsonNode, HttpStatus.CREATED);
    }

    /**
     * Handles PUT request of players making a move in the game
     * Returns status 200 if successful, 404 if no game is found, 400 if game move is illegal
     *
     * @param request incoming HttpServletRequest
     * @param gameId ID of game
     * @param pitId Integer representing position of pit in game
     * @return Json node containing id, url and status
     * @throws IllegalGameMoveException if pitId is invalid
     * @throws GameNotFoundException if gameId doesn't match an existing game
     */
    @Operation(summary = "Carry out a player move")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "404")
    @PutMapping(path ="/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> play(final HttpServletRequest request,
                                       @PathVariable final int gameId,
                                       @Min(1) @PathVariable final int pitId)
            throws IllegalGameMoveException, GameNotFoundException {
        GameEntity game = gameService.makeMove(gameId, pitId - 1);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("id", String.valueOf(game.getId()));
        jsonNode.put("url", request.getRequestURL().toString());
        ObjectNode status = objectMapper.valueToTree(game.getFormattedStatus());
        jsonNode.set("status", status);

        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }
}
