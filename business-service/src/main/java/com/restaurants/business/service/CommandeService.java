package com.restaurants.business.service;

import com.restaurants.business.dto.CommandeDTO;

import java.util.List;

public interface CommandeService {
    List<CommandeDTO> findAll(Long idClient, Long idRestaurant);
    CommandeDTO findById(Long id);
    CommandeDTO create(CommandeDTO dto);
    CommandeDTO updateStatut(Long id, String statut);
}
