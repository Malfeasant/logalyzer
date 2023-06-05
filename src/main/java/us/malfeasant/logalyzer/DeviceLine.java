package us.malfeasant.logalyzer;

public class DeviceLine {
    public final String client;
    public final String deviceId;
    public final String deviceIp;

    public DeviceLine(String deviceLine) {
        if (deviceLine == null) {
            throw new NullPointerException("DeviceLine constructor called with nulls.  Bad.");
        }
        var clientIdx = deviceLine.indexOf("Client - ");
        var deviceIdx = deviceLine.indexOf(", Device - ");
        var typeIdx = deviceLine.indexOf(", Type - ");
        var ipIdx = deviceLine.indexOf(", IP - ");
        
        // TODO fix this up
        client = deviceLine.substring(clientIdx + 9, deviceIdx);
        deviceId = deviceLine.substring(deviceIdx + 11, typeIdx);
        deviceIp = deviceLine.substring(ipIdx + 7);   // TODO separate IP & port?

        
    }
}
