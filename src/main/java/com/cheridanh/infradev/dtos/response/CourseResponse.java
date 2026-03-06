package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Course;
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
public class CourseResponse {

    private Long id;
    private String name;
    private String description;
    private String videoUrl;
    private String content;
    private Boolean completed;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link CourseResponse} à partir de l'entité {@link Course}.
     *
     * @param course l'entité cours source
     * @return le dto correspondant
     */
    public static CourseResponse fromEntity(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .videoUrl(course.getVideoUrl())
                .content(course.getContent())
                .completed(course.getCompleted())
                .moduleId(course.getModule() != null ? course.getModule().getId() : null)
                .moduleName(course.getModule() != null ? course.getModule().getName() : null)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
