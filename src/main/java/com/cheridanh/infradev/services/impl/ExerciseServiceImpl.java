package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.ExerciseRequest;
import com.cheridanh.infradev.dtos.response.ExerciseResponse;
import com.cheridanh.infradev.entities.Exercise;
import com.cheridanh.infradev.entities.Module;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.ExerciseRepository;
import com.cheridanh.infradev.repositories.ModuleRepository;
import com.cheridanh.infradev.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponse> getAllExercises() {
        log.debug("Récupération de tous les exercices");

        List<ExerciseResponse> exercises = exerciseRepository.findAll()
                .stream()
                .map(ExerciseResponse::fromEntity)
                .toList();

        log.debug("Total d'exercices obtenus : {}", exercises.size());

        return exercises;
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponse getExerciseById(Long id) {
        log.debug("Récupération de l'exercice id : {}", id);
        Exercise exercise = findOrThrow(id);
        log.debug("Exercice récupéré : {}", exercise.getName());
        return ExerciseResponse.fromEntity(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponse> getExercisesByModuleId(Long moduleId) {
        log.debug("Récupération des exercices du module id : {}", moduleId);

        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module", moduleId);
        }

        List<ExerciseResponse> exercises = exerciseRepository.findByModuleId(moduleId)
                .stream()
                .map(ExerciseResponse::fromEntity)
                .toList();

        log.debug("Total d'exercices obtenus pour le module id {} : {}", moduleId, exercises.size());

        return exercises;
    }

    @Override
    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest exerciseRequest) {
        log.info("Tentative de création d'un exercice");

        if (exerciseRepository.existsByNameEqualsIgnoreCase(exerciseRequest.getName())) {
            throw new DuplicateResourceException("Exercice", "nom", exerciseRequest.getName());
        }

        Module module = moduleRepository.findById(exerciseRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", exerciseRequest.getModuleId()));

        Exercise exercise = buildNewExercise(exerciseRequest, module);
        exerciseRepository.save(exercise);

        log.info("Exercice créé avec succès, id : {}", exercise.getId());

        return ExerciseResponse.fromEntity(exercise);
    }

    @Override
    @Transactional
    public ExerciseResponse updateExercise(Long id, ExerciseRequest exerciseRequest) {
        log.info("Tentative de mise à jour de l'exercice id : {}", id);

        Exercise exercise = findOrThrow(id);

        if (exerciseRepository.existsByNameEqualsIgnoreCaseAndIdNot(exerciseRequest.getName(), id)) {
            throw new DuplicateResourceException("Exercice", "nom", exerciseRequest.getName());
        }

        Module module = moduleRepository.findById(exerciseRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", exerciseRequest.getModuleId()));

        updateExerciseFields(exerciseRequest, exercise, module);
        exerciseRepository.save(exercise);

        log.info("Exercice id : {} mis à jour avec succès", id);

        return ExerciseResponse.fromEntity(exercise);
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        log.info("Tentative de suppression de l'exercice id : {}", id);
        Exercise exercise = findOrThrow(id);
        exerciseRepository.delete(exercise);
        log.info("Exercice id : {} supprimé avec succès", id);
    }

    @Override
    @Transactional
    public ExerciseResponse toggleCompleted(Long id) {
        log.info("Bascule de l'état complété pour l'exercice id : {}", id);

        Exercise exercise = findOrThrow(id);
        exercise.setCompleted(!exercise.getCompleted());
        exerciseRepository.save(exercise);

        log.info("Exercice id : {} est désormais {}", id, exercise.getCompleted() ? "complété" : "non complété");

        return ExerciseResponse.fromEntity(exercise);
    }

    /**
     * Construit un nouvel exercice à partir d'un {@link ExerciseRequest}.
     *
     * @param exerciseRequest la requête de création de l'exercice
     * @param module le module de l'exercice
     * @return le nouvel exercice construit
     */
    private static Exercise buildNewExercise(ExerciseRequest exerciseRequest, Module module) {
        return Exercise.builder()
                .name(exerciseRequest.getName())
                .content(exerciseRequest.getContent())
                .module(module)
                .build();
    }

    /**
     * Met à jour les champs d'un exercice
     *
     * @param exerciseRequest la requête de mise à jour de l'exercice
     * @param exercise l'exercice à mettre à jour
     * @param module le module de l'exercice
     */
    private static void updateExerciseFields(ExerciseRequest exerciseRequest, Exercise exercise, Module module) {
        exercise.setName(exerciseRequest.getName());
        exercise.setContent(exerciseRequest.getContent());
        exercise.setModule(module);
    }

    /**
     * Récupère un exercice par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant de l'exercice
     * @return l'entité {@link Exercise}
     * @throws ResourceNotFoundException si aucun exercice ne correspond à l'id
     */
    private Exercise findOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice", id));
    }
}
