package br.com.ifood.ifoodconnection.model;

import br.com.ifood.ifoodconnection.model.exception.ScheduleDateTimeException;
import org.junit.Before;
import org.junit.Test;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.*;

public class RestaurantTest {

    private Restaurant restaurant;

    @Before
    public void setUp() throws Exception {
        this.restaurant = new Restaurant("Restaurant Fake");
    }

    @Test
    public void shouldChangeConnectionStateToOfflineWhenHasScheduleUnavailableValid() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(now().minusHours(1), now().plusHours(1));
        restaurant.addScheduleUnavailable(scheduleUnavailable);
        assertEquals(restaurant.getConnectionState(), ConnectionState.OFFLINE);
    }

    @Test
    public void shouldBeUnavailableWhenScheduleDatesInRangeNow() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(now().minusHours(1), now().plusHours(1));
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        assertTrue(restaurant.hasScheduleUnavailableNow());
    }

    @Test
    public void shouldNotBeUnavailableWhenNowIsAfterScheduleEndDate() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(now().minusHours(2), now().minusHours(1));
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        assertFalse(restaurant.hasScheduleUnavailableNow());
    }

    @Test
    public void shouldNotBeUnavailableWhenNowIsBeforeScheduleStartDate() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(now().plusHours(2), now().plusHours(3));
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        assertFalse(restaurant.hasScheduleUnavailableNow());
    }

    @Test(expected = ScheduleDateTimeException.class)
    public void shouldThrowErrorWhenHasScheduleInTime() throws Exception {
        ScheduleUnavailable scheduleUnavailable = new ScheduleUnavailable(now().plusHours(2), now().plusHours(3));
        ScheduleUnavailable scheduleUnavailable2 = new ScheduleUnavailable(now().plusHours(2).plusMinutes(10), now().plusHours(3).minusMinutes(10));
        this.restaurant.addScheduleUnavailable(scheduleUnavailable);
        this.restaurant.addScheduleUnavailable(scheduleUnavailable2);
    }
}