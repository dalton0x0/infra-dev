package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.AssignBlocksRequest;
import com.cheridanh.infradev.dtos.request.UserUpdateRequest;
import com.cheridanh.infradev.dtos.response.UserResponse;
import com.cheridanh.infradev.entities.Block;
import com.cheridanh.infradev.entities.Promotion;
import com.cheridanh.infradev.entities.Role;
import com.cheridanh.infradev.entities.User;
import com.cheridanh.infradev.exceptions.EmailAlreadyExistsException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.BlockRepository;
import com.cheridanh.infradev.repositories.PromotionRepository;
import com.cheridanh.infradev.repositories.UserRepository;
import com.cheridanh.infradev.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final BlockRepository blockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Récupération de tous les utilisateurs");

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();

        log.debug("Total d'utilisateurs obtenus : {}", users.size());

        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Récupération de l'utilisateur id : {}", id);
        User user = findOrThrow(id);
        log.debug("Utilisateur récupéré : {}", user.getEmail());
        return UserResponse.fromEntityWithBlocs(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Tentative de mise à jour de l'utilisateur id : {}", id);

        User user = findOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        updateUserFields(request, user);
        userRepository.save(user);

        log.info("Utilisateur id : {} mis à jour avec succès", id);

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse toggleRole(Long id) {
        log.info("Bascule du rôle pour l'utilisateur id : {}", id);

        User user = findOrThrow(id);
        user.setRole(user.getRole() == Role.ADMIN ? Role.USER : Role.ADMIN);
        userRepository.save(user);

        log.info("Utilisateur id : {} a désormais le rôle {}", id, user.getRole().name());

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse toggleEnabled(Long id) {
        log.info("Bascule de l'état actif pour l'utilisateur id : {}", id);

        User user = findOrThrow(id);
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);

        log.info("Utilisateur id : {} est désormais {}", id, user.getEnabled() ? "activé" : "désactivé");

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse assignPromotion(Long userId, Long promotionId) {
        log.info("Assignation de la promotion id {} à l'utilisateur id {}", promotionId, userId);

        User user = findOrThrow(userId);
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", promotionId));

        user.setPromotion(promotion);
        userRepository.save(user);

        log.info("Promotion '{}' assignée à l'utilisateur id : {}", promotion.getName(), userId);

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse removePromotion(Long userId) {
        log.info("Retrait de la promotion de l'utilisateur id : {}", userId);

        User user = findOrThrow(userId);
        user.setPromotion(null);
        userRepository.save(user);

        log.info("Promotion retirée de l'utilisateur id : {}", userId);

        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse assignBlocks(Long userId, AssignBlocksRequest request) {
        log.info("Assignation de blocs à l'utilisateur id : {}", userId);

        User user = findOrThrow(userId);

        List<Block> blocs = blockRepository.findAllById(request.getBlockIds());

        if (blocs.size() != request.getBlockIds().size()) {
            log.warn("Certains blocs demandés n'existent pas. Demandés : {}, trouvés : {}",
                    request.getBlockIds().size(), blocs.size());
            throw new ResourceNotFoundException("Bloc", "identifiants",
                    "Un ou plusieurs blocs demandés sont introuvables");
        }

        user.setBlocks(blocs);
        userRepository.save(user);

        log.info("{} bloc(s) assigné(s) à l'utilisateur id : {}", blocs.size(), userId);

        return UserResponse.fromEntityWithBlocs(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Tentative de suppression de l'utilisateur id : {}", id);
        User user = findOrThrow(id);
        userRepository.delete(user);
        log.info("Utilisateur id : {} supprimé avec succès", id);
    }

    /**
     * Met à jour les champs d'un utilisateur
     *
     * @param request la requête de mise à jour de l'utilisateur
     * @param user l'utilisateur à mettre à jour
     */
    private static void updateUserFields(UserUpdateRequest request, User user) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
    }

    /**
     * Récupère un utilisateur par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'entité {@link User}
     * @throws ResourceNotFoundException si aucun utilisateur ne correspond à l'id
     */
    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
    }
}
