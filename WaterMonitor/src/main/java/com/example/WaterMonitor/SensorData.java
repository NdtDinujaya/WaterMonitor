package com.example.WaterMonitor;

public class SensorData {
    private double Temperature;
    private double ecValue;
    private double turbidity;

    // Getters and setters for temperature, ecValue, and turbidity

    public double getTemperature() {
        return Temperature;
    }

    public void setTemperature(double temperature) {
        Temperature = temperature;
    }

    public double getEcValue() {
        return ecValue;
    }

    public void setEcValue(double ecValue) {
        this.ecValue = ecValue;
    }

    public double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(double turbidity) {
        this.turbidity = turbidity;
    }
}
