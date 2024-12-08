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

    @Column(name = "guest_name") // Matches "guest_name" in the database
    private String guestName;

    @Column(name = "qr_code_hash") // Matches "qr_code_hash" in the database
    private String qrCodeHash;

    @Column(name = "allowed_guests") // Matches "allowed_guests" in the database
    private int allowedGuests;

    @Column(name = "remaining_guests") // Matches "remaining_guests" in the database
    private int remainingGuests;

    @Column(name = "checked_in") // Matches "checked_in" in the database
    private boolean checkedIn;

    public void decrementRemainingGuests(int count) {
        if (remainingGuests >= count) {
            remainingGuests -= count;
        } else {
            throw new RuntimeException("Nicht genügend verbleibende Gästeplätze.");
        }
    }
}


