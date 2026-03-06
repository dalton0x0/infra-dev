package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Vérifie si le nom de l'exercice est déjà utilisé (insensible à la casse).
     *
     * @param name le nom de l'exercice
     * @return true si l'exercice existe déjà
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Vérifie si le nom de l'exercice est déjà utilisé par un autre exercice
     * (insensible à la casse), en excluant un identifiant donné.
     *
     * @param name le nom à vérifier
     * @param id l'identifiant de l'exercice à exclure
     * @return true si le nom est déjà pris par un autre exercice
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Récupère tous les exercices d'un module donné.
     *
     * @param moduleId l'identifiant du module
     * @return la liste des exercices du module
     */
    List<Exercise> findByModuleId(Long moduleId);
}
