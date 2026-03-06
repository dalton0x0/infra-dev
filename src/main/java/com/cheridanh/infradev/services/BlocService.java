package com.cheridanh.infradev.services;

import com.cheridanh.infradev.dtos.request.BlocRequest;
import com.cheridanh.infradev.dtos.response.BlocResponse;

import java.util.List;

public interface BlocService {

    /**
     * Récupère tous les blocs
     *
     * @return la liste des blocs
     */
    List<BlocResponse> getAllBlocs();

    /**
     * Récupère un bloc par son identifiant, avec la liste de ses modules.
     *
     * @param id l'identifiant du bloc
     * @return le bloc avec ses modules
     */
    BlocResponse getBlocById(Long id);

    /**
     * Crée un nouveau bloc.
     *
     * @param blocRequest les données du bloc à créer
     * @return le bloc créé
     */
    BlocResponse createBloc(BlocRequest blocRequest);

    /**
     * Met à jour un bloc existant.
     *
     * @param id l'identifiant du bloc à modifier
     * @param blocRequest les nouvelles données
     * @return le bloc mise à jour
     */
    BlocResponse updateBloc(Long id, BlocRequest blocRequest);

    /**
     * Supprime définitivement un bloc.
     *
     * @param id l'identifiant du bloc à supprimer
     */
    void deleteBloc(Long id);
}
