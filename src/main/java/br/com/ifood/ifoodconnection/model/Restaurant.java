package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.model.exception.ScheduleDateTimeException;
import br.com.ifood.ifoodconnection.model.view.ViewSummary;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;

@EqualsAndHashCode(of = "id")
@Getter
@Entity
@DynamicUpdate
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonView(ViewSummary.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(ViewSummary.class)
    private String name;

    @JsonView(ViewSummary.class)
    private RestaurantStatus status;

    @JsonView(ViewSummary.class)
    private ConnectionState connectionState;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ScheduleUnavailable> unavailables = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantHistory> histories = new ArrayList<>();

    /* FOR HIBERNATE */
    private Restaurant() {
    }

    public Restaurant(String name) {
        this.name = name;
        this.connectionState = ConnectionState.OFFLINE;
    }

    @PostLoad
    private void calculateConnectionStatus() {
        if (hasScheduleUnavailableNow()) {
            this.status = RestaurantStatus.UNAVAILABLE;
            this.connectionState = ConnectionState.OFFLINE;
        } else {
            this.status = RestaurantStatus.AVAILABLE;
        }
    }

    public void addScheduleUnavailable(ScheduleUnavailable scheduleUnavailable) {
        if (hasScheduleUnavailableInRange(scheduleUnavailable.getStart(), scheduleUnavailable.getEnd())) {
            throw new ScheduleDateTimeException("There is already a scheduler at this time.");
        }
        scheduleUnavailable.setRestaurant(this);
        unavailables.add(scheduleUnavailable);
        calculateConnectionStatus();
    }


    public boolean removeScheduleUnavailable(Long scheduleId) {
        boolean removed = this.unavailables.removeIf(scheduleUnavailable -> scheduleUnavailable.getId().equals(scheduleId));
        calculateConnectionStatus();
        return removed;
    }

    private boolean hasScheduleUnavailableInRange(LocalDateTime start, LocalDateTime end) {
        return getUnavailables().stream()
                .anyMatch(s -> s.getStart().isBefore(start) && s.getEnd().isAfter(end));
    }


    public boolean hasScheduleUnavailableNow() {
        return getUnavailables().stream()
                .anyMatch(s -> now().isAfter(s.getStart()) && now().isBefore(s.getEnd()));
    }

    public void changeConnectionState(ConnectionState state) {
        this.connectionState = state;
        this.histories.add(new RestaurantHistory(this));
    }

    public List<ScheduleUnavailable> getUnavailables() {
        return Collections.unmodifiableList(unavailables);
    }
}
