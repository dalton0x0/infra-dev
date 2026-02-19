package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Construit un {@link UserSummaryResponse} à partir d'une entité {@link User}.
     *
     * @param user l'entité utilisateur source
     * @return le dto correspondant
     */
    public static UserSummaryResponse fromEntity(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
