package com.bowerzlabs.analytics;

import com.bowerzlabs.dtos.AnalyticsData;

public interface AnalyticsObserver {
    void update(AnalyticsData data);
}
