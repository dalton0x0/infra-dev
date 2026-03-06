package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.CourseRequest;
import com.cheridanh.infradev.dtos.response.CourseResponse;
import com.cheridanh.infradev.entities.Course;
import com.cheridanh.infradev.entities.Module;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.CourseRepository;
import com.cheridanh.infradev.repositories.ModuleRepository;
import com.cheridanh.infradev.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        log.debug("Récupération de tous les cours");

        List<CourseResponse> courses = courseRepository.findAll()
                .stream()
                .map(CourseResponse::fromEntity)
                .toList();

        log.debug("Total de cours obtenus : {}", courses.size());

        return courses;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        log.debug("Récupération du cours id : {}", id);
        Course course = findOrThrow(id);
        log.debug("Cours récupéré : {}", course.getName());
        return CourseResponse.fromEntity(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByModuleId(Long moduleId) {
        log.debug("Récupération des cours du module id : {}", moduleId);

        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module", moduleId);
        }

        List<CourseResponse> courses = courseRepository.findByModuleId(moduleId)
                .stream()
                .map(CourseResponse::fromEntity)
                .toList();

        log.debug("Total de cours obtenus pour le module id {} : {}", moduleId, courses.size());

        return courses;
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest courseRequest) {
        log.info("Tentative de création d'un cours");

        if (courseRepository.existsByNameEqualsIgnoreCase(courseRequest.getName())) {
            throw new DuplicateResourceException("Cours", "nom", courseRequest.getName());
        }

        Module module = moduleRepository.findById(courseRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", courseRequest.getModuleId()));

        Course course = buildNewCourse(courseRequest, module);
        courseRepository.save(course);

        log.info("Cours créé avec succès, id : {}", course.getId());

        return CourseResponse.fromEntity(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest courseRequest) {
        log.info("Tentative de mise à jour du cours id : {}", id);

        Course course = findOrThrow(id);

        if (courseRepository.existsByNameEqualsIgnoreCaseAndIdNot(courseRequest.getName(), id)) {
            throw new DuplicateResourceException("Cours", "nom", courseRequest.getName());
        }

        Module module = moduleRepository.findById(courseRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", courseRequest.getModuleId()));

        updateCourseFields(courseRequest, course, module);
        courseRepository.save(course);

        log.info("Cours id : {} mis à jour avec succès", id);

        return CourseResponse.fromEntity(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        log.info("Tentative de suppression du cours id : {}", id);
        Course course = findOrThrow(id);
        courseRepository.delete(course);
        log.info("Cours id : {} supprimé avec succès", id);
    }

    @Override
    @Transactional
    public CourseResponse toggleCompleted(Long id) {
        log.info("Bascule de l'état complété pour le cours id : {}", id);

        Course course = findOrThrow(id);
        course.setCompleted(!course.getCompleted());
        courseRepository.save(course);

        log.info("Cours id : {} est désormais {}", id, course.getCompleted() ? "complété" : "non complété");

        return CourseResponse.fromEntity(course);
    }

    /**
     * Construit un nouveau cours à partir d'un {@link CourseRequest}.
     *
     * @param courseRequest la requête de création du cours
     * @param module le module du cours
     * @return le nouveau cours construit
     */
    private static Course buildNewCourse(CourseRequest courseRequest, Module module) {
        return Course.builder()
                .name(courseRequest.getName())
                .description(courseRequest.getDescription())
                .videoUrl(courseRequest.getVideoUrl())
                .content(courseRequest.getContent())
                .module(module)
                .build();
    }

    /**
     * Met à jour les champs d'un cours
     *
     * @param courseRequest la requête de mise à jour du cours
     * @param course le cours à mettre à jour
     * @param module le module du cours
     */
    private static void updateCourseFields(CourseRequest courseRequest, Course course, Module module) {
        course.setName(courseRequest.getName());
        course.setDescription(courseRequest.getDescription());
        course.setVideoUrl(courseRequest.getVideoUrl());
        course.setContent(courseRequest.getContent());
        course.setModule(module);
    }

    /**
     * Récupère un cours par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant du cours
     * @return l'entité {@link Course}
     * @throws ResourceNotFoundException si aucun cours ne correspond à l'id
     */
    private Course findOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours", id));
    }
}
