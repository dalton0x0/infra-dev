package com.cheridanh.infradev.services.impl;

import com.cheridanh.infradev.dtos.request.ModuleRequest;
import com.cheridanh.infradev.dtos.response.ModuleResponse;
import com.cheridanh.infradev.entities.Block;
import com.cheridanh.infradev.entities.Module;
import com.cheridanh.infradev.exceptions.DuplicateResourceException;
import com.cheridanh.infradev.exceptions.ResourceNotFoundException;
import com.cheridanh.infradev.repositories.BlockRepository;
import com.cheridanh.infradev.repositories.ModuleRepository;
import com.cheridanh.infradev.services.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final BlockRepository blockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> getAllModules() {
        log.debug("Récupération de tous les modules");

        List<ModuleResponse> modules = moduleRepository.findAll()
                .stream()
                .map(ModuleResponse::fromEntity)
                .toList();

        log.debug("Total de modules obtenus : {}", modules.size());

        return modules;
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleResponse getModuleById(Long id) {
        log.debug("Récupération du module id : {}", id);
        Module module = findOrThrow(id);
        log.debug("Module récupéré : {}", module.getName());
        return ModuleResponse.fromEntityWithDetails(module);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> getModulesByBlockId(Long blocId) {
        log.debug("Récupération des modules du bloc id : {}", blocId);

        if (!blockRepository.existsById(blocId)) {
            throw new ResourceNotFoundException("Bloc", blocId);
        }

        List<ModuleResponse> modules = moduleRepository.findByBlockId(blocId)
                .stream()
                .map(ModuleResponse::fromEntity)
                .toList();

        log.debug("Total de modules obtenus pour le bloc id {} : {}", blocId, modules.size());

        return modules;
    }

    @Override
    @Transactional
    public ModuleResponse createModule(ModuleRequest moduleRequest) {
        log.info("Tentative de création d'un module");

        if (moduleRepository.existsByNameEqualsIgnoreCase(moduleRequest.getName())) {
            throw new DuplicateResourceException("Module", "nom", moduleRequest.getName());
        }

        Block block = blockRepository.findById(moduleRequest.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Bloc", moduleRequest.getBlockId()));

        Module module = buildNewModule(moduleRequest, block);
        moduleRepository.save(module);

        log.info("Module créé avec succès, id : {}", module.getId());

        return ModuleResponse.fromEntity(module);
    }

    @Override
    @Transactional
    public ModuleResponse updateModule(Long id, ModuleRequest moduleRequest) {
        log.info("Tentative de mise à jour du module id : {}", id);

        Module module = findOrThrow(id);

        if (moduleRepository.existsByNameEqualsIgnoreCaseAndIdNot(moduleRequest.getName(), id)) {
            throw new DuplicateResourceException("Module", "nom", moduleRequest.getName());
        }

        Block block = blockRepository.findById(moduleRequest.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Bloc", moduleRequest.getBlockId()));

        updateModuleFields(moduleRequest, module, block);
        moduleRepository.save(module);

        log.info("Module id : {} mis à jour avec succès", id);

        return ModuleResponse.fromEntity(module);
    }

    @Override
    @Transactional
    public void deleteModule(Long id) {
        log.info("Tentative de suppression du module id : {}", id);
        Module module = findOrThrow(id);
        moduleRepository.delete(module);
        log.info("Module id : {} supprimé avec succès", id);
    }

    /**
     * Construit un nouveau module à partir d'un {@link ModuleRequest}.
     *
     * @param moduleRequest la requête de création du module
     * @return le nouveau module construit
     */
    private static Module buildNewModule(ModuleRequest moduleRequest, Block block) {
        return Module.builder()
                .name(moduleRequest.getName())
                .description(moduleRequest.getDescription())
                .block(block)
                .build();
    }

    /**
     * Met à jour les champs d'un module
     *
     * @param moduleRequest la requête de mise à jour du module
     * @param module le cours à mettre à jour
     * @param block le module du module
     */
    private static void updateModuleFields(ModuleRequest moduleRequest, Module module, Block block) {
        module.setName(moduleRequest.getName());
        module.setDescription(moduleRequest.getDescription());
        module.setBlock(block);
    }

    /**
     * Récupère un module par son identifiant ou lève une exception si introuvable.
     *
     * @param id l'identifiant du module
     * @return l'entité {@link Module}
     * @throws ResourceNotFoundException si aucun module ne correspond à l'id
     */
    private Module findOrThrow(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", id));
    }
}
