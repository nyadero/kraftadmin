package com.kraftadmin.analytics;

import com.kraftadmin.dtos.AnalyticsData;

public interface AnalyticsObserver {
    void update(AnalyticsData data);
}
