package us.malfeasant.logalyzer;

public class CashDevice extends LogComponent {
    private final String deviceIp;
    
    CashDevice(DeviceLine line) {
        super(Type.DEVICE, line.deviceId);
        
        deviceIp = line.deviceIp;
    }
}
