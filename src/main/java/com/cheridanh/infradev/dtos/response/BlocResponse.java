package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Bloc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlocResponse {

    private Long id;
    private String name;

    /**
     * Construit un {@link BlocResponse} à partir de l'entité {@link Bloc}
     * sans la liste des modules
     *
     * @param bloc l'entité bloc source
     * @return le dto sans les modules
     */
    public static BlocResponse fromEntity(Bloc bloc) {
        return BlocResponse.builder()
                .id(bloc.getId())
                .name(bloc.getName())
                .build();
    }
}
