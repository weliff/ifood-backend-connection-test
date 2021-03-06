package br.com.ifood.ifoodconnection.controller;

import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import br.com.ifood.ifoodconnection.model.validation.Create;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

@RestController
@RequestMapping("/restaurants/{restaurantId}/schedules-unavailable")
public class RestaurantScheduleController {

    private RestaurantService restaurantService;

    private RestaurantRepository restaurantRepository;

    public RestaurantScheduleController(RestaurantService restaurantService, RestaurantRepository restaurantRepository) {
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public Page<ScheduleUnavailable> findRestaurantSchedules(@PathVariable Long restaurantId, @PageableDefault(15) Pageable pageable) {
        return this.restaurantRepository.findSchedulesUnavailableByRestaurant(restaurantId, pageable);
    }

    @PostMapping
    public void saveScheduleUnavailable(@PathVariable Long restaurantId,
                                        @RequestBody @Validated({Create.class, Default.class}) ScheduleUnavailable schedule) {

        this.restaurantService.saveScheduleUnavailable(restaurantId, schedule);
    }

    @DeleteMapping("/{scheduleId}")
    public void removeScheduleUnavailable(@PathVariable Long restaurantId, @PathVariable Long scheduleId) {
        this.restaurantService.deleteScheduleUnavailable(restaurantId, scheduleId);
    }
}
