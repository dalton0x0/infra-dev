package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Promotion;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromoResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private Long userCount;
    private List<UserSummaryResponse> users;

    /**
     * Construit un {@link PromoResponse} à partir d'une entité {@link Promotion},
     * sans la liste des utilisateurs.
     *
     * @param promotion l'entité promotion source
     * @return le dto sans le détail des utilisateurs
     */
    public static PromoResponse fromEntity(Promotion promotion) {
        return PromoResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .active(promotion.isActive())
                .userCount((long) promotion.getUsers().size())
                .build();
    }

    /**
     * Construit un {@link PromoResponse} enrichi avec la liste des utilisateurs.
     *
     * @param promotion l'entité promotion source
     * @return le dto avec le détail des utilisateurs
     */
    public static PromoResponse fromEntityWithUsers(Promotion promotion) {
        List<UserSummaryResponse> userSummaries = promotion.getUsers().stream()
                .map(UserSummaryResponse::fromEntity)
                .toList();

        return PromoResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .active(promotion.isActive())
                .userCount((long) promotion.getUsers().size())
                .users(userSummaries)
                .build();
    }
}
