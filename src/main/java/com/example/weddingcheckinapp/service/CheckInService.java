package com.example.weddingcheckinapp.service;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import com.example.weddingcheckinapp.dal.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Service
public class CheckInService {

    @Autowired
    private InvitationRepository invitationRepository;

    public String validateQRCode(String qrCodeContent) {
        // Suche den QR-Code in der Datenbank
        Invitation invitation = invitationRepository.findByQrCodeHash(qrCodeContent)
                .orElseThrow(() -> new InvitationNotFoundException("Einladung nicht gefunden. Bitte überprüfen Sie den QR-Code: " + qrCodeContent));

        // Berechnung: Erlaubte Gäste + Gastgeber
        int totalAllowedGuests = invitation.getAllowedGuests() + 1;

        // Prüfen, ob noch Plätze verfügbar sind
        if (invitation.getRemainingGuests() <= 0) {
            throw new NoRemainingSeatsException(invitation.getGuestName());
        }

        // Verbleibende Gäste reduzieren
        invitation.setRemainingGuests(invitation.getRemainingGuests() - 1);
        invitationRepository.save(invitation);

        // Erfolgreiche Rückmeldung
        return String.format("Check-In erfolgreich! Willkommen '%s'. Verbleibende Plätze: %d von %d",
                invitation.getGuestName(), invitation.getRemainingGuests(), totalAllowedGuests);
    }

    // Exception für fehlende Plätze
    public static class NoRemainingSeatsException extends RuntimeException {
        public NoRemainingSeatsException(String guestName) {
            super("Keine verbleibenden Plätze für " + guestName + " für diese Einladung.");
        }
    }

    // Exception für nicht gefundene Einladungen
    public static class InvitationNotFoundException extends RuntimeException {
        public InvitationNotFoundException(String message) {
            super(message);
        }
    }
}


