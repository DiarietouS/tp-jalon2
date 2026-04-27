package com.restaurants.business.service;

import com.restaurants.business.dto.RestaurantDTO;

import java.util.List;

public interface RestaurantService {
    List<RestaurantDTO> findAll();
    RestaurantDTO findById(Long id);
    List<RestaurantDTO> findByProprietaire(Long idProprietaire);
    RestaurantDTO create(RestaurantDTO dto);
    RestaurantDTO update(Long id, RestaurantDTO dto);
    void delete(Long id);
}
