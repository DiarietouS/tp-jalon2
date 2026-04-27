package com.restaurants.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurants.business.dto.ArticleCommandeDTO;
import com.restaurants.business.dto.CommandeDTO;
import com.restaurants.business.entity.CommandeEntity;
import com.restaurants.business.repository.CommandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository repository;
    private final ObjectMapper objectMapper;

    public CommandeServiceImpl(CommandeRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<CommandeDTO> findAll(Long idClient, Long idRestaurant) {
        List<CommandeEntity> entities;
        if (idClient != null) {
            entities = repository.findByIdClientOrderByIdDesc(idClient);
        } else if (idRestaurant != null) {
            entities = repository.findByIdRestaurantOrderByIdDesc(idRestaurant);
        } else {
            entities = repository.findAll();
            entities.sort((a, b) -> Long.compare(b.getId(), a.getId()));
        }

        List<CommandeDTO> result = new ArrayList<>();
        for (CommandeEntity e : entities) {
            result.add(toDTO(e));
        }
        return result;
    }

    @Override
    public CommandeDTO findById(Long id) {
        CommandeEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable: " + id));
        return toDTO(entity);
    }

    @Override
    public CommandeDTO create(CommandeDTO dto) {
        CommandeEntity entity = new CommandeEntity();
        entity.setIdClient(dto.getIdClient());
        entity.setIdRestaurant(dto.getIdRestaurant());
        entity.setNomRestaurant(dto.getNomRestaurant());
        entity.setArticlesJson(serializeArticles(dto.getArticles()));
        entity.setStatut(dto.getStatut() == null || dto.getStatut().isBlank() ? "enAttente" : dto.getStatut());
        entity.setSousTotal(dto.getSousTotal() == null ? 0.0 : dto.getSousTotal());
        entity.setFraisLivraison(dto.getFraisLivraison() == null ? 0.0 : dto.getFraisLivraison());
        entity.setTotal(dto.getTotal() == null ? 0.0 : dto.getTotal());
        entity.setAdresseLivraison(dto.getAdresseLivraison());
        entity.setTelephone(dto.getTelephone());
        entity.setCreeLe(dto.getCreeLe() == null || dto.getCreeLe().isBlank() ? OffsetDateTime.now().toString() : dto.getCreeLe());
        entity.setLivraisonEstimee(dto.getLivraisonEstimee());

        return toDTO(repository.save(entity));
    }

    @Override
    public CommandeDTO updateStatut(Long id, String statut) {
        CommandeEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable: " + id));

        existing.setStatut(statut);
        return toDTO(repository.save(existing));
    }

    private CommandeDTO toDTO(CommandeEntity e) {
        CommandeDTO dto = new CommandeDTO();
        dto.setId(e.getId());
        dto.setIdClient(e.getIdClient());
        dto.setIdRestaurant(e.getIdRestaurant());
        dto.setNomRestaurant(e.getNomRestaurant());
        dto.setArticles(deserializeArticles(e.getArticlesJson()));
        dto.setStatut(e.getStatut());
        dto.setSousTotal(e.getSousTotal());
        dto.setFraisLivraison(e.getFraisLivraison());
        dto.setTotal(e.getTotal());
        dto.setAdresseLivraison(e.getAdresseLivraison());
        dto.setTelephone(e.getTelephone());
        dto.setCreeLe(e.getCreeLe());
        dto.setLivraisonEstimee(e.getLivraisonEstimee());
        return dto;
    }

    private String serializeArticles(List<ArticleCommandeDTO> articles) {
        try {
            return objectMapper.writeValueAsString(articles == null ? List.of() : articles);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Articles invalides");
        }
    }

    private List<ArticleCommandeDTO> deserializeArticles(String json) {
        try {
            if (json == null || json.isBlank()) {
                return List.of();
            }
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }
}
