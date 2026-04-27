package com.restaurants.auth.repository;

import com.restaurants.auth.entity.UtilisateurEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends CrudRepository<UtilisateurEntity, Long> {

    Optional<UtilisateurEntity> findByCourriel(String courriel);

    boolean existsByCourriel(String courriel);
}
