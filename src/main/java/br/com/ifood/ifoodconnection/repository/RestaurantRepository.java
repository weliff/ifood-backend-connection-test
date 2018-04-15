package br.com.ifood.ifoodconnection.repository;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>{

    @Query("select r from Restaurant r left join fetch r.unavailables where r.id = ?1")
    Optional<Restaurant> findByIdWithUnavailables(Long id);

    @Query("select rh from Restaurant r join r.histories rh where r.id = ?1")
    List<RestaurantHistory> findHistory(Long restaurantId);
}
