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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
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

    @GetMapping("/{id}")
    public Restaurant findRestaurantById(@PathVariable("id") Long restaurantId) {
        return this.restaurantService.findById(restaurantId);
    }

    @GetMapping("/{id}/history")
    @Transactional
    public Page<RestaurantHistory> getConnectionStatusHistory(@PathVariable("id") Long restaurantId,
                                                              @PageableDefault(15) Pageable pageable) {
        return restaurantRepository.findHistory(restaurantId, pageable);
    }


}
