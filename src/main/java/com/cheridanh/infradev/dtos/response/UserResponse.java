package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.User;
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
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String avatar;
    private Boolean enabled;
    private String promotionName;
    private Long promotionId;
    private List<BlockResponse> blocks;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link UserResponse} à partir de l'entité {@link User}
     * sans la liste des blocs.
     *
     * @param user l'entité utilisateur source
     * @return le dto sans les blocs
     */
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatar(user.getAvatar())
                .enabled(user.getEnabled())
                .promotionName(user.getPromotion() != null ? user.getPromotion().getName() : null)
                .promotionId(user.getPromotion() != null ? user.getPromotion().getId() : null)
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Construit un {@link UserResponse} enrichi avec la liste des blocs.
     *
     * @param user l'entité utilisateur source
     * @return le dto avec les blocs
     */
    public static UserResponse fromEntityWithBlocs(User user) {
        List<BlockResponse> blocResponses = user.getBlocks().stream()
                .map(BlockResponse::fromEntity)
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatar(user.getAvatar())
                .enabled(user.getEnabled())
                .promotionName(user.getPromotion() != null ? user.getPromotion().getName() : null)
                .promotionId(user.getPromotion() != null ? user.getPromotion().getId() : null)
                .blocks(blocResponses)
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
