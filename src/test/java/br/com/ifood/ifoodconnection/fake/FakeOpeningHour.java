package br.com.ifood.ifoodconnection.fake;

import br.com.ifood.ifoodconnection.model.OpeningHour;

import java.time.LocalTime;

/**
 * Essa classe utiliza uma técnica de substituição de dependencias, que simula o valor
 * do  {@link LocalTime} #now() através de setNow() para a flexibilidade dos testes
 */
public class FakeOpeningHour extends OpeningHour {

    private LocalTime now;

    public FakeOpeningHour(LocalTime from, LocalTime to) {
        super(from, to);
    }

    @Override
    protected LocalTime getNow() {
        return now;
    }

    public void setNow(LocalTime now) {
        this.now = now;
    }

    public static OpeningHour openNow() {
        FakeOpeningHour fakeOpeningHour = new FakeOpeningHour(LocalTime.of(10, 0), LocalTime.of(23, 0));
        fakeOpeningHour.setNow(LocalTime.of(19, 0));
        return fakeOpeningHour;
    }

    public static OpeningHour closedNow() {
        FakeOpeningHour fakeOpeningHour = new FakeOpeningHour(LocalTime.of(10, 0), LocalTime.of(23, 0));
        fakeOpeningHour.setNow(LocalTime.of(7, 0));
        return fakeOpeningHour;
    }
}
