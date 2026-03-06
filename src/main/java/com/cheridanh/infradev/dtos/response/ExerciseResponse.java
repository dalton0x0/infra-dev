package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Exercise;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseResponse {

    private Long id;
    private String name;
    private String content;
    private Boolean completed;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link ExerciseResponse} à partir de l'entité {@link Exercise}.
     *
     * @param exercise l'entité exercice source
     * @return le dto correspondant
     */
    public static ExerciseResponse fromEntity(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .content(exercise.getContent())
                .completed(exercise.getCompleted())
                .moduleId(exercise.getModule() != null ? exercise.getModule().getId() : null)
                .moduleName(exercise.getModule() != null ? exercise.getModule().getName() : null)
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .build();
    }
}
