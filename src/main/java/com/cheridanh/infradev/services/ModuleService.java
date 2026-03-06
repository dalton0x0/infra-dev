package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.ModuleRequest;
import com.cheridanh.infradev.dtos.response.ModuleResponse;

import java.util.List;

public interface ModuleService {

    /**
     * Récupère tous les modules.
     *
     * @return la liste des modules
     */
    List<ModuleResponse> getAllModules();

    /**
     * Récupère un module par son identifiant, avec ses cours, exercices et quiz.
     *
     * @param id l'identifiant du module
     * @return le module avec ses détails
     */
    ModuleResponse getModuleById(Long id);

    /**
     * Récupère tous les modules d'un bloc donné.
     *
     * @param blockId l'identifiant du bloc
     * @return la liste des modules du bloc
     */
    List<ModuleResponse> getModulesByBlockId(Long blockId);

    /**
     * Crée un nouveau module rattaché à un bloc.
     *
     * @param moduleRequest les données du module à créer
     * @return le module créé
     */
    ModuleResponse createModule(ModuleRequest moduleRequest);

    /**
     * Met à jour un module existant.
     *
     * @param id l'identifiant du module à modifier
     * @param moduleRequest les nouvelles données
     * @return le module mis à jour
     */
    ModuleResponse updateModule(Long id, ModuleRequest moduleRequest);

    /**
     * Supprime définitivement un module.
     *
     * @param id l'identifiant du module à supprimer
     */
    void deleteModule(Long id);
}
