package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.fake.FakeOpeningHour;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class OpeningHourTest {

    @Test
    public void shouldBeOpenWhenNowInTheRangeOpeningHour() throws Exception {
        FakeOpeningHour openingHour = new FakeOpeningHour(LocalTime.of(10, 0), LocalTime.of(23, 0));
        openingHour.setNow(LocalTime.of(12, 0));
        Assertions.assertThat(openingHour.isOpenNow())
                .isTrue();
    }

    @Test
    public void shouldBeCloseWhenNowPassedOnRangeOpeningHour() throws Exception {
        FakeOpeningHour openingHour = new FakeOpeningHour(LocalTime.of(10, 0), LocalTime.of(23, 0));
        openingHour.setNow(LocalTime.of(1, 0));
        Assertions.assertThat(openingHour.isOpenNow())
                .isFalse();
    }

    @Test
    public void shouldBeOpenWhenNowIsEqualOf() throws Exception {
        LocalTime of = LocalTime.of(10, 0);
        FakeOpeningHour openingHour = new FakeOpeningHour(of, LocalTime.of(23, 0));
        openingHour.setNow(LocalTime.of(10, 0));
        Assertions.assertThat(openingHour.isOpenNow())
            .isTrue();
    }


    @Test
    public void shouldByOpenWhenNowIsEqualTo() throws Exception {
        FakeOpeningHour openingHour = new FakeOpeningHour(LocalTime.of(10, 0), LocalTime.of(23, 0));
        openingHour.setNow(LocalTime.of(23, 0));
        Assertions.assertThat(openingHour.isOpenNow())
                .isTrue();
    }
}