package com.restaurants.business.repository;

import com.restaurants.business.entity.PlatEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlatRepository extends CrudRepository<PlatEntity, Long> {
    List<PlatEntity> findByIdRestaurant(Long idRestaurant);
}
