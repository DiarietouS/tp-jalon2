package com.restaurants.business.services;

import com.restaurants.business.dtos.PlatDTO;
import com.restaurants.business.exceptions.BusinessException;
import com.restaurants.business.models.Plat;
import com.restaurants.business.models.Restaurant;
import com.restaurants.business.repositories.PlatRepository;
import com.restaurants.business.repositories.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlatService {

    private final PlatRepository platRepository;
    private final RestaurantRepository restaurantRepository;

    public PlatService(PlatRepository platRepository, RestaurantRepository restaurantRepository) {
        this.platRepository = platRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<PlatDTO> obtenirTousLesPlats() {
        return platRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public List<PlatDTO> obtenirPlatsParRestaurant(Long restaurantId) {
        return platRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    public PlatDTO obtenirPlatParId(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plat non trouvé avec l'id: " + id));
        return convertirEnDTO(plat);
    }

    public PlatDTO creerPlat(PlatDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new BusinessException("Restaurant non trouvé avec l'id: " + dto.getRestaurantId()));
        Plat plat = convertirEnEntite(dto, restaurant);
        return convertirEnDTO(platRepository.save(plat));
    }

    public PlatDTO mettreAJourPlat(Long id, PlatDTO dto) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plat non trouvé avec l'id: " + id));
        plat.setNom(dto.getNom());
        plat.setDescription(dto.getDescription());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());
        if (dto.getRestaurantId() != null && !dto.getRestaurantId().equals(plat.getRestaurant().getId())) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new BusinessException("Restaurant non trouvé"));
            plat.setRestaurant(restaurant);
        }
        return convertirEnDTO(platRepository.save(plat));
    }

    public void supprimerPlat(Long id) {
        if (!platRepository.existsById(id)) {
            throw new BusinessException("Plat non trouvé avec l'id: " + id);
        }
        platRepository.deleteById(id);
    }

    private PlatDTO convertirEnDTO(Plat plat) {
        PlatDTO dto = new PlatDTO();
        dto.setId(plat.getId());
        dto.setNom(plat.getNom());
        dto.setDescription(plat.getDescription());
        dto.setPrix(plat.getPrix());
        dto.setDisponible(plat.isDisponible());
        dto.setRestaurantId(plat.getRestaurant().getId());
        return dto;
    }

    private Plat convertirEnEntite(PlatDTO dto, Restaurant restaurant) {
        Plat plat = new Plat();
        plat.setNom(dto.getNom());
        plat.setDescription(dto.getDescription());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());
        plat.setRestaurant(restaurant);
        return plat;
    }
}
