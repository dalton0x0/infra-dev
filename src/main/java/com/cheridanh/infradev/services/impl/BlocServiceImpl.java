package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.BlocRequest;
import com.cheridanh.infradev.dtos.response.BlocResponse;
import com.cheridanh.infradev.entities.Bloc;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.BlocRepository;
import com.cheridanh.infradev.services.BlocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlocServiceImpl implements BlocService {

    private final BlocRepository blocRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BlocResponse> getAllBlocs() {
        log.debug("Récupération de tous les blocs");

        List<BlocResponse> blocs = blocRepository.findAll()
                .stream()
                .map(BlocResponse::fromEntity)
                .toList();

        log.debug("Total de blocs obtenus : {}", blocs.size());

        return blocs;
    }

    @Override
    public BlocResponse getBlocById(Long id) {
        log.debug("Récupération du bloc id : {}", id);
        Bloc bloc = findOrThrow(id);
        log.debug("Bloc récupéré : {}", bloc.getName());
        return BlocResponse.fromEntity(bloc);
    }

    @Override
    @Transactional
    public BlocResponse createBloc(BlocRequest blocRequest) {
        log.info("Création d'un nouveau bloc");

        if (blocRepository.existsByNameEqualsIgnoreCase(blocRequest.getName())) {
            throw new DuplicateResourceException("Bloc", "nom", blocRequest.getName());
        }

        Bloc bloc = buildNewBloc(blocRequest);
        blocRepository.save(bloc);

        log.debug("Bloc créé : {}", bloc.getName());

        return BlocResponse.fromEntity(bloc);
    }

    @Override
    @Transactional
    public BlocResponse updateBloc(Long id, BlocRequest blocRequest) {
        log.info("Mise à jour du bloc id : {}", id);

        Bloc bloc = findOrThrow(id);

        if (blocRepository.existsByNameEqualsIgnoreCaseAndIdNot(blocRequest.getName(), id)) {
            throw new DuplicateResourceException("Bloc", "nom", blocRequest.getName());
        }

        updateBlocFields(blocRequest, bloc);
        blocRepository.save(bloc);

        log.info("Bloc id {} : mise à jour", id);

        return BlocResponse.fromEntity(bloc);
    }

    @Override
    @Transactional
    public void deleteBloc(Long id) {
        log.info("Suppression du bloc id : {}", id);

        Bloc bloc = findOrThrow(id);
        blocRepository.delete(bloc);

        log.info("Bloc id : {} supprimée avec succès", id);
    }

    /**
     * Construit un nouveau bloc à partir d'un {@link BlocRequest}.
     *
     * @param blocRequest la requête de création du bloc
     * @return le nouveau bloc construit
     */
    private static Bloc buildNewBloc(BlocRequest blocRequest) {
        return Bloc.builder()
                .name(blocRequest.getName())
                .description(blocRequest.getDescription())
                .cover(blocRequest.getCover())
                .build();
    }

    /**
     * Met à jour les champs d'un bloc
     *
     * @param blocRequest la requête de mise à jour du bloc
     * @param bloc le bloc à mettre à jour
     */
    private static void updateBlocFields(BlocRequest blocRequest, Bloc bloc) {
        bloc.setName(blocRequest.getName());
        bloc.setDescription(blocRequest.getDescription());
        bloc.setCover(blocRequest.getCover());
    }

    /**
     * Récupère un bloc par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant du bloc
     * @return l'entité {@link Bloc}
     * @throws ResourceNotFoundException si aucun bloc ne correspond à l'id
     */
    private Bloc findOrThrow(Long id) {
        return blocRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloc", id));
    }
}
