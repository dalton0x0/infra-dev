package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Module;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleResponse {

    private Long id;
    private String name;
    private String description;
    private Long blockId;
    private String blockName;
    private Long courseCount;
    private Long exerciseCount;
    private Boolean hasQuiz;
    private List<CourseResponse> courses;
    private List<ExerciseResponse> exercises;
    private QuizResponse quiz;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link ModuleResponse} à partir de l'entité {@link Module}
     * sans les listes de cours, exercices et quiz.
     *
     * @param module l'entité module source
     * @return le dto sans les détails imbriqués
     */
    public static ModuleResponse fromEntity(Module module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .courseCount((long) module.getCourses().size())
                .exerciseCount((long) module.getExercises().size())
                .hasQuiz(module.getQuiz() != null)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }

    /**
     * Construit un {@link ModuleResponse} enrichi avec les cours, exercices et quiz.
     *
     * @param module l'entité module source
     * @return le dto avec les détails imbriqués
     */
    public static ModuleResponse fromEntityWithDetails(Module module) {
        List<CourseResponse> courseResponses = module.getCourses().stream()
                .map(CourseResponse::fromEntity)
                .toList();

        List<ExerciseResponse> exerciseResponses = module.getExercises().stream()
                .map(ExerciseResponse::fromEntity)
                .toList();

        QuizResponse quizResponse = module.getQuiz() != null
                ? QuizResponse.fromEntity(module.getQuiz())
                : null;

        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .courseCount((long) module.getCourses().size())
                .exerciseCount((long) module.getExercises().size())
                .hasQuiz(module.getQuiz() != null)
                .courses(courseResponses)
                .exercises(exerciseResponses)
                .quiz(quizResponse)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }
}
