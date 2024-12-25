package com.projetTB.projetTB.Auth.repository;

import com.projetTB.projetTB.Auth.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> { // Changed the second parameter to Long for the ID type

    // Find a user by Email
    Optional<Users> findByEmail(String email);

    // Check whether the user exists or not:
    boolean existsById(Long id); // Changed the parameter type to Long

    // Check if a user exists by Email
    boolean existsByEmail(String email);

    // Count the number of users with a specific role (assuming Users have a role field)
    long countByRole(String role);

    // Delete a user by Email
    void deleteByEmail(String email);
}
