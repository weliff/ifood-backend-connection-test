package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.model.validation.Create;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(of = {"start", "end"})
@Entity
@EqualsAndHashCode(of = "id")
@Getter
public class ScheduleUnavailable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime end;

    private Boolean applied;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ScheduleUnavailableReason reason;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @Fetch(FetchMode.JOIN)
    private Restaurant restaurant;

    /* FOR HIBERNATE */
    private ScheduleUnavailable() {
        this.applied = false;
    }

    @JsonCreator
    private ScheduleUnavailable(@JsonProperty("start") LocalDateTime start,
                                @JsonProperty("end") LocalDateTime end,
                                @JsonProperty("reason") ScheduleUnavailableReason reason) {
        this();
        this.start = start;
        this.end = end;
        this.reason = reason;
    }

    public ScheduleUnavailable(Long id, LocalDateTime start, LocalDateTime end, ScheduleUnavailableReason reason) {
        this(start, end, reason);
        this.id = id;
    }

    public ScheduleUnavailable(Long id, LocalDateTime start, LocalDateTime end, Restaurant restaurant, ScheduleUnavailableReason reason) {
        this(id, start, end, reason);
        this.restaurant = restaurant;
    }

    public void apply() {
        this.applied = true;
    }
}
