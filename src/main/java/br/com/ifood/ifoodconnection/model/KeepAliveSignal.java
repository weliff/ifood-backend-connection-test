package br.com.ifood.ifoodconnection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id")
@Getter
@Entity
public class KeepAliveSignal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime receivedDate;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    /* FOR HIBERNATE */
    private KeepAliveSignal() {
    }

    public KeepAliveSignal(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }
}
