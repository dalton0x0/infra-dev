package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.QuizRequest;
import com.cheridanh.infradev.dtos.response.QuizResponse;
import com.cheridanh.infradev.entities.Module;
import com.cheridanh.infradev.entities.Quiz;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.ModuleRepository;
import com.cheridanh.infradev.repositories.QuizRepository;
import com.cheridanh.infradev.services.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getAllQuizzes() {
        log.debug("Récupération de tous les quiz");

        List<QuizResponse> quizzes = quizRepository.findAll()
                .stream()
                .map(QuizResponse::fromEntity)
                .toList();

        log.debug("Total de quiz obtenus : {}", quizzes.size());

        return quizzes;
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        log.debug("Récupération du quiz id : {}", id);
        Quiz quiz = findOrThrow(id);
        log.debug("Quiz récupéré : {}", quiz.getName());
        return QuizResponse.fromEntity(quiz);
    }

    @Override
    @Transactional
    public QuizResponse createQuiz(QuizRequest quizRequest) {
        log.info("Tentative de création d'un quiz");

        if (quizRepository.existsByNameEqualsIgnoreCase(quizRequest.getName())) {
            throw new DuplicateResourceException("Quiz", "nom", quizRequest.getName());
        }

        Module module = moduleRepository.findById(quizRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", quizRequest.getModuleId()));

        if (module.getQuiz() != null) {
            throw new DuplicateResourceException("Quiz", "module",
                    "Le module '" + module.getName() + "' possède déjà un quiz");
        }

        Quiz quiz = buildNewQuiz(quizRequest);
        quizRepository.save(quiz);
        module.setQuiz(quiz);
        moduleRepository.save(module);

        log.info("Quiz créé avec succès, id : {}", quiz.getId());

        return QuizResponse.fromEntity(quiz);
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long id, QuizRequest quizRequest) {
        log.info("Tentative de mise à jour du quiz id : {}", id);

        Quiz quiz = findOrThrow(id);

        if (quizRepository.existsByNameEqualsIgnoreCaseAndIdNot(quizRequest.getName(), id)) {
            throw new DuplicateResourceException("Quiz", "nom", quizRequest.getName());
        }

        Module newModule = moduleRepository.findById(quizRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", quizRequest.getModuleId()));

        if (quiz.getModule() != null && !quiz.getModule().getId().equals(newModule.getId())) {
            if (newModule.getQuiz() != null) {
                throw new DuplicateResourceException("Quiz", "module",
                        "Le module '" + newModule.getName() + "' possède déjà un quiz");
            }

            Module oldModule = quiz.getModule();
            oldModule.setQuiz(null);
            moduleRepository.save(oldModule);

            newModule.setQuiz(quiz);
            moduleRepository.save(newModule);
        }

        quiz.setName(quizRequest.getName());
        quiz.setContent(quizRequest.getContent());
        quizRepository.save(quiz);

        log.info("Quiz id : {} mis à jour avec succès", id);

        return QuizResponse.fromEntity(quiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        log.info("Tentative de suppression du quiz id : {}", id);

        Quiz quiz = findOrThrow(id);

        // Détacher le quiz du module avant la suppression
        if (quiz.getModule() != null) {
            Module module = quiz.getModule();
            module.setQuiz(null);
            moduleRepository.save(module);
        }

        quizRepository.delete(quiz);

        log.info("Quiz id : {} supprimé avec succès", id);
    }

    @Override
    @Transactional
    public QuizResponse toggleCompleted(Long id) {
        log.info("Bascule de l'état complété pour le quiz id : {}", id);

        Quiz quiz = findOrThrow(id);
        quiz.setCompleted(!quiz.getCompleted());
        quizRepository.save(quiz);

        log.info("Quiz id : {} est désormais {}", id, quiz.getCompleted() ? "complété" : "non complété");

        return QuizResponse.fromEntity(quiz);
    }

    /**
     * Construit un nouveau quiz à partir d'un {@link QuizRequest}.
     *
     * @param quizRequest la requête de création du quiz
     * @return le nouveau quiz construit
     */
    private static Quiz buildNewQuiz(QuizRequest quizRequest) {
        return Quiz.builder()
                .name(quizRequest.getName())
                .content(quizRequest.getContent())
                .build();
    }

    /**
     * Récupère un quiz par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant du quiz
     * @return l'entité {@link Quiz}
     * @throws ResourceNotFoundException si aucun quiz ne correspond à l'id
     */
    private Quiz findOrThrow(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }
}
