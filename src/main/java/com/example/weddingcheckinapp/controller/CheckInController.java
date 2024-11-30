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
    @PostMapping()
    public ResponseEntity<String> checkIn(@RequestBody String qrCodeContent) {
        try {
            String result = checkInService.validateQRCode(qrCodeContent)+"\nWORKS";
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> getCheckIn() {
        return ResponseEntity.ok("CheckInController works");
    }
}
