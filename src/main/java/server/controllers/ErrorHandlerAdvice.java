package server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.exceptions.IncorrectAudioException;
import server.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class ErrorHandlerAdvice {
  @ExceptionHandler(IncorrectAudioException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public IncorrectAudioException handleIncorrectAudioException(IncorrectAudioException e) {
    return e;
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResourceNotFoundException handleResourceNotFoundException(ResourceNotFoundException e) {
    return e;
  }
}
