package br.com.ifood.ifoodconnection.repository;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantHistory;
import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>{

    @Query("select rh from Restaurant r join r.histories rh where r.id = ?1")
    Page<RestaurantHistory> findHistory(Long restaurantId, Pageable pageable);

    @Query("select u from ScheduleUnavailable u join fetch u.restaurant r where u.start = ?2 and r.id = ?1")
    Optional<ScheduleUnavailable> findScheduleUnavailableByRestaurantAndStartDate(Long id, LocalDateTime startDate);


    @Query("select u from ScheduleUnavailable u join fetch u.restaurant r where u.end = ?2 and r.id = ?1")
    Optional<ScheduleUnavailable> findScheduleUnavailableByRestaurantAndEndDate(Long id, LocalDateTime endDate);

    @Query("select u from ScheduleUnavailable u join u.restaurant r where r.id = ?1")
    Page<ScheduleUnavailable> findSchedulesUnavailableByRestaurant(Long restaurantId, Pageable pageable);
}
