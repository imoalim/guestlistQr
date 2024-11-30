package com.example.weddingcheckinapp.controller;

import com.example.weddingcheckinapp.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;
 /*   @PostMapping
    public ResponseEntity<?> checkInGuest(@RequestBody Map<String, String> payload) {
        String qrCode = payload.get("qrCode");

        // Beispiel: QR-Code überprüfen
        if (qrCode == null || qrCode.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "QR-Code ist leer."));
        }

        // Dummy-Logik: QR-Code validieren (Hier mit DB-Abfrage erweitern)
        if (qrCode.equals("INVITATION_123")) {
            return ResponseEntity.ok(Map.of("message", "Willkommen zur Hochzeit!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Ungültige Einladung."));
        }
    }*/
 @PostMapping
 public ResponseEntity<?> processQRCode(@RequestBody Map<String, String> payload) {
     try {
         // Extrahiere den QR-Code aus der Anfrage
         String qrCode = payload.get("qrCode");

         // Fehlerbehandlung: QR-Code ist leer oder null
         if (qrCode == null || qrCode.trim().isEmpty()) {
             return ResponseEntity.badRequest().body(Map.of("message", "QR-Code darf nicht leer sein."));
         }

         // Logik zur Verarbeitung des QR-Codes
         String responseMessage = checkInService.validateQRCode(qrCode);

         // Erfolg: Rückgabe der Nachricht
         return ResponseEntity.ok(Map.of("message", responseMessage));

     } catch (RuntimeException e) {
         // Fehler: QR-Code existiert nicht oder ist ungültig
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
     } catch (Exception e) {
         // Interner Serverfehler
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Ein interner Fehler ist aufgetreten. Bitte versuchen Sie es später erneut."));
     }
 }

    @GetMapping
    public ResponseEntity<String> getCheckIn() {
        return ResponseEntity.ok("CheckInController works");
    }
}
