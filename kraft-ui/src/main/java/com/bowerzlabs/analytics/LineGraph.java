package com.bowerzlabs.analytics;

import java.util.Map;

public class LineGraph extends AnalyticsComponent{
    public LineGraph(String title, String description) {
        super(title, description);
    }

    /**
     * This will return a model to inject into Thymeleaf. Subclasses to implement fetching and transforming data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of(
                "title", getTitle(),
                "labels", new String[]{"Jan", "Feb", "Mar", "Apr"},
                "values", new int[]{300, 500, 400, 700}
        );
    }

    /**
     * Returns the Thymeleaf fragment path to render this component.
     */
    @Override
    public String getTemplatePath() {
        return "fragments/analytics/linechart";
    }
}
