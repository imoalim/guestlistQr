package com.example.weddingcheckinapp.Config;
import com.example.weddingcheckinapp.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Behandlung von NoRemainingSeatsException
    @ExceptionHandler(CheckInService.NoRemainingSeatsException.class)
    public ResponseEntity<?> handleNoRemainingSeatsException(CheckInService.NoRemainingSeatsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    // Behandlung von InvitationNotFoundException
    @ExceptionHandler(CheckInService.InvitationNotFoundException.class)
    public ResponseEntity<?> handleInvitationNotFoundException(CheckInService.InvitationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    // Generischer Exception-Handler (für unerwartete Fehler)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Logge den Fehler für Debugging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Ein unerwarteter Fehler ist aufgetreten."));
    }
}

