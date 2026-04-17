package com.restaurants.business.services;

import com.restaurants.business.dtos.RestaurantDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Restaurant;
import com.restaurants.business.repositories.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantDTO> obtenirTousLesRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public RestaurantDTO obtenirRestaurantParId(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Restaurant non trouvé avec l'id: " + id));
        return convertirEnDTO(restaurant);
    }

    public RestaurantDTO creerRestaurant(RestaurantDTO dto) {
        Restaurant restaurant = convertirEnEntite(dto);
        Restaurant sauvegarde = restaurantRepository.save(restaurant);
        return convertirEnDTO(sauvegarde);
    }

    public RestaurantDTO mettreAJourRestaurant(Long id, RestaurantDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Restaurant non trouvé avec l'id: " + id));
        restaurant.setNom(dto.getNom());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAdresse(dto.getAdresse());
        restaurant.setTelephone(dto.getTelephone());
        restaurant.setTypeCuisine(dto.getTypeCuisine());
        restaurant.setFraisLivraison(dto.getFraisLivraison());
        restaurant.setCommandeMinimum(dto.getCommandeMinimum());
        return convertirEnDTO(restaurantRepository.save(restaurant));
    }

    public void supprimerRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new BusinessException("Restaurant non trouvé avec l'id: " + id);
        }
        restaurantRepository.deleteById(id);
    }

    private RestaurantDTO convertirEnDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setNom(restaurant.getNom());
        dto.setDescription(restaurant.getDescription());
        dto.setAdresse(restaurant.getAdresse());
        dto.setTelephone(restaurant.getTelephone());
        dto.setTypeCuisine(restaurant.getTypeCuisine());
        dto.setFraisLivraison(restaurant.getFraisLivraison());
        dto.setCommandeMinimum(restaurant.getCommandeMinimum());
        return dto;
    }

    private Restaurant convertirEnEntite(RestaurantDTO dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setNom(dto.getNom());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAdresse(dto.getAdresse());
        restaurant.setTelephone(dto.getTelephone());
        restaurant.setTypeCuisine(dto.getTypeCuisine());
        restaurant.setFraisLivraison(dto.getFraisLivraison());
        restaurant.setCommandeMinimum(dto.getCommandeMinimum());
        return restaurant;
    }
}
