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
    private int allowedGuests;
    private boolean checkedIn;

}

