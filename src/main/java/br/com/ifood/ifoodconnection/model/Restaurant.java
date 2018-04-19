package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.model.exception.ScheduleConflictDateTimeException;
import br.com.ifood.ifoodconnection.model.exception.ScheduleUnavailableStateException;
import br.com.ifood.ifoodconnection.model.view.ViewSummary;
import br.com.ifood.ifoodconnection.service.exception.ScheduleUnavailableNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    @NotNull
    @Valid
    @Embedded
    private OpeningHour openingHour;

    private Boolean sendingKeepAliveSignal;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleUnavailable> unavailables = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantHistory> histories = new ArrayList<>();

    /* FOR HIBERNATE */
    private Restaurant() {
    }

    public Restaurant(String name, OpeningHour openingHour) {
        Assert.notNull(openingHour, "OpeningHour is required");
        this.name = name;
        this.openingHour = openingHour;
        this.status = RestaurantStatus.AVAILABLE;
        this.connectionState = ConnectionState.OFFLINE;
        this.sendingKeepAliveSignal = false;
    }

    public void addScheduleUnavailable(ScheduleUnavailable scheduleUnavailable) {
        if (hasScheduleUnavailableInRange(scheduleUnavailable.getStart(), scheduleUnavailable.getEnd())) {
            throw new ScheduleConflictDateTimeException("There is already a scheduler at this time.");
        }
        scheduleUnavailable.setRestaurant(this);
        unavailables.add(scheduleUnavailable);
    }


    public void removeScheduleUnavailable(Long scheduleId) {
        ScheduleUnavailable scheduleUnavailable = this.unavailables.stream().filter(s -> s.getId().equals(scheduleId))
                .findAny()
                .orElseThrow(() -> new ScheduleUnavailableNotFoundException(String.format("Not found the resource ScheduleUnavailable with id=%s", scheduleId)));
        if (scheduleUnavailable.getApplied()) {
            throw new ScheduleUnavailableStateException(String.format("Schedule has already been applied with Schedule=%s", scheduleUnavailable));
        }
        this.unavailables.remove(scheduleUnavailable);
    }

    public void sendingKeepAliveSignal(boolean sending) {
        this.sendingKeepAliveSignal = sending;
        changeConnectionState();
        this.histories.add(new RestaurantHistory(this));
    }

    public void changeStatus(RestaurantStatus status) {
        this.status = status;
        changeConnectionState();
        this.histories.add(new RestaurantHistory(this));
    }

    public List<ScheduleUnavailable> getUnavailables() {
        return Collections.unmodifiableList(unavailables);
    }

    private void changeConnectionState() {
        if (sendingKeepAliveSignal && status == RestaurantStatus.AVAILABLE && openingHour.isOpenNow()) {
            this.connectionState = ConnectionState.ONLINE;
        } else {
            this.connectionState = ConnectionState.OFFLINE;
        }
    }

    private boolean hasScheduleUnavailableInRange(LocalDateTime start, LocalDateTime end) {
        return getUnavailables().stream()
                .anyMatch(s -> (s.getStart().isEqual(start) || s.getEnd().isEqual(end)) ||  (s.getStart().isBefore(start)
                        && (s.getEnd().isAfter(end))));
    }
}
