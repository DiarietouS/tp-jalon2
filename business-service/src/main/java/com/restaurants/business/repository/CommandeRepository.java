package com.restaurants.business.repository;

import com.restaurants.business.entity.CommandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<CommandeEntity, Long> {
    List<CommandeEntity> findByIdClientOrderByIdDesc(Long idClient);
    List<CommandeEntity> findByIdRestaurantOrderByIdDesc(Long idRestaurant);
}
