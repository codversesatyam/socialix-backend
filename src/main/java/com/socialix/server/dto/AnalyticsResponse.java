package com.socialix.server.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
    private long totalReach;
    private double growthPercentage;
    private List<DataPoint> chartPoints;
    private Map<String, Long> platformBreakdown;

    public AnalyticsResponse() {}

    public AnalyticsResponse(long totalReach, double growthPercentage, List<DataPoint> chartPoints, Map<String, Long> platformBreakdown) {
        this.totalReach = totalReach;
        this.growthPercentage = growthPercentage;
        this.chartPoints = chartPoints;
        this.platformBreakdown = platformBreakdown;
    }

    public static class DataPoint {
        private String label;
        private long value;

        public DataPoint() {}

        public DataPoint(String label, long value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }
    }

    public long getTotalReach() { return totalReach; }
    public void setTotalReach(long totalReach) { this.totalReach = totalReach; }
    public double getGrowthPercentage() { return growthPercentage; }
    public void setGrowthPercentage(double growthPercentage) { this.growthPercentage = growthPercentage; }
    public List<DataPoint> getChartPoints() { return chartPoints; }
    public void setChartPoints(List<DataPoint> chartPoints) { this.chartPoints = chartPoints; }
    public Map<String, Long> getPlatformBreakdown() { return platformBreakdown; }
    public void setPlatformBreakdown(Map<String, Long> platformBreakdown) { this.platformBreakdown = platformBreakdown; }
}