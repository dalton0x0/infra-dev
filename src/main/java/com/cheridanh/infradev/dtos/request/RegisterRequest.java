package com.cheridanh.infradev.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]{2,}\\.[a-zA-Z]{2,}$")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Pattern(
            message = "Le mot de passe doit contenir au minimum " +
                    "10 caractères avec au moins " +
                    "1 lettre majuscule, " +
                    "1 lettre minuscule, " +
                    "1 chiffre et " +
                    "1 caractère spécial parmi : # $ @ ! % & * ?",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{10,}$"
    )
    private String password;
}
