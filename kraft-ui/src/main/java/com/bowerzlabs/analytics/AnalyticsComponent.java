package com.bowerzlabs.analytics;

import java.util.Map;

public abstract class AnalyticsComponent {
    protected String title;
    protected String description;
    // Optional: which application instance or module this component is scoped to
    protected String appInstanceId;
    protected String module;

    public AnalyticsComponent(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Optional filters, e.g. "dateFrom", "dateTo", "eventType", "appType".
     * Subclasses can override to support dynamic filtering.
     */
    public Map<String, Object> getFilters() {
        return Map.of();
    }

    // Abstract method for subclasses to implement fetching and transforming data
//    public abstract Map<String, Object> fetchData();

//    // Render as HTML, can also be renderAsJSON() if frontend is JS-heavy
//    public abstract String render();

    /**
     * This will return a model to inject into Thymeleaf. Subclasses to implement fetching and transforming data
     */
    public abstract Map<String, Object> getModelData();

    /**
     * Returns the Thymeleaf fragment path to render this component.
     */
    public abstract String getTemplatePath();

    public String getType() {
        return this.getClass().getSimpleName(); // e.g. "PieCharts" or "BarCharts"
    }



}
