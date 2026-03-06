package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.CourseRequest;
import com.cheridanh.infradev.dtos.response.CourseResponse;

import java.util.List;

public interface CourseService {

    /**
     * Récupère tous les cours.
     *
     * @return la liste des cours
     */
    List<CourseResponse> getAllCourses();

    /**
     * Récupère un cours par son identifiant.
     *
     * @param id l'identifiant du cours
     * @return le cours correspondant
     */
    CourseResponse getCourseById(Long id);

    /**
     * Récupère tous les cours d'un module donné.
     *
     * @param moduleId l'identifiant du module
     * @return la liste des cours du module
     */
    List<CourseResponse> getCoursesByModuleId(Long moduleId);

    /**
     * Crée un nouveau cours rattaché à un module.
     *
     * @param courseRequest les données du cours à créer
     * @return le cours créé
     */
    CourseResponse createCourse(CourseRequest courseRequest);

    /**
     * Met à jour un cours existant.
     *
     * @param id l'identifiant du cours à modifier
     * @param courseRequest les nouvelles données
     * @return le cours mis à jour
     */
    CourseResponse updateCourse(Long id, CourseRequest courseRequest);

    /**
     * Supprime définitivement un cours.
     *
     * @param id l'identifiant du cours à supprimer
     */
    void deleteCourse(Long id);

    /**
     * Bascule l'état complété/non complété d'un cours.
     *
     * @param id l'identifiant du cours
     * @return le cours avec son nouvel état
     */
    CourseResponse toggleCompleted(Long id);
}
