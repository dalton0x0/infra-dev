package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /**
     * Vérifie si le nom du quiz est déjà utilisé (insensible à la casse).
     *
     * @param name le nom du quiz
     * @return true si le quiz existe déjà
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Vérifie si le nom du quiz est déjà utilisé par un autre quiz
     * (insensible à la casse), en excluant un identifiant donné.
     *
     * @param name le nom à vérifier
     * @param id l'identifiant du quiz à exclure
     * @return true si le nom est déjà pris par un autre quiz
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Vérifie si un module possède déjà un quiz.
     *
     * @param moduleId l'identifiant du module
     * @return true si le module a déjà un quiz
     */
    boolean existsByModuleId(Long moduleId);

    /**
     * Vérifie si un autre module (hors celui du quiz courant) possède déjà un quiz.
     *
     * @param moduleId l'identifiant du module
     * @param id l'identifiant du quiz à exclure
     * @return true si un autre quiz est déjà rattaché au module
     */
    boolean existsByModuleIdAndIdNot(Long moduleId, Long id);
}
