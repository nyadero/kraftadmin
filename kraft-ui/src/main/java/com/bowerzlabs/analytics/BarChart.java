package com.bowerzlabs.analytics;

import java.util.Map;

public class BarChart extends AnalyticsComponent{
    public BarChart(String title, String description) {
        super(title, description);
    }

    /**
     * This will return a model to inject into Thymeleaf.
     */
    @Override
        public Map<String, Object> getModelData() {
        // implement fetching data from the database
            return Map.of(
                    "title", getTitle(),
                    "labels", new String[]{"Jan", "Feb", "Mar"},
                    "values", new int[]{1000000, 15000000, 7000918}
            );
        }


    /**
     * Returns the Thymeleaf fragment path to render this component.
     */
    @Override
    public String getTemplatePath() {
        return "fragments/analytics/bar-chart";
    }
}

