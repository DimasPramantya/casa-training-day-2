package com.example.casagrpctask.repository;

import com.example.casagrpctask.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> { // Mengubah Long menjadi UUID

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}