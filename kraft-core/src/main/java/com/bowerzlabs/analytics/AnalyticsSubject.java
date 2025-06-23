package com.bowerzlabs.analytics;

public interface AnalyticsSubject {
    void addSubscriber(AnalyticsObserver analyticsObserver);
    void unSubscribe(AnalyticsObserver analyticsObserver);
    void notifySubscribers();
}

