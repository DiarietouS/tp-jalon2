package com.restaurants.business.services;

import com.restaurants.business.dtos.RestaurantDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Restaurant;
import com.restaurants.business.repositories.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des restaurants.
 *
 * COURS INF1013 - Architecture :
 * "Controller → Service → Repository"
 */
@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Récupère tous les restaurants.
     *
     * @return la liste de tous les restaurants
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> obtenirTousLesRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(RestaurantDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un restaurant par son identifiant.
     *
     * @param id l'identifiant du restaurant
     * @return le restaurant trouvé
     * @throws BusinessException si le restaurant n'existe pas
     */
    @Transactional(readOnly = true)
    public RestaurantDTO obtenirRestaurantParId(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Restaurant introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));
        return RestaurantDTO.depuisEntite(restaurant);
    }

    /**
     * Récupère les restaurants d'un propriétaire.
     *
     * @param idProprietaire l'identifiant du propriétaire
     * @return la liste des restaurants
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> obtenirRestaurantsParProprietaire(Long idProprietaire) {
        return restaurantRepository.findByIdProprietaire(idProprietaire)
                .stream()
                .map(RestaurantDTO::depuisEntite)
                .collect(Collectors.toList());
    }

    /**
     * Crée un nouveau restaurant.
     *
     * @param dto les données du restaurant
     * @return le restaurant créé
     */
    public RestaurantDTO creerRestaurant(RestaurantDTO dto) {
        Restaurant restaurant = Restaurant.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .adresse(dto.getAdresse())
                .localisationTexte(dto.getLocalisationTexte())
                .telephone(dto.getTelephone())
                .courriel(dto.getCourriel())
                .imageUrl(dto.getImageUrl())
                .typeCuisine(dto.getTypeCuisine())
                .note(dto.getNote() != null ? dto.getNote() : 0.0)
                .tempsLivraison(dto.getTempsLivraison())
                .fraisLivraison(dto.getFraisLivraison() != null ? dto.getFraisLivraison() : 0.0)
                .commandeMinimum(dto.getCommandeMinimum() != null ? dto.getCommandeMinimum() : 0.0)
                .idProprietaire(dto.getIdProprietaire())
                .build();

        Restaurant sauvegarde = restaurantRepository.save(restaurant);
        return RestaurantDTO.depuisEntite(sauvegarde);
    }

    /**
     * Met à jour un restaurant existant.
     *
     * @param id  l'identifiant du restaurant
     * @param dto les nouvelles données
     * @return le restaurant mis à jour
     * @throws BusinessException si le restaurant n'existe pas
     */
    public RestaurantDTO mettreAJourRestaurant(Long id, RestaurantDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Restaurant introuvable avec l'identifiant : " + id,
                        HttpStatus.NOT_FOUND
                ));

        // Mettre à jour les champs
        if (dto.getNom() != null) restaurant.setNom(dto.getNom());
        if (dto.getDescription() != null) restaurant.setDescription(dto.getDescription());
        if (dto.getAdresse() != null) restaurant.setAdresse(dto.getAdresse());
        if (dto.getLocalisationTexte() != null) restaurant.setLocalisationTexte(dto.getLocalisationTexte());
        if (dto.getTelephone() != null) restaurant.setTelephone(dto.getTelephone());
        if (dto.getCourriel() != null) restaurant.setCourriel(dto.getCourriel());
        if (dto.getImageUrl() != null) restaurant.setImageUrl(dto.getImageUrl());
        if (dto.getTypeCuisine() != null) restaurant.setTypeCuisine(dto.getTypeCuisine());
        if (dto.getNote() != null) restaurant.setNote(dto.getNote());
        if (dto.getTempsLivraison() != null) restaurant.setTempsLivraison(dto.getTempsLivraison());
        if (dto.getFraisLivraison() != null) restaurant.setFraisLivraison(dto.getFraisLivraison());
        if (dto.getCommandeMinimum() != null) restaurant.setCommandeMinimum(dto.getCommandeMinimum());

        Restaurant sauvegarde = restaurantRepository.save(restaurant);
        return RestaurantDTO.depuisEntite(sauvegarde);
    }

    /**
     * Supprime un restaurant.
     *
     * @param id l'identifiant du restaurant à supprimer
     * @throws BusinessException si le restaurant n'existe pas
     */
    public void supprimerRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new BusinessException(
                    "Restaurant introuvable avec l'identifiant : " + id,
                    HttpStatus.NOT_FOUND
            );
        }
        restaurantRepository.deleteById(id);
    }
}
