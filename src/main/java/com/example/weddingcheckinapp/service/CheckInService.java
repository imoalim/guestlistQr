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
        try {
            // Suche den QR-Code in der Datenbank
            Invitation invitation = invitationRepository.findByQrCodeHash(qrCodeContent)
                    .orElseThrow(() -> new RuntimeException("Einladung nicht gefunden. Bitte überprüfen Sie den QR-Code."));

            // Fehlerbehandlung: Gast ist bereits eingecheckt
            if (invitation.isCheckedIn()) {
                return String.format("Gast '%s' ist bereits eingecheckt.", invitation.getGuestName());
            }

            // Einladung ist gültig: Status aktualisieren
            invitation.setCheckedIn(true);
            invitationRepository.save(invitation);

            // Erfolgreiche Verarbeitung
            return String.format("Willkommen '%s'! Erlaubte Begleitpersonen: %d",
                    invitation.getGuestName(), invitation.getAllowedGuests());

        } catch (RuntimeException e) {
            // Wirf eine RuntimeException für bekannte Fehlerfälle
            throw e;
        } catch (Exception e) {
            // Unbekannte Fehler behandeln
            throw new RuntimeException("Ein interner Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    }
}


