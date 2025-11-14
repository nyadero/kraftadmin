package com.kraftadmin.dtos;


import com.kraftadmin.constants.TimePeriod;

import java.time.LocalDateTime;

public class PeriodFilter {
    private final TimePeriod period;
    private final LocalDateTime time;

    public PeriodFilter(TimePeriod period) {
        this.period = period;
        time = switch (period) {
//            case MINUTE -> LocalDateTime.now().minusMinutes(1);
//            case HOUR -> LocalDateTime.now().minusHours(1);
            case DAY -> LocalDateTime.now().minusDays(1);
            case WEEK -> LocalDateTime.now().minusDays(7);
            case MONTH -> LocalDateTime.now().minusMonths(1);
            case YEAR -> LocalDateTime.now().minusYears(1);
        };
    }

    public LocalDateTime getTime() {
        return time;
    }

    public TimePeriod getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return "PeriodFilter{" +
                "time=" + time +
                ", period=" + period +
                '}';
    }
}
