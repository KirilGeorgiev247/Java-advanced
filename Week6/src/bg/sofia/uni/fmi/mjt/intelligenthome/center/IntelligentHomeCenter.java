package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.KWhComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.RegistrationComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class IntelligentHomeCenter {
    DeviceStorage storage;
//    Comparator<IoTDevice> comparatorKWh = new Comparator<IoTDevice>() {
//        public int compare(IoTDevice firstDevice, IoTDevice secondDevice) {
//            long hoursFirstDevice =
//                Duration.between(firstDevice.getInstallationDateTime(), LocalDateTime.now()).toHours();
//            long hoursSecondDevice =
//                Duration.between(secondDevice.getInstallationDateTime(), LocalDateTime.now()).toHours();
//            double firstDeviceConsumption = firstDevice.getPowerConsumption();
//            double secondDeviceConsumption = secondDevice.getPowerConsumption();
//
//            if (firstDeviceConsumption * hoursFirstDevice > secondDeviceConsumption * hoursSecondDevice) {
//                return 1;
//            } else if (firstDeviceConsumption * hoursFirstDevice < secondDeviceConsumption * hoursSecondDevice) {
//                return -1;
//            } else {
//                return 0;
//            }
//
//        }
//    };

    public IntelligentHomeCenter(DeviceStorage storage) {
        this.storage = storage;
    }

    /**
     * Adds a @device to the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already
     *                                          registered.
     */
    public void register(IoTDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (storage.exists(device.getId())) {
            throw new DeviceAlreadyRegisteredException("device already exists");
        }

        storage.store(device.getId(), device);
        device.setRegistration(LocalDateTime.now());
    }

    /**
     * Removes the @device from the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(IoTDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (!storage.exists(device.getId())) {
            throw new DeviceNotFoundException("device not found");
        }

        storage.delete(device.getId());
    }

    /**
     * Returns a IoTDevice with an ID @id if found.
     *
     * @throws IllegalArgumentException in case @id is null or empty.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public IoTDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (storage.exists(id)) {
            return storage.get(id);
        } else {
            throw new DeviceNotFoundException("device not found");
        }
    }

    /**
     * Returns the total number of devices with type @type registered in
     * SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("device type is null");
        }

        int quantity = 0;

        for (IoTDevice value : storage.listAll()) {
            if (value.getType().getShortName().equals(type.getShortName())) {
                quantity++;
            }
        }

        return quantity;
    }

    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be more than 0");
        }

        List<IoTDevice> list = new LinkedList<>(storage.listAll());

        KWhComparator compareKWh = new KWhComparator();
        list.sort(compareKWh);

        List<String> arrList = new ArrayList<>();

        if (n > list.size()) {
            n = list.size();
        }

        for (int i = 0; i < n; i++) {
            arrList.add(list.get(i).getId());
        }

        return arrList;
    }

    public Collection<IoTDevice> getFirstNDevicesByRegistration(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be more than 0");
        }

        List<IoTDevice> list = new LinkedList<>(storage.listAll());

        RegistrationComparator compareReg = new RegistrationComparator();
        list.sort(compareReg);

        List<IoTDevice> arrList = new ArrayList<>();

        if (n > list.size()) {
            n = list.size();
        }

        for (int i = 0; i < n; i++) {
            arrList.add(list.get(i));
        }

        return arrList;
    }

}