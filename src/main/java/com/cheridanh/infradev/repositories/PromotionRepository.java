package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     * Vérifie si un nom de promotion est déjà utilisé (insensible à la casse).
     *
     * @param name le nom de la promotion
     * @return true si le nom existe déjà
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Vérifie si un nom de promotion est déjà utilisé par une autre promotion
     * (insensible à la casse), en excluant un identifiant donné.
     *
     * @param name le nom à vérifier
     * @param id l'identifiant de la promotion à exclure
     * @return true si le nom est déjà pris par une autre promotion
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Recherche la promotion active dont la plage de dates englobe la date fournie.
     *
     * @param date la date de référence (typiquement la date du jour)
     * @return la promotion active correspondante, ou {@link Optional#empty()} si aucune
     */
    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :date AND p.endDate >= :date")
    Optional<Promotion> findActiveByDate(@Param("date") LocalDate date);
}
