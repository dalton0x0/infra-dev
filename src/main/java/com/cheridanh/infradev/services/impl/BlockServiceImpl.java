package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.BlockRequest;
import com.cheridanh.infradev.dtos.response.BlockResponse;
import com.cheridanh.infradev.entities.Block;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.BlockRepository;
import com.cheridanh.infradev.services.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BlockResponse> getAllBlocks() {
        log.debug("Récupération de tous les blocs");

        List<BlockResponse> blocs = blockRepository.findAll()
                .stream()
                .map(BlockResponse::fromEntity)
                .toList();

        log.debug("Total de blocs obtenus : {}", blocs.size());

        return blocs;
    }

    @Override
    public BlockResponse getBlockById(Long id) {
        log.debug("Récupération du bloc id : {}", id);
        Block block = findOrThrow(id);
        log.debug("Bloc récupéré : {}", block.getName());
        return BlockResponse.fromEntity(block);
    }

    @Override
    @Transactional
    public BlockResponse createBlock(BlockRequest blockRequest) {
        log.info("Création d'un nouveau bloc");

        if (blockRepository.existsByNameEqualsIgnoreCase(blockRequest.getName())) {
            throw new DuplicateResourceException("Bloc", "nom", blockRequest.getName());
        }

        Block block = buildNewBlock(blockRequest);
        blockRepository.save(block);

        log.debug("Bloc créé : {}", block.getName());

        return BlockResponse.fromEntity(block);
    }

    @Override
    @Transactional
    public BlockResponse updateBlock(Long id, BlockRequest blockRequest) {
        log.info("Mise à jour du bloc id : {}", id);

        Block block = findOrThrow(id);

        if (blockRepository.existsByNameEqualsIgnoreCaseAndIdNot(blockRequest.getName(), id)) {
            throw new DuplicateResourceException("Bloc", "nom", blockRequest.getName());
        }

        updateBlockFields(blockRequest, block);
        blockRepository.save(block);

        log.info("Bloc id {} : mise à jour", id);

        return BlockResponse.fromEntity(block);
    }

    @Override
    @Transactional
    public void deleteBlock(Long id) {
        log.info("Suppression du bloc id : {}", id);

        Block block = findOrThrow(id);
        blockRepository.delete(block);

        log.info("Bloc id : {} supprimée avec succès", id);
    }

    /**
     * Construit un nouveau bloc à partir d'un {@link BlockRequest}.
     *
     * @param blockRequest la requête de création du bloc
     * @return le nouveau bloc construit
     */
    private static Block buildNewBlock(BlockRequest blockRequest) {
        return Block.builder()
                .name(blockRequest.getName())
                .description(blockRequest.getDescription())
                .cover(blockRequest.getCover())
                .build();
    }

    /**
     * Met à jour les champs d'un bloc
     *
     * @param blockRequest la requête de mise à jour du bloc
     * @param block le bloc à mettre à jour
     */
    private static void updateBlockFields(BlockRequest blockRequest, Block block) {
        block.setName(blockRequest.getName());
        block.setDescription(blockRequest.getDescription());
        block.setCover(blockRequest.getCover());
    }

    /**
     * Récupère un bloc par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant du bloc
     * @return l'entité {@link Block}
     * @throws ResourceNotFoundException si aucun bloc ne correspond à l'id
     */
    private Block findOrThrow(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloc", id));
    }
}
