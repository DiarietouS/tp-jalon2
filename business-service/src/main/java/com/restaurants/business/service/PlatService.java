package com.restaurants.business.service;

import com.restaurants.business.dto.PlatDTO;

import java.util.List;

public interface PlatService {
    List<PlatDTO> findAll();
    List<PlatDTO> findByRestaurant(Long idRestaurant);
    PlatDTO findById(Long id);
    PlatDTO create(PlatDTO dto);
    PlatDTO update(Long id, PlatDTO dto);
    void delete(Long id);
}
