package com.restaurants.business.repositories;

import com.restaurants.business.models.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {
    List<Plat> findByRestaurantId(Long restaurantId);
    List<Plat> findByRestaurantIdAndDisponible(Long restaurantId, boolean disponible);
}
