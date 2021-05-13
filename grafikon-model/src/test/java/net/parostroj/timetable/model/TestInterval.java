package net.parostroj.timetable.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Basic interval test.
 *
 * @author jub
 */
class TestInterval {
    @Test
    void createNormalizedInterval() {
        Interval interval = IntervalFactory.createInterval(3600, 7200);

        assertThat("normalized", interval.isNormalized(), is(true));
        assertThat("length one hour", interval.getLength(), is(3600));
    }

    @Test
    void createNonNormalizedInterval() {
        Interval interval = IntervalFactory.createInterval(-1800, 1800);

        assertThat("non-normalized", interval.isNormalized(), is(false));
        assertThat("length one hour", interval.getLength(), is(3600));
    }

    @Test
    void getNormalizedFromNonNormalizedInterval() {
        Interval nonNormalizedinterval = IntervalFactory.createInterval(-1800, 1800);
        Interval normalizedInterval = nonNormalizedinterval.normalize();

        assertThat("start is normalized", normalizedInterval.getStart(), is(24 * 3600 - 1800));
        assertThat("normalized", normalizedInterval.isNormalized(), is(true));
        assertThat("length one hour", normalizedInterval.getLength(), is(3600));
    }
}
