package es.salesianos.controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class WakaControllerAdvice {
	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<String> handleRunTimeException(RuntimeException e) {
		return error(INTERNAL_SERVER_ERROR, e);
	}

	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<String> handleNotFoundException(NoSuchElementException e) {
		return error(NOT_FOUND, e);
	}

	private ResponseEntity<String> error(HttpStatus status, Exception e) {
		log.error("Exception : ", e);
		return ResponseEntity.status(status).body(e.getMessage());
	}
}