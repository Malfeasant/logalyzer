package us.malfeasant.logalyzer;

public class CashDevice extends Thing {
    private final String deviceIp;
    
    CashDevice(DeviceLine line) {
        super(Type.DEVICE, line.deviceId);
        
        deviceIp = line.deviceIp;
    }
}
