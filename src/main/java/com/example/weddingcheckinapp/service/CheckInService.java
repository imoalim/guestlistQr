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
                .orElseThrow(() -> new RuntimeException("Einladung nicht gefunden"));

        // Prüfen, ob der Gast bereits eingecheckt ist
        if (invitation.isCheckedIn()) {
            return "Gast ist bereits eingecheckt.";
        }

        // Einladung ist gültig
        invitation.setCheckedIn(true);
        invitationRepository.save(invitation); // Status aktualisieren
        return String.format("Willkommen %s! Erlaubte Begleitpersonen: %d",
                invitation.getGuestName(), invitation.getAllowedGuests());
    }
}

