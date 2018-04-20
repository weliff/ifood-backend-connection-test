package br.com.ifood.ifoodconnection.controller;

import br.com.ifood.ifoodconnection.model.Restaurant;
import br.com.ifood.ifoodconnection.model.RestaurantHistory;
import br.com.ifood.ifoodconnection.model.view.ViewSummary;
import br.com.ifood.ifoodconnection.repository.RestaurantRepository;
import br.com.ifood.ifoodconnection.service.RestaurantService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@Validated
public class RestaurantController {

    private RestaurantService restaurantService;

    private RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantService restaurantService, RestaurantRepository restaurantRepository) {
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
    }

    @JsonView(ViewSummary.class)
    @GetMapping
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    @GetMapping(params = "restaurantIds")
    public List<Restaurant> findAllByIds(@RequestParam @Size(min=1, max = 100) List<Long> restaurantIds) {
        return this.restaurantService.findAllByIds(restaurantIds);
    }

    @GetMapping("/{id}")
    public Restaurant findById(@PathVariable("id") Long restaurantId) {
        return this.restaurantService.findById(restaurantId);
    }

    @GetMapping("/{id}/history")
    @Transactional
    public Page<RestaurantHistory> getConnectionStatusHistory(@PathVariable("id") Long restaurantId,
                                                              @PageableDefault(15) Pageable pageable) {
        return restaurantRepository.findHistoryById(restaurantId, pageable);
    }


}
