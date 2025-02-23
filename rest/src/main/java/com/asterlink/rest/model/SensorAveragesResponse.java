package com.asterlink.rest.model;


public class SensorAveragesResponse {
    private String timestamp;
    private double avgTemp;
    private double avgHumidity;
    private double avgLight;
    private double avgSoil;

    public SensorAveragesResponse(String timestamp, double avgTemp, double avgHumidity, double avgLight, double avgSoil) {
        this.timestamp = timestamp;
        this.avgTemp = avgTemp;
        this.avgHumidity = avgHumidity;
        this.avgLight = avgLight;
        this.avgSoil = avgSoil;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public double getAvgLight() {
        return avgLight;
    }

    public void setAvgLight(double avgLight) {
        this.avgLight = avgLight;
    }

    public double getAvgSoil() {
        return avgSoil;
    }

    public void setAvgSoil(double avgSoil) {
        this.avgSoil = avgSoil;
    }
}
