package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.model.view.ViewSummary;
import br.com.ifood.ifoodconnection.model.exception.ScheduleDateTimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.*;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
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
    @Transient
    private RestaurantStatus status;

    @Transient
    @JsonView(ViewSummary.class)
    private ConnectionState connectionState;

    @JsonIgnore
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeepAliveSignal> keepAliveSignals = new ArrayList<>();

    @JsonIgnore
    @Transient
    private long minutesAwaitPing;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ScheduleUnavailable> unavailables = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantHistory> histories = new ArrayList<>();

    /* FOR HIBERNATE */
    private Restaurant() {
    }

    public Restaurant(String name, long minutesAwaitPing) {
        this.name = name;
        this.minutesAwaitPing = minutesAwaitPing;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public RestaurantStatus getStatus() {
        return this.status;
    }

    public boolean hasScheduleUnavailableNow() {
        return getUnavailables().stream()
            .anyMatch(s -> now().isAfter(s.getStart()) && now().isBefore(s.getEnd()));
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

    public void addKeepAliveSignal(KeepAliveSignal signal) {
        signal.setRestaurant(this);
        this.keepAliveSignals.add(signal);
        calculateConnectionStatus();
    }

    @PostLoad
    private void calculateConnectionStatus() {
        if (hasScheduleUnavailableNow()) {
            this.status = RestaurantStatus.UNAVAILABLE;
            this.connectionState = ConnectionState.OFFLINE;
        } else {
            this.status = RestaurantStatus.AVAILABLE;
            if (lastSignalMinutes() <= this.minutesAwaitPing) {
                this.connectionState = ConnectionState.ONLINE;
            } else {
                this.connectionState = ConnectionState.OFFLINE;
            }
        }
    }

    private boolean hasScheduleUnavailableInRange(LocalDateTime start, LocalDateTime end) {
        return getUnavailables().stream()
                .anyMatch(s -> s.getStart().isBefore(start) && s.getEnd().isAfter(end));
    }

    private long lastSignalMinutes() {
        return Duration.between(lastDateKeepAliveSignal(), now()).toMinutes();
    }

    private LocalDateTime lastDateKeepAliveSignal() {
        return this.getKeepAliveSignals().stream()
                .max(Comparator.comparing(KeepAliveSignal::getReceivedDate))
                .map(KeepAliveSignal::getReceivedDate)
                .orElse(LocalDateTime.MIN);
    }

    public List<ScheduleUnavailable> getUnavailables() {
        return Collections.unmodifiableList(unavailables);
    }

    public List<KeepAliveSignal> getKeepAliveSignals() {
        return Collections.unmodifiableList(keepAliveSignals);
    }

}
