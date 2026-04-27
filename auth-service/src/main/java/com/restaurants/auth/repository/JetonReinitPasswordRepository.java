package com.restaurants.auth.repository;

import com.restaurants.auth.entity.JetonReinitPassword;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JetonReinitPasswordRepository extends CrudRepository<JetonReinitPassword, Long> {
    Optional<JetonReinitPassword> findByJeton(String jeton);
    void deleteByCourriel(String courriel);
}
