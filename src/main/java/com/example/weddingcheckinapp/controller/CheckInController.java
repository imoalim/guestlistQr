package com.example.weddingcheckinapp.controller;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import com.example.weddingcheckinapp.dal.repo.InvitationRepository;
import com.example.weddingcheckinapp.service.CheckInService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    private final InvitationRepository invitationRepository;

 @PostMapping
 public ResponseEntity<?> processQRCode(@RequestBody Map<String, String> payload) {
     try {
         // Extrahiere den QR-Code aus der Anfrage
         String qrCode = payload.get("qrCode");
         System.out.println("Erhaltener QR-Code: " + qrCode); // Logge den QR-Code für Debugging-Zwecke

         // Fehlerbehandlung: QR-Code ist leer oder null
         if (qrCode == null || qrCode.trim().isEmpty()) {
             String errorMessage = "QR-Code darf nicht leer sein.";
             System.err.println(errorMessage); // Logge den Fehler
             return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
         }

         // Logik zur Verarbeitung des QR-Codes
         String responseMessage = checkInService.validateQRCode(qrCode);

         // Erfolg: Rückgabe der Nachricht
         return ResponseEntity.ok(Map.of("message", responseMessage));
//
     } catch (RuntimeException e) {
         String errorMessage = "Fehler beim Verarbeiten des QR-Codes: " + e.getMessage();
         System.err.println(errorMessage); // Logge den Fehler
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", errorMessage));
     } catch (Exception e) {
         String errorMessage = "Interner Serverfehler: " + e.getMessage();
         e.printStackTrace(); // Protokolliere den vollständigen Stacktrace
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", errorMessage));
     }
 }


    @GetMapping
    public ResponseEntity<String> getCheckIn() {
        return ResponseEntity.ok("CheckInController works");
    }

    @GetMapping("/guests")
    public List<Invitation> getAllGuests() {
        return invitationRepository.findAll();
    }
    @PutMapping("/guests/{id}")
    public ResponseEntity<?> updateGuest(@PathVariable UUID id, @RequestBody Invitation updatedInvitation) {
        return invitationRepository.findById(id)
                .map(invitation -> {
                    // Aktualisiere die Felder des Gastes
                    invitation.setGuestName(updatedInvitation.getGuestName());
                    invitation.setAllowedGuests(updatedInvitation.getAllowedGuests());
                    invitation.setCheckedIn(updatedInvitation.isCheckedIn());
                    invitation.setRemainingGuests(updatedInvitation.getRemainingGuests());

                    // Speichere die aktualisierten Daten
                    invitationRepository.save(invitation);

                    // Rückgabe des aktualisierten Gastes
                    return ResponseEntity.ok(invitation);
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body((Invitation) Map.of("message", "Gast nicht gefunden"))); // Einheitlicher Typ
    }

}
