package com.restaurants.business.services;

import com.restaurants.business.dtos.PlatDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Plat;
import com.restaurants.business.models.Restaurant;
import com.restaurants.business.repositories.PlatRepository;
import com.restaurants.business.repositories.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des plats.
 */
@Service
@Transactional
public class PlatService {

    private final PlatRepository platRepository;
    private final RestaurantRepository restaurantRepository;

    public PlatService(PlatRepository platRepository, RestaurantRepository restaurantRepository) {
        this.platRepository = platRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Récupère tous les plats d'un restaurant.
     *
     * @param idRestaurant l'identifiant du restaurant
     * @return la liste des plats
     */
    @Transactional(readOnly = true)
    public List<PlatDTO> obtenirPlatsParRestaurant(Long idRestaurant) {
        return platRepository.findByRestaurantId(idRestaurant)
                .stream()
                .map(PlatDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un plat par son identifiant.
     *
     * @param id l'identifiant du plat
     * @return le plat trouvé
     * @throws BusinessException si le plat n'existe pas
     */
    @Transactional(readOnly = true)
    public PlatDTO obtenirPlatParId(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Plat introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));
        return PlatDTO.depuisEntite(plat);
    }

    /**
     * Crée un nouveau plat pour un restaurant.
     *
     * @param dto les données du plat
     * @return le plat créé
     * @throws BusinessException si le restaurant n'existe pas
     */
    public PlatDTO creerPlat(PlatDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getIdRestaurant())
                .orElseThrow(() -> new BusinessException(
                        "Restaurant introuvable avec l'identifiant : " + dto.getIdRestaurant(),
                        HttpStatus.NOT_FOUND
                ));

        Plat plat = Plat.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .prix(dto.getPrix())
                .categorie(dto.getCategorie())
                .imageUrl(dto.getImageUrl())
                .disponible(dto.isDisponible())
                .restaurant(restaurant)
                .build();

        Plat sauvegarde = platRepository.save(plat);
        return PlatDTO.depuisEntite(sauvegarde);
    }

    /**
     * Met à jour un plat existant.
     *
     * @param id  l'identifiant du plat
     * @param dto les nouvelles données
     * @return le plat mis à jour
     * @throws BusinessException si le plat n'existe pas
     */
    public PlatDTO mettreAJourPlat(Long id, PlatDTO dto) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Plat introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));

        if (dto.getNom() != null) plat.setNom(dto.getNom());
        if (dto.getDescription() != null) plat.setDescription(dto.getDescription());
        if (dto.getPrix() != null) plat.setPrix(dto.getPrix());
        if (dto.getCategorie() != null) plat.setCategorie(dto.getCategorie());
        if (dto.getImageUrl() != null) plat.setImageUrl(dto.getImageUrl());
        plat.setDisponible(dto.isDisponible());

        Plat sauvegarde = platRepository.save(plat);
        return PlatDTO.depuisEntite(sauvegarde);
    }

    /**
     * Supprime un plat.
     *
     * @param id l'identifiant du plat à supprimer
     * @throws BusinessException si le plat n'existe pas
     */
    public void supprimerPlat(Long id) {
        if (!platRepository.existsById(id)) {
            throw new BusinessException(
                    "Plat introuvable avec l'identifiant : " + id,
                    HttpStatus.NOT_FOUND
            );
        }
        platRepository.deleteById(id);
    }
}
