package com.restaurants.business.service;

import com.restaurants.business.dto.RestaurantDTO;
import com.restaurants.business.entity.RestaurantEntity;
import com.restaurants.business.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantServiceImpl(RestaurantRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RestaurantDTO> findAll() {
        List<RestaurantDTO> result = new ArrayList<>();
        repository.findAll().forEach(e -> result.add(toDTO(e)));
        return result;
    }

    @Override
    public RestaurantDTO findById(Long id) {
        RestaurantEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant introuvable: " + id));
        return toDTO(entity);
    }

    @Override
    public List<RestaurantDTO> findByProprietaire(Long idProprietaire) {
        List<RestaurantDTO> result = new ArrayList<>();
        repository.findByIdProprietaire(idProprietaire).forEach(e -> result.add(toDTO(e)));
        return result;
    }

    @Override
    public RestaurantDTO create(RestaurantDTO dto) {
        RestaurantEntity entity = toEntity(dto);
        return toDTO(repository.save(entity));
    }

    @Override
    public RestaurantDTO update(Long id, RestaurantDTO dto) {
        RestaurantEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant introuvable: " + id));
        existing.setNom(dto.getNom());
        existing.setAdresse(dto.getAdresse());
        existing.setLocalisationTexte(dto.getLocalisationTexte());
        existing.setTelephone(dto.getTelephone());
        existing.setCourriel(dto.getCourriel());
        existing.setImageUrl(dto.getImageUrl());
        existing.setTypeCuisine(dto.getTypeCuisine());
        existing.setNote(dto.getNote());
        existing.setTempsLivraison(dto.getTempsLivraison());
        existing.setFraisLivraison(dto.getFraisLivraison());
        existing.setCommandeMinimum(dto.getCommandeMinimum());
        return toDTO(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant introuvable: " + id);
        }
        repository.deleteById(id);
    }

    private RestaurantDTO toDTO(RestaurantEntity e) {
        return new RestaurantDTO(e.getId(), e.getIdProprietaire(), e.getNom(), e.getAdresse(),
                e.getLocalisationTexte(), e.getTelephone(), e.getCourriel(), e.getImageUrl(),
                e.getTypeCuisine(), e.getNote(), e.getTempsLivraison(), e.getFraisLivraison(),
                e.getCommandeMinimum());
    }

    private RestaurantEntity toEntity(RestaurantDTO dto) {
        RestaurantEntity e = new RestaurantEntity();
        e.setIdProprietaire(dto.getIdProprietaire());
        e.setNom(dto.getNom());
        e.setAdresse(dto.getAdresse());
        e.setLocalisationTexte(dto.getLocalisationTexte());
        e.setTelephone(dto.getTelephone());
        e.setCourriel(dto.getCourriel());
        e.setImageUrl(dto.getImageUrl());
        e.setTypeCuisine(dto.getTypeCuisine());
        e.setNote(dto.getNote() != null ? dto.getNote() : 0.0);
        e.setTempsLivraison(dto.getTempsLivraison());
        e.setFraisLivraison(dto.getFraisLivraison() != null ? dto.getFraisLivraison() : 0.0);
        e.setCommandeMinimum(dto.getCommandeMinimum() != null ? dto.getCommandeMinimum() : 0.0);
        return e;
    }
}
