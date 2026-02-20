package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.PromoRequest;
import com.cheridanh.infradev.dtos.response.PromoResponse;
import com.cheridanh.infradev.entities.Promotion;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.PromotionNotFoundException;
import com.cheridanh.infradev.repositories.PromotionRepository;
import com.cheridanh.infradev.services.PromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromoServiceImpl implements PromoService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PromoResponse> getAllPromos() {
        log.debug("Récupération de toutes les promotions");

        List<PromoResponse> promotions = promotionRepository.findAll()
                .stream()
                .map(PromoResponse::fromEntity)
                .sorted(Comparator.comparing(PromoResponse::getStartDate))
                .toList();

        log.debug("Total de promotions obtenues : {}", promotions.size());

        return promotions;
    }

    @Override
    @Transactional(readOnly = true)
    public PromoResponse getPromoById(Long id) {
        log.info("Récupération de la promotion id : {}", id);
        Promotion promotion = findOrThrow(id);
        return PromoResponse.fromEntityWithUsers(promotion);
    }

    @Override
    @Transactional
    public PromoResponse createPromo(PromoRequest promoRequest) {
        log.info("Tentative de création d'une promotion");

        if (promotionRepository.existsByNameEqualsIgnoreCase(promoRequest.getName())) {
            throw new DuplicateResourceException("Promotion", "nom", promoRequest.getName());
        }

        Promotion promotion = buildNewPromo(promoRequest);
        promotionRepository.save(promotion);

        log.info("Promotion créée avec succès, id : {}", promotion.getId());

        return PromoResponse.fromEntity(promotion);
    }

    @Override
    @Transactional
    public PromoResponse updatePromo(Long id, PromoRequest promoRequest) {
        log.info("Tentative de mise à jour de la promotion id : {}", id);

        Promotion promotion = findOrThrow(id);

        if (promotionRepository.existsByNameEqualsIgnoreCaseAndIdNot(promoRequest.getName(), id)) {
            throw new DuplicateResourceException("Promotion", "nom", promoRequest.getName());
        }

        updatePromotionFields(promoRequest, promotion);
        promotionRepository.save(promotion);

        log.info("Promotion id : {} mise à jour avec succès", id);

        return PromoResponse.fromEntityWithUsers(promotion);
    }

    @Override
    @Transactional
    public void deletePromo(Long id) {
        log.info("Tentative de suppression de la promotion id : {}", id);

        Promotion promotion = findOrThrow(id);
        promotionRepository.delete(promotion);

        log.info("Promotion id : {} supprimée avec succès", id);
    }

    @Override
    @Transactional
    public PromoResponse toggleActive(Long id) {
        log.info("Bascule de l'état actif/inactif pour la promotion id : {}", id);

        Promotion promotion = findOrThrow(id);
        promotion.setActive(!promotion.isActive());
        promotionRepository.save(promotion);

        log.info("Promotion id : {} est désormais {}", id, promotion.isActive() ? "active" : "inactive");

        return PromoResponse.fromEntity(promotion);
    }

    /**
     * Construit une nouvelle promotion à partir d'un {@link PromoRequest}.
     *
     * @param promoRequest la requête de création de promotion
     * @return la nouvelle promotion construite
     */
    private static Promotion buildNewPromo(PromoRequest promoRequest) {
        return Promotion.builder()
                .name(promoRequest.getName())
                .startDate(promoRequest.getStartDate())
                .endDate(promoRequest.getEndDate())
                .build();
    }

    /**
     * Met à jour les champs d'une promotion
     *
     * @param promoRequest la requête de mise à jour de promotion
     * @param promotion la promotion à mettre à jour
     */
    private static void updatePromotionFields(PromoRequest promoRequest, Promotion promotion) {
        promotion.setName(promoRequest.getName());
        promotion.setStartDate(promoRequest.getStartDate());
        promotion.setEndDate(promoRequest.getEndDate());
    }

    /**
     * Récupère une promotion par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant de la promotion
     * @return l'entité {@link Promotion}
     * @throws PromotionNotFoundException si aucune promotion ne correspond à l'id
     */
    private Promotion findOrThrow(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException(id));
    }
}
