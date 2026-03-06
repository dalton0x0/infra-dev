package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.QuizRequest;
import com.cheridanh.infradev.dtos.response.QuizResponse;

import java.util.List;

public interface QuizService {

    /**
     * Récupère tous les quiz.
     *
     * @return la liste des quiz
     */
    List<QuizResponse> getAllQuizzes();

    /**
     * Récupère un quiz par son identifiant.
     *
     * @param id l'identifiant du quiz
     * @return le quiz correspondant
     */
    QuizResponse getQuizById(Long id);

    /**
     * Crée un nouveau quiz rattaché à un module (un seul quiz par module).
     *
     * @param quizRequest les données du quiz à créer
     * @return le quiz créé
     */
    QuizResponse createQuiz(QuizRequest quizRequest);

    /**
     * Met à jour un quiz existant.
     *
     * @param id l'identifiant du quiz à modifier
     * @param quizRequest les nouvelles données
     * @return le quiz mis à jour
     */
    QuizResponse updateQuiz(Long id, QuizRequest quizRequest);

    /**
     * Supprime définitivement un quiz.
     *
     * @param id l'identifiant du quiz à supprimer
     */
    void deleteQuiz(Long id);

    /**
     * Bascule l'état complété/non complété d'un quiz.
     *
     * @param id l'identifiant du quiz
     * @return le quiz avec son nouvel état
     */
    QuizResponse toggleCompleted(Long id);
}
