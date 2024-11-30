package com.example.weddingcheckinapp.dal.repo;

import com.example.weddingcheckinapp.dal.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Optional<Invitation> findByQrCodeHash(String qrCodeHash);
}

