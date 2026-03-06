package com.cheridanh.infradev.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlocRequest {

    @NotBlank(message = "Le nom du bloc est obligatoire")
    private String name;

    private String description;

    private String cover;
}
