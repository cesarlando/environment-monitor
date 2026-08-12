package br.com.cesarlando.environmentmonitor.dto;

public class EnvironmentHistorySummaryResponse {

    private Long environmentId;
    private String environmentName;
    private long totalChecks;
    private long offLineCount;
    private Double averageResponseTime;

    public EnvironmentHistorySummaryResponse() {
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public long getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(long totalChecks) {
        this.totalChecks = totalChecks;
    }

    public long getOffLineCount() {
        return offLineCount;
    }

    public void setOffLineCount(long offLineCount) {
        this.offLineCount = offLineCount;
    }

    public Double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }
}
