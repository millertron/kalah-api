package com.millertronics.kalahapi.exceptions.handlers;

import com.millertronics.kalahapi.exceptions.GameNotFoundException;
import com.millertronics.kalahapi.exceptions.IllegalGameMoveException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public void handleNoSuchElement(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "Invalid game ID.");
    }

    @ExceptionHandler(IllegalGameMoveException.class)
    public void handleIllegalGameMove(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Illegal game move.");
    }
}
