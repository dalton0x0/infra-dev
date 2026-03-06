package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Vérifie si un email est déjà utilisé par un autre utilisateur.
     *
     * @param email l'email à vérifier
     * @param id l'identifiant de l'utilisateur à exclure
     * @return true si l'email est déjà pris par un autre utilisateur
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Récupère tous les utilisateurs d'une promotion donnée.
     *
     * @param promotionId l'identifiant de la promotion
     * @return la liste des utilisateurs de la promotion
     */
    List<User> findByPromotionId(Long promotionId);
}
