package br.com.ifood.ifoodconnection.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Embeddable
public class OpeningHour implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "opening_from")
    private LocalTime from;

    @NotNull
    @Column(name = "opening_to")
    private LocalTime to;

    private OpeningHour() {
    }

    public boolean isOpenNow() {
        LocalTime now = getNow();
        return (now.equals(from) || now.isAfter(from)) && (now.equals(to) || now.isBefore(to));
    }

    protected LocalTime getNow() {
        return LocalTime.now();
    }

}
