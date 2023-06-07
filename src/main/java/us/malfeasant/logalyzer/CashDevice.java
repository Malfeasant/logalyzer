package us.malfeasant.logalyzer;

public class CashDevice extends LogComponent {
    private final String deviceIp;
    
    CashDevice(DeviceLine line) {
        super(line.deviceId);
        
        deviceIp = line.deviceIp;
    }

    @Override
    public String prettyPrint() {
        return "Device: " + name;
    }
}
