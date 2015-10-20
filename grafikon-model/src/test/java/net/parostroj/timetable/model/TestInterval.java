package net.parostroj.timetable.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestInterval {
    @Test
    public void testNormalizedInterval() {
        Interval interval = IntervalFactory.createInterval(3600, 7200);

        assertTrue("normalized", interval.isNormalized());
        assertEquals("length one hour", 3600, interval.getLength());
    }
}
