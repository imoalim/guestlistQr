package com.example.weddingcheckinapp.dal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String guestName;
    private String qrCodeHash; // QR-Inhalt (Hash oder plain)
    private int allowedGuests; // Gesamtanzahl erlaubter Gäste
    private int remainingGuests; // Verbleibende Gäste
    private boolean checkedIn; // Hauptgast eingecheckt

    public void decrementRemainingGuests(int count) {
        if (remainingGuests >= count) {
            remainingGuests -= count;
        } else {
            throw new RuntimeException("Nicht genügend verbleibende Gästeplätze.");
        }
    }
}


