package com.bowerzlabs.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsData {
    /**
     * The selected time
     */
    private PeriodFilter periodFilter;
    /**
     * Total entities matching that filter
     */
    private long total;
    /**
     * Total count in the previous equivalent time period (used for calculating change).
     */
    private long previousTotal;
    private int change;
    /**
     * The actual computed percentage difference between previousTotal and total.
     */
    private double percentageChange;
    /**
     * labels for charts.
     */
    private List<String> labels;
    /**
     * Useful for bar/line charts — actual values for each label (e.g. [54, 76, 22, 91]).
     */
    private List<Long> values;
    /**
     * structure for attaching any extra info (e.g., top countries, most active users).
     */
    private Map<String, Object> metadata;
    /**
     * Useful for color coding the frontend or setting chart behavior.
     */
    private String trend;
    /**
     * Explicitly show the period the data is based on.
     */
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public AnalyticsData(PeriodFilter periodFilter, long total, int change) {
        this.periodFilter = periodFilter;
        this.total = total;
        this.change = change;
    }

    public AnalyticsData() {
    }

    public PeriodFilter getPeriodFilter() {
        return periodFilter;
    }

    public void setPeriodFilter(PeriodFilter periodFilter) {
        this.periodFilter = periodFilter;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public long getPreviousTotal() {
        return previousTotal;
    }

    public void setPreviousTotal(long previousTotal) {
        this.previousTotal = previousTotal;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(double percentageChange) {
        this.percentageChange = percentageChange;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Long> getValues() {
        return values;
    }

    public void setValues(List<Long> values) {
        this.values = values;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * calculates change for the period filter
     */
    public void calculateChange() {
        if (previousTotal == 0) {
            this.percentageChange = 100.0;
            this.trend = "up";
            return;
        }
        double diff = total - previousTotal;
        this.percentageChange = ((diff / previousTotal) * 100);
        this.trend = diff > 0 ? "up" : (diff < 0 ? "down" : "steady");
        this.change = (int) diff;
    }

    @Override
    public String toString() {
        return "AnalyticsData{" +
                "periodFilter=" + periodFilter +
                ", total=" + total +
                ", previousTotal=" + previousTotal +
                ", change=" + change +
                ", percentageChange=" + percentageChange +
                ", labels=" + labels +
                ", values=" + values +
                ", metadata=" + metadata +
                ", trend=" + trend +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
