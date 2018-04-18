package br.com.ifood.ifoodconnection.fake;

import br.com.ifood.ifoodconnection.model.OpeningHour;
import lombok.Data;

import java.time.LocalTime;

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
}
