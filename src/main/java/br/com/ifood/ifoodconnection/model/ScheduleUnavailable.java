package br.com.ifood.ifoodconnection.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
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

    private LocalDateTime start;

    private LocalDateTime end;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonCreator
    public ScheduleUnavailable(@JsonProperty("start") LocalDateTime start,
                               @JsonProperty("end") LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    /* FOR HIBERNATE */
    private ScheduleUnavailable() {
    }
}
