package uk.ac.shef.com3529;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class CalendarTest {

    @Test
    public void testIsLeapYear() {
        assertTrue(Calendar.isLeapYear(2020));   // divisible by 4, not 100
        assertTrue(Calendar.isLeapYear(2000));   // divisible by 400
        assertFalse(Calendar.isLeapYear(1900));  // divisible by 100, not 400
        assertFalse(Calendar.isLeapYear(2019));  // not divisible by 4
    }

    @Test
    public void testDaysInMonth() {
        assertEquals(31, Calendar.daysInMonth(1, 2021));  // January
        assertEquals(28, Calendar.daysInMonth(2, 2021));  // Feb (non-leap)
        assertEquals(29, Calendar.daysInMonth(2, 2020));  // Feb (leap)
        assertEquals(30, Calendar.daysInMonth(11, 2021)); // November
    }

    @Test
    public void testDaysBetweenTwoDatesSameMonth() {
        assertEquals(9, Calendar.daysBetweenTwoDates(2021, 5, 1, 2021, 5, 10));
    }

    @Test
    public void testDaysBetweenTwoDatesAcrossMonths() {
        assertEquals(31, Calendar.daysBetweenTwoDates(2021, 1, 15, 2021, 2, 15));
    }

    @Test
    public void testDaysBetweenTwoDatesAcrossYears() {
        assertEquals(367, Calendar.daysBetweenTwoDates(2019, 12, 31, 2021, 1, 1)); // includes leap year 2020
    }

    @Test
    public void testDaysBetweenWithUnorderedDates() {
        // date2 is before date1, should still return correct positive days
        assertEquals(30, Calendar.daysBetweenTwoDates(2021, 4, 30, 2021, 3, 31));
    }

    @Test
    public void testDateSanitization() {
        // Invalid months/days should be sanitized internally
        assertEquals(0, Calendar.daysBetweenTwoDates(2021, 2, 30, 2021, 2, 30)); // same date after sanitization
    }
}
