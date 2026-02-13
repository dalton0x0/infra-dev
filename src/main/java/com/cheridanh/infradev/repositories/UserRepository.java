package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son email.
     *
     * @param email l'email de l'utilisateur
     * @return un Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email est déjà utilisé.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    boolean existsByEmail(String email);
}
