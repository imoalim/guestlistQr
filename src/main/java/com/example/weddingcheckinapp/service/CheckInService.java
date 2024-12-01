package com.example.weddingcheckinapp.service;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import com.example.weddingcheckinapp.dal.repo.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckInService {

        @Autowired
        private InvitationRepository invitationRepository;

    public String validateQRCode(String qrCodeContent) {
        // Suche den QR-Code in der Datenbank
        Invitation invitation = invitationRepository.findByQrCodeHash(qrCodeContent)
                .orElseThrow(() -> new RuntimeException("Einladung nicht gefunden. Bitte überprüfen Sie den QR-Code."));

        // Prüfen, ob noch verbleibende Plätze vorhanden sind
        //TODO:Fix remaining guests
        if (invitation.getRemainingGuests() <=0) {
            throw new RuntimeException("Keine verbleibenden Plätze für diese Einladung.");
        }

        // Verbleibende Gäste reduzieren
        invitation.setRemainingGuests((invitation.getRemainingGuests()) - 1);
        invitationRepository.save(invitation);

        // Erfolgreiche Rückmeldung
        return String.format("Check-In erfolgreich! Willkommen '%s'. Verbleibende Plätze: %d",
                invitation.getGuestName(), invitation.getRemainingGuests());
    }
}



