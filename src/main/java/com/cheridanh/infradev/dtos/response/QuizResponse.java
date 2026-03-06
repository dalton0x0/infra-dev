package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Quiz;
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
public class QuizResponse {

    private Long id;
    private String name;
    private String content;
    private Boolean completed;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link QuizResponse} à partir de l'entité {@link Quiz}.
     *
     * @param quiz l'entité quiz source
     * @return le dto correspondant
     */
    public static QuizResponse fromEntity(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .content(quiz.getContent())
                .completed(quiz.getCompleted())
                .moduleId(quiz.getModule() != null ? quiz.getModule().getId() : null)
                .moduleName(quiz.getModule() != null ? quiz.getModule().getName() : null)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }
}
