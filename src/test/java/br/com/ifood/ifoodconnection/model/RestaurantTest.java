package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.model.exception.ScheduleConflictDateTimeException;
import br.com.ifood.ifoodconnection.model.exception.ScheduleUnavailableStateException;
import br.com.ifood.ifoodconnection.service.exception.ScheduleUnavailableNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;

public class RestaurantTest {

    private Restaurant restaurant;

    @Before
    public void setUp() throws Exception {
        this.restaurant = new Restaurant("Restaurant Fake");
    }

    @Test
    public void shouldChangeConnectionStateToOfflineWhenHasScheduleUnavailableValid() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().minusHours(1), now().plusHours(1));
        restaurant.addScheduleUnavailable(scheduleUnavailable);
        assertEquals(restaurant.getConnectionState(), ConnectionState.OFFLINE);
    }

    @Test(expected = ScheduleConflictDateTimeException.class)
    public void shouldThrowErrorWhenHasScheduleInTime() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().plusHours(2), now().plusHours(3));
        ScheduleUnavailable scheduleUnavailable2 = new ScheduleUnavailable(1L, now().plusHours(2).plusMinutes(10), now().plusHours(3).minusMinutes(10));
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        this.restaurant.addScheduleUnavailable(scheduleUnavailable2);
    }

    @Test
    public void shouldRemoveScheduleWhenNotApply() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().plusHours(2).plusMinutes(10), now().plusHours(3).minusMinutes(10));
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
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(1L, now().plusHours(2).plusMinutes(10), now().plusHours(3).minusMinutes(10));
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);

        scheduleUnavailable.apply();
        this.restaurant.removeScheduleUnavailable(1L);
    }

    @Test
    public void shouldChangeStatusToUnavailableAndChangeConnectionStateToOffline() throws Exception {
        this.restaurant.changeStatus(RestaurantStatus.UNAVAILABLE);
        Assertions.assertThat(this.restaurant.getStatus()).isEqualTo(RestaurantStatus.UNAVAILABLE);
        Assertions.assertThat(this.restaurant.getConnectionState()).isEqualTo(ConnectionState.OFFLINE);
    }

    @Test
    public void shouldChangeStatusToAvailableAndChangeConnectionStateToOffline() throws Exception {
        this.restaurant.changeStatus(RestaurantStatus.AVAILABLE);
        Assertions.assertThat(this.restaurant.getStatus()).isEqualTo(RestaurantStatus.AVAILABLE);
        Assertions.assertThat(this.restaurant.getConnectionState()).isEqualTo(ConnectionState.OFFLINE);
    }
}