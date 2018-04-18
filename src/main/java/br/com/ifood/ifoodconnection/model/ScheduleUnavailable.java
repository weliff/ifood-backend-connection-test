package br.com.ifood.ifoodconnection.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PastOrPresent;
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

    @FutureOrPresent
    private LocalDateTime start;

    @FutureOrPresent
    private LocalDateTime end;

    private Boolean applied;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    /* FOR HIBERNATE */
    private ScheduleUnavailable() {
        this.applied = false;
    }

    @JsonCreator
    private ScheduleUnavailable(@JsonProperty("date") LocalDateTime start,
                                @JsonProperty("end") LocalDateTime end) {
        this();
        this.start = start;
        this.end = end;
    }

    public ScheduleUnavailable(Long id, LocalDateTime start, LocalDateTime end) {
        this(start, end);
        this.id = id;
    }

    public ScheduleUnavailable(Long id, LocalDateTime start, LocalDateTime end, Restaurant restaurant) {
        this(id, start, end);
        this.restaurant = restaurant;
    }

    public void apply() {
        this.applied = true;
    }
}
