package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Vérifie si le nom du cours est déjà utilisé (insensible à la casse).
     *
     * @param name le nom du cours
     * @return true si le cours existe déjà
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Vérifie si le nom du cours est déjà utilisé par un autre cours
     * (insensible à la casse), en excluant un identifiant donné.
     *
     * @param name le nom à vérifier
     * @param id l'identifiant du cours à exclure
     * @return true si le nom est déjà pris par un autre cours
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Récupère tous les cours d'un module donné.
     *
     * @param moduleId l'identifiant du module
     * @return la liste des cours du module
     */
    List<Course> findByModuleId(Long moduleId);
}
