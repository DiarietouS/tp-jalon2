package com.restaurants.business.repositories;

import com.restaurants.business.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByTypeCuisine(String typeCuisine);
    List<Restaurant> findByNomContainingIgnoreCase(String nom);
}
