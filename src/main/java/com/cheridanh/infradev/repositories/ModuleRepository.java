package com.cheridanh.infradev.repositories;

import com.cheridanh.infradev.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    /**
     * Vérifie si le nom du module est déjà utilisé (insensible à la casse).
     *
     * @param name le nom du module
     * @return true si le module existe déjà
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Vérifie si le nom du module est déjà utilisé par un autre module
     * (insensible à la casse), en excluant un identifiant donné.
     *
     * @param name le nom à vérifier
     * @param id l'identifiant du module à exclure
     * @return true si le nom est déjà pris par un autre module
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Récupère tous les modules d'un bloc donné.
     *
     * @param blocId l'identifiant du bloc
     * @return la liste des modules du bloc
     */
    List<Module> findByBlockId(Long blocId);
}
