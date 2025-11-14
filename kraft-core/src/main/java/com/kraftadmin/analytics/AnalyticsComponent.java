package com.kraftadmin.analytics;

import com.kraftadmin.dtos.AnalyticsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsComponent implements AnalyticsSubject {
    public String templatePath = "";
    private String title;
    private String description;
    private AnalyticsData data = new AnalyticsData();
    private List<AnalyticsObserver> analyticsObservers;
    private String type;
    /**
     * Optional filters, e.g. "dateFrom", "dateTo", "eventType", "appType".
     * Subclasses can override to support dynamic filtering.
     */
    private Map<String, Object> filters;


    public AnalyticsComponent() {
    }

    public AnalyticsComponent(String title, String description) {
        this.title = title;
        this.description = description;
        this.analyticsObservers = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AnalyticsData getData() {
        return data;
    }

    public void setData(AnalyticsData data) {
        this.data = data;
        notifySubscribers();
    }

    public Map<String, Object> getFilters() {
        return Map.of();
    }

    @Override
    public void addSubscriber(AnalyticsObserver analyticsObserver) {
        analyticsObservers.add(analyticsObserver);
    }

    @Override
    public void unSubscribe(AnalyticsObserver analyticsObserver) {
        int i = analyticsObservers.indexOf(analyticsObserver);
        if (i >= 0) {
            analyticsObservers.remove(i);
        }
    }

    @Override
    public void notifySubscribers() {
//        could use event listener to track create and delete operations and update
        for (AnalyticsObserver analyticsObserver : analyticsObservers) {
            analyticsObserver.update(data);
        }
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }


    @Override
    public String toString() {
        return "AnalyticsComponent{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", data=" + data +
                ", analyticsObservers=" + analyticsObservers +
                ", templatePath='" + templatePath + '\'' +
                ", type='" + type + '\'' +
                ", filters=" + filters +
                '}';
    }

}
