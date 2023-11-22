package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public interface IoTDevice {

    /**
     * Returns the ID of the device.
     */
    String getId();

    /**
     * Returns the name of the device.
     */
    String getName();

    /**
     * Returns the power consumption of the device.
     * For example, a lamp may consume 1kW/hour.
     */
    double getPowerConsumption();

    /**
     * Returns the date and time of device installation.
     * This is a time in the past when the device was 'physically' installed.
     * It is not related to the time when the device is registered in the Hub.
     */
    LocalDateTime getInstallationDateTime();

    /**
     * Returns the type of the device.
     */
    DeviceType getType();

    public long getRegistration();

    public void setRegistration(LocalDateTime registration);

    public long getPowerConsumptionKWh();
}