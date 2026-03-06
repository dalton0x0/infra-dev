package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.AssignBlocksRequest;
import com.cheridanh.infradev.dtos.request.UserUpdateRequest;
import com.cheridanh.infradev.dtos.response.UserResponse;

import java.util.List;

public interface UserService {

    /**
     * Récupère tous les utilisateurs.
     *
     * @return la liste des utilisateurs
     */
    List<UserResponse> getAllUsers();

    /**
     * Récupère un utilisateur par son identifiant avec ses blocs.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur avec ses blocs
     */
    UserResponse getUserById(Long id);

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur à modifier
     * @param request les nouvelles données
     * @return l'utilisateur mis à jour
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Bascule le rôle d'un utilisateur entre ADMIN et USER.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur avec son nouveau rôle
     */
    UserResponse toggleRole(Long id);

    /**
     * Bascule l'état actif/inactif d'un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur avec son nouvel état
     */
    UserResponse toggleEnabled(Long id);

    /**
     * Assigne une promotion à un utilisateur.
     *
     * @param userId l'identifiant de l'utilisateur
     * @param promotionId l'identifiant de la promotion
     * @return l'utilisateur mis à jour
     */
    UserResponse assignPromotion(Long userId, Long promotionId);

    /**
     * Retire la promotion d'un utilisateur.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    UserResponse removePromotion(Long userId);

    /**
     * Assigne des blocs à un utilisateur (remplace les blocs existants).
     *
     * @param userId l'identifiant de l'utilisateur
     * @param request la liste des identifiants de blocs à assigner
     * @return l'utilisateur mis à jour avec ses blocs
     */
    UserResponse assignBlocks(Long userId, AssignBlocksRequest request);

    /**
     * Supprime définitivement un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur à supprimer
     */
    void deleteUser(Long id);
}
