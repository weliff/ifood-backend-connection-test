package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.fake.FakeOpeningHour;
import br.com.ifood.ifoodconnection.model.exception.ScheduleConflictDateTimeException;
import br.com.ifood.ifoodconnection.model.exception.ScheduleUnavailableStateException;
import br.com.ifood.ifoodconnection.service.exception.ScheduleUnavailableNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static br.com.ifood.ifoodconnection.model.ScheduleUnavailableReason.CONNECTION_ISSUES;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;

public class RestaurantTest {

    private Restaurant restaurant;

    @Before
    public void setUp() throws Exception {
        OpeningHour openingHour = FakeOpeningHour.openNow();
        this.restaurant = new Restaurant("Restaurant Fake", openingHour);
    }

    @Test
    public void shouldChangeConnectionStateToOfflineWhenHasScheduleUnavailableValid() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().minusHours(1), now().plusHours(1), CONNECTION_ISSUES);
        restaurant.addScheduleUnavailable(scheduleUnavailable);
        assertEquals(restaurant.getConnectionState(), ConnectionState.OFFLINE);
    }

    @Test(expected = ScheduleConflictDateTimeException.class)
    public void shouldThrowErrorWhenHasScheduleInTime() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().plusHours(2), now().plusHours(3), CONNECTION_ISSUES);
        ScheduleUnavailable scheduleUnavailable2 = new ScheduleUnavailable(1L, now().plusHours(2).plusMinutes(10),
                now().plusHours(3).minusMinutes(10), CONNECTION_ISSUES);
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        this.restaurant.addScheduleUnavailable(scheduleUnavailable2);
    }

    @Test
    public void shouldRemoveScheduleWhenNotApply() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().plusHours(2).plusMinutes(10),
                now().plusHours(3).minusMinutes(10), CONNECTION_ISSUES);
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        this.restaurant.removeScheduleUnavailable(1L);

        Assertions.assertThat(restaurant.getUnavailables())
                .isEmpty();
    }

    @Test(expected = ScheduleUnavailableNotFoundException.class)
    public void shouldThrowErrorWhenNotFoundSchedule() throws Exception {
        this.restaurant.removeScheduleUnavailable(1L);
    }

    @Test(expected = ScheduleUnavailableStateException.class)
    public void shouldThrowErrorWhenScheduleAlreadyApplied() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().plusHours(2).plusMinutes(10),
                now().plusHours(3).minusMinutes(10), CONNECTION_ISSUES);
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);

        scheduleUnavailable.apply();
        this.restaurant.removeScheduleUnavailable(1L);
    }

    @Test
    public void shouldNotChangeConnectionStateToOnlineWhenNotInTheOpeningHours() throws Exception {
        OpeningHour openingHour = FakeOpeningHour.closedNow();
        this.restaurant = new Restaurant("Restaurant Fake", openingHour);
        this.restaurant.sendingKeepAliveSignal(true);

        Assertions.assertThat(this.restaurant.getConnectionState()).isEqualTo(ConnectionState.OFFLINE);
        Assertions.assertThat(this.restaurant.getHistories())
                .hasSize(1);
    }

    @Test
    public void shouldNotChangeConnectionStateToOnlineWhenNotAvailable() throws Exception {
        this.restaurant.sendingKeepAliveSignal(true);
        this.restaurant.changeStatus(RestaurantStatus.UNAVAILABLE);
        Assertions.assertThat(this.restaurant.getConnectionState()).isEqualTo(ConnectionState.OFFLINE);
        Assertions.assertThat(this.restaurant.getHistories())
                .hasSize(2);
    }

    @Test
    public void shouldNotChangeConnectionStateToOnlineWhenNotSendingKeepAliveSignal() throws Exception {
        this.restaurant.sendingKeepAliveSignal(false);
        Assertions.assertThat(this.restaurant.getConnectionState()).isEqualTo(ConnectionState.OFFLINE);
        Assertions.assertThat(this.restaurant.getHistories())
                .hasSize(1);
    }


    @Test
    public void shouldChangeConnectionStateToOnlineWhenInTheOpeningHoursAndSendingKeepAliveSignalAndRestaurantIsAvailable() throws Exception {
        this.restaurant.sendingKeepAliveSignal(true);

        Assertions.assertThat(this.restaurant.getConnectionState()).isEqualTo(ConnectionState.ONLINE);
        Assertions.assertThat(this.restaurant.getHistories())
            .hasSize(1);
    }
}