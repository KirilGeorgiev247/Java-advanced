package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.MapDeviceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class IntelligentHomeCenterTest {

    @Mock
    DeviceStorage deviceStorageMock = new MapDeviceStorage();

    String testName = "test";

    double testPowerConsumption = 5;

    @Mock
    LocalDateTime localDateTimeMock = LocalDateTime.now(ZoneId.of("UTC-2"));
    @Mock
    AmazonAlexa amazonAlexaMock = new AmazonAlexa(testName, testPowerConsumption, localDateTimeMock);

    IntelligentHomeCenter intelligentHomeCenter;

    @BeforeEach
    void setUp() {
        intelligentHomeCenter = new IntelligentHomeCenter(deviceStorageMock);
    }

    @Test
    void testIfRegisterThrowsWhenDeviceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.register(null),
            "Register method should throw when argument is null!");
    }

    @Test
    void testIfRegisterThrowsWhenDeviceAlreadyExists() throws DeviceAlreadyRegisteredException {
        intelligentHomeCenter.register(amazonAlexaMock);

        assertThrows(DeviceAlreadyRegisteredException.class, () -> intelligentHomeCenter.register(amazonAlexaMock),
            "Register method should throw when device already exists!");
    }

    @Test
    void testIfRegisterMethodStoresItemSuccessfully() throws DeviceAlreadyRegisteredException, DeviceNotFoundException {
        intelligentHomeCenter.register(amazonAlexaMock);

        assertDoesNotThrow(() -> intelligentHomeCenter.getDeviceById(amazonAlexaMock.getId()),
            "Device should have been successfully stored and device not found exception should not be thrown!");

        assertEquals(amazonAlexaMock.getId(), intelligentHomeCenter.getDeviceById(amazonAlexaMock.getId()).getId(),
            "Device Id should be equal to the same Id when stored");
    }

    @Test
    void testIfUnregisterMethodThrowsWhenDeviceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.unregister(null),
            "Unregister method should throw when argument is null!");
    }

    @Test
    void testIfUnregisterMethodThrowsWhenDeviceIsNotFounf() {
        assertThrows(DeviceNotFoundException.class, () -> intelligentHomeCenter.unregister(amazonAlexaMock),
            "Unregister method should throw when device is not found!");
    }

    @Test
    void testIfUnregisterMethodDeletesCorrectly() throws DeviceAlreadyRegisteredException, DeviceNotFoundException {
        intelligentHomeCenter.register(amazonAlexaMock);
        intelligentHomeCenter.unregister(amazonAlexaMock);

        assertThrows(DeviceNotFoundException.class, () -> intelligentHomeCenter.getDeviceById(amazonAlexaMock.getId()),
            "Unregister method should delete the existing device!");
    }

    @Test
    void testIfGetDeviceByIdMethodThrowsWhenArgumentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getDeviceById(null),
            "Get device by id method should throw when argument is null!");
    }

    @Test
    void testIfGetDeviceByIdMethodThrowsWhenDeviceNotFound() {
        assertThrows(DeviceNotFoundException.class, () -> intelligentHomeCenter.getDeviceById(amazonAlexaMock.getId()),
            "Get device by id method should throw when device is not found!");
    }

    @Test
    void testIfGetDeviceByIdMethodReturnCorrect() throws DeviceAlreadyRegisteredException, DeviceNotFoundException {
        intelligentHomeCenter.register(amazonAlexaMock);
        assertEquals(amazonAlexaMock, intelligentHomeCenter.getDeviceById(amazonAlexaMock.getId()),
            "Get device by id should return equal object to added!");
    }

    @Test
    void testIfGetDeviceQuantityByTypeThrowsWhenTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getDeviceQuantityPerType(null),
            "Get device by quantity method should throw when argument is null!");
    }

    @Test
    void testIfGetDeviceQuantityByTypeReturnsCorrectCount() throws DeviceAlreadyRegisteredException {
        AmazonAlexa amazonAlexa1 = new AmazonAlexa(testName, testPowerConsumption, localDateTimeMock);
        AmazonAlexa amazonAlexa2 = new AmazonAlexa(testName, testPowerConsumption, localDateTimeMock);
        AmazonAlexa amazonAlexa3 = new AmazonAlexa(testName, testPowerConsumption, localDateTimeMock);

        RgbBulb rgbBulb1 = new RgbBulb(testName, testPowerConsumption, localDateTimeMock);
        RgbBulb rgbBulb2 = new RgbBulb(testName, testPowerConsumption, localDateTimeMock);

        WiFiThermostat wiFiThermostat1 = new WiFiThermostat(testName, testPowerConsumption, localDateTimeMock);

        intelligentHomeCenter.register(amazonAlexa1);
        intelligentHomeCenter.register(amazonAlexa2);
        intelligentHomeCenter.register(amazonAlexa3);
        intelligentHomeCenter.register(rgbBulb1);
        intelligentHomeCenter.register(rgbBulb2);
        intelligentHomeCenter.register(wiFiThermostat1);

        assertEquals(3, intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER),
            "Quantity type should be equal to added devices!");

        assertEquals(2, intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.BULB),
            "Quantity type should be equal to added devices!");

        assertEquals(1, intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.THERMOSTAT),
            "Quantity type should be equal to added devices!");
    }

    @Test
    void testIfGetTopNDevicesByPowerConsumptionThrowsWhenNegativeArgument() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getTopNDevicesByPowerConsumption(-1),
            "Get top n devices by power consumption method should throw when negative argument is passed!");

        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getTopNDevicesByPowerConsumption(0),
            "Get top n devices by power consumption method should throw when zero argument is passed!");
    }

    @Test
    void testIfGetTopNDevicesByPowerConsumptionReturnsCorrectly() throws DeviceAlreadyRegisteredException {
        AmazonAlexa amazonAlexa1 = new AmazonAlexa(testName, 4, localDateTimeMock);
        AmazonAlexa amazonAlexa2 = new AmazonAlexa(testName, 7, localDateTimeMock);
        AmazonAlexa amazonAlexa3 = new AmazonAlexa(testName, 14, localDateTimeMock);

        RgbBulb rgbBulb1 = new RgbBulb(testName, 2, localDateTimeMock);
        RgbBulb rgbBulb2 = new RgbBulb(testName, 4, localDateTimeMock);

        WiFiThermostat wiFiThermostat1 = new WiFiThermostat(testName, 5, localDateTimeMock);

        intelligentHomeCenter.register(amazonAlexa1);
        intelligentHomeCenter.register(amazonAlexa2);
        intelligentHomeCenter.register(amazonAlexa3);
        intelligentHomeCenter.register(rgbBulb1);
        intelligentHomeCenter.register(rgbBulb2);
        intelligentHomeCenter.register(wiFiThermostat1);

        Collection<String> expected = List.of(amazonAlexa3.getId(), amazonAlexa2.getId(), wiFiThermostat1.getId());

        assertEquals(expected, intelligentHomeCenter.getTopNDevicesByPowerConsumption(3),
            "Method should return the devices with most consumption and the order should be right!");
    }

    @Test
    void testIfGetTopNDevicesByPowerConsumptionReturnsAllIfNIsBiggerThanSize() throws DeviceAlreadyRegisteredException {
        AmazonAlexa amazonAlexa2 = new AmazonAlexa(testName, 7, localDateTimeMock);
        AmazonAlexa amazonAlexa3 = new AmazonAlexa(testName, 14, localDateTimeMock);

        WiFiThermostat wiFiThermostat1 = new WiFiThermostat(testName, 5, localDateTimeMock);

        intelligentHomeCenter.register(amazonAlexa2);
        intelligentHomeCenter.register(amazonAlexa3);
        intelligentHomeCenter.register(wiFiThermostat1);

        Collection<String> expected = List.of(amazonAlexa3.getId(), amazonAlexa2.getId(), wiFiThermostat1.getId());

        assertEquals(expected, intelligentHomeCenter.getTopNDevicesByPowerConsumption(25),
            "Result should have at max the size of the available devices in storage!");
    }

    @Test
    void testIfGetFirstNDevicesByRegistrationThrowsWhenArgumentIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getFirstNDevicesByRegistration(0),
            "Method should throw when argument is 0!");

        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getFirstNDevicesByRegistration(-5),
            "Method should throw when argument is negative!");
    }

    private void safetySleep(long milliseconds) {
        try {
            sleep(milliseconds);
        } catch (InterruptedException interruptedException) {
            fail();
        }
    }

    @Test
    void testIfGetFirstNDevicesByRegistrationReturnsCorrectlyOrdered() throws DeviceAlreadyRegisteredException{
        AmazonAlexa amazonAlexa1 = new AmazonAlexa(testName, 4, LocalDateTime.now(ZoneId.of("UTC-2")).plusDays(3));
        AmazonAlexa amazonAlexa2 = new AmazonAlexa(testName, 7, LocalDateTime.now(ZoneId.of("UTC-2")).plusYears(1));
        AmazonAlexa amazonAlexa3 = new AmazonAlexa(testName, 14, LocalDateTime.now(ZoneId.of("UTC-2")));

        RgbBulb rgbBulb1 = new RgbBulb(testName, 2, LocalDateTime.now(ZoneId.of("UTC-2")).plusDays(3));
        RgbBulb rgbBulb2 = new RgbBulb(testName, 4, LocalDateTime.now(ZoneId.of("UTC-2")).minusDays(2));

        WiFiThermostat wiFiThermostat1 =
            new WiFiThermostat(testName, 5, LocalDateTime.now(ZoneId.of("UTC-2")).minusDays(2));

        intelligentHomeCenter.register(amazonAlexa1);
        safetySleep(1000);
        intelligentHomeCenter.register(amazonAlexa2);
        safetySleep(1000);
        intelligentHomeCenter.register(amazonAlexa3);
        safetySleep(500);
        intelligentHomeCenter.register(rgbBulb1);
        safetySleep(1000);
        intelligentHomeCenter.register(rgbBulb2);
        safetySleep(2000);
        intelligentHomeCenter.register(wiFiThermostat1);

        Collection<IoTDevice> expected =
            List.of(wiFiThermostat1, rgbBulb2, rgbBulb1, amazonAlexa3, amazonAlexa2, amazonAlexa1);


        assertEquals(expected, intelligentHomeCenter.getFirstNDevicesByRegistration(6),
            "Items should be returned by sorted by registration time!");
    }

    @Test
    void testIfGetFirstNDevicesByRegistrationReturnsCorrectlyWhenArgumentIsBiggerThanSize() throws DeviceAlreadyRegisteredException {
        AmazonAlexa amazonAlexa1 = new AmazonAlexa(testName, 4, LocalDateTime.now(ZoneId.of("UTC-2")).plusDays(3));

        RgbBulb rgbBulb2 = new RgbBulb(testName, 4, LocalDateTime.now(ZoneId.of("UTC-2")).minusDays(2));

        WiFiThermostat wiFiThermostat1 =
            new WiFiThermostat(testName, 5, LocalDateTime.now(ZoneId.of("UTC-2")).minusDays(2));

        intelligentHomeCenter.register(amazonAlexa1);
        safetySleep(1000);
        intelligentHomeCenter.register(rgbBulb2);
        safetySleep(1000);
        intelligentHomeCenter.register(wiFiThermostat1);

        Collection<IoTDevice> expected =
            List.of(wiFiThermostat1, rgbBulb2, amazonAlexa1);


        assertEquals(expected, intelligentHomeCenter.getFirstNDevicesByRegistration(6),
            "All items should be returned when N is bigger than size!");
    }
}
