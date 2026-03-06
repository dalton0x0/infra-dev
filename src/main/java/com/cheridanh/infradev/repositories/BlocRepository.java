package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.Bloc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlocRepository extends JpaRepository<Bloc, Long> {

    /**
     * Vérifie si le nom du bloc est déjà utilisé (insensible à la casse).
     *
     * @param name le nom du bloc
     * @return true si le bloc existe déjà
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Vérifie si le nom du bloc est déjà utilisé par un autre bloc
     * (insensible à la casse), en excluant un identifiant donné.
     *
     * @param name le nom à vérifier
     * @param id l'identifiant du bloc à exclure
     * @return true si le nom est déjà pris par un autre bloc
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);
}
