package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.ExerciseRequest;
import com.cheridanh.infradev.dtos.response.ExerciseResponse;

import java.util.List;

public interface ExerciseService {

    /**
     * Récupère tous les exercices.
     *
     * @return la liste des exercices
     */
    List<ExerciseResponse> getAllExercises();

    /**
     * Récupère un exercice par son identifiant.
     *
     * @param id l'identifiant de l'exercice
     * @return l'exercice correspondant
     */
    ExerciseResponse getExerciseById(Long id);

    /**
     * Récupère tous les exercices d'un module donné.
     *
     * @param moduleId l'identifiant du module
     * @return la liste des exercices du module
     */
    List<ExerciseResponse> getExercisesByModuleId(Long moduleId);

    /**
     * Crée un nouvel exercice rattaché à un module.
     *
     * @param exerciseRequest les données de l'exercice à créer
     * @return l'exercice créé
     */
    ExerciseResponse createExercise(ExerciseRequest exerciseRequest);

    /**
     * Met à jour un exercice existant.
     *
     * @param id l'identifiant de l'exercice à modifier
     * @param exerciseRequest les nouvelles données
     * @return l'exercice mis à jour
     */
    ExerciseResponse updateExercise(Long id, ExerciseRequest exerciseRequest);

    /**
     * Supprime définitivement un exercice.
     *
     * @param id l'identifiant de l'exercice à supprimer
     */
    void deleteExercise(Long id);

    /**
     * Bascule l'état complété/non complété d'un exercice.
     *
     * @param id l'identifiant de l'exercice
     * @return l'exercice avec son nouvel état
     */
    ExerciseResponse toggleCompleted(Long id);
}
