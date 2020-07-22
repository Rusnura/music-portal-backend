package server.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.exceptions.IncorrectAudioException;
import server.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class ErrorHandlerAdvice {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @ExceptionHandler(IncorrectAudioException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public JsonNode handleIncorrectAudioException(IncorrectAudioException e) {
    return generateExceptionInJsonFormat(e);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public JsonNode handleResourceNotFoundException(ResourceNotFoundException e) {
    return generateExceptionInJsonFormat(e);
  }

  private JsonNode generateExceptionInJsonFormat(Exception e) {
    return objectMapper.createObjectNode().put("error", e.getMessage());
  }
}
