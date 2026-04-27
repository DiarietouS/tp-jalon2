package com.restaurants.business.repository;

import com.restaurants.business.entity.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RestaurantRepository extends CrudRepository<RestaurantEntity, Long> {
    List<RestaurantEntity> findByIdProprietaire(Long idProprietaire);
}
