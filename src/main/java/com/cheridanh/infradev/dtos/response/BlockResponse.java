package com.cheridanh.infradev.dtos.response;

import com.cheridanh.infradev.entities.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockResponse {

    private Long id;
    private String name;

    /**
     * Construit un {@link BlockResponse} à partir de l'entité {@link Block}
     * sans la liste des modules
     *
     * @param block l'entité bloc source
     * @return le dto sans les modules
     */
    public static BlockResponse fromEntity(Block block) {
        return BlockResponse.builder()
                .id(block.getId())
                .name(block.getName())
                .build();
    }
}
