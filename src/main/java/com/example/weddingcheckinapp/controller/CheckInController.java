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
        String qrCode = payload.get("qrCode");
        if (qrCode == null || qrCode.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "QR-Code darf nicht leer sein."));
        }

        String responseMessage = checkInService.validateQRCode(qrCode);
        return ResponseEntity.ok(Map.of("message", responseMessage));
    }

    @GetMapping("/guests/{id}")
    public ResponseEntity<Invitation> getGuestById(@PathVariable UUID id) {
        return invitationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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
    @DeleteMapping("/guests/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable UUID id) {
        if (invitationRepository.existsById(id)) {
            invitationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
