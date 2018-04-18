package br.com.ifood.ifoodconnection.controller;

import br.com.ifood.ifoodconnection.model.ScheduleUnavailable;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants/{restaurantId}/schedules-unavailable")
public class RestaurantScheduleController {

    private RestaurantService restaurantService;

    public RestaurantScheduleController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    //TODO: implementar paginacao
    public List<ScheduleUnavailable> findRestaurantSchedules(@PathVariable Long restaurantId) {
        return this.restaurantService.findById(restaurantId).getUnavailables();

    }

    @PostMapping
    public void saveScheduleUnavailable(@PathVariable Long restaurantId, @RequestBody @Validated ScheduleUnavailable scheduleUnavailable) {
        this.restaurantService.saveScheduleUnavailable(restaurantId, scheduleUnavailable);
    }

    @DeleteMapping("/{scheduleId}")
    public void removeScheduleUnavailable(@PathVariable Long restaurantId, @PathVariable Long scheduleId) {
        this.restaurantService.deleteScheduleUnavailable(restaurantId, scheduleId);
    }
}
