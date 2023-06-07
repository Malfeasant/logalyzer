package us.malfeasant.logalyzer;

public class CashDevice extends LogComponent {
    private final String deviceIp;
    private final String typeString;
    private final DeviceType type;

    CashDevice(DeviceLine line) {
        super(line.deviceId);
        
        deviceIp = line.deviceIp;
        typeString = line.type;
        type = DeviceType.from(line.type);
    }

    @Override
    public String prettyPrint() {
        return "Device: " + name + " - " + typeString;
    }
}
