package com.restaurants.business.service;

import com.restaurants.business.dto.PlatDTO;
import com.restaurants.business.entity.PlatEntity;
import com.restaurants.business.repository.PlatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatServiceImpl implements PlatService {

    private final PlatRepository repository;

    public PlatServiceImpl(PlatRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PlatDTO> findAll() {
        List<PlatDTO> result = new ArrayList<>();
        repository.findAll().forEach(e -> result.add(toDTO(e)));
        return result;
    }

    @Override
    public List<PlatDTO> findByRestaurant(Long idRestaurant) {
        List<PlatDTO> result = new ArrayList<>();
        repository.findByIdRestaurant(idRestaurant).forEach(e -> result.add(toDTO(e)));
        return result;
    }

    @Override
    public PlatDTO findById(Long id) {
        PlatEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plat introuvable: " + id));
        return toDTO(entity);
    }

    @Override
    public PlatDTO create(PlatDTO dto) {
        return toDTO(repository.save(toEntity(dto)));
    }

    @Override
    public PlatDTO update(Long id, PlatDTO dto) {
        PlatEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plat introuvable: " + id));
        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());
        existing.setPrix(dto.getPrix());
        existing.setCategorie(dto.getCategorie());
        existing.setImageUrl(dto.getImageUrl());
        existing.setDisponible(dto.isDisponible());
        return toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plat introuvable: " + id);
        }
        repository.deleteById(id);
    }

    private PlatDTO toDTO(PlatEntity e) {
        return new PlatDTO(e.getId(), e.getNom(), e.getDescription(), e.getPrix(),
                e.getCategorie(), e.getImageUrl(), e.isDisponible(), e.getIdRestaurant());
    }

    private PlatEntity toEntity(PlatDTO dto) {
        PlatEntity e = new PlatEntity();
        e.setNom(dto.getNom());
        e.setDescription(dto.getDescription());
        e.setPrix(dto.getPrix());
        e.setCategorie(dto.getCategorie());
        e.setImageUrl(dto.getImageUrl());
        e.setDisponible(dto.isDisponible());
        e.setIdRestaurant(dto.getIdRestaurant());
        return e;
    }
}
