package us.malfeasant.logalyzer;

/**
 * Helper class- makes it easier to build a CashDevice from its line of log text
 */
public class DeviceLine {
    public final String client;
    public final String type;
    public final String deviceId;
    public final String deviceIp;

    public DeviceLine(String deviceLine) {
        if (deviceLine == null) {
            throw new NullPointerException("DeviceLine constructor called with nulls.  Bad.");
        }
        var clientIdx = deviceLine.indexOf("Client - ");        // must add to index 
        var deviceIdx = deviceLine.indexOf(", Device - ");      // to account for
        var typeIdx = deviceLine.indexOf(", Type - ");          // match string
        var ipIdx = deviceLine.indexOf(", IP - ");
        
        // TODO fix this up
        client = deviceLine.substring(clientIdx + 9, deviceIdx);
        deviceId = deviceLine.substring(deviceIdx + 11, typeIdx);
        deviceIp = deviceLine.substring(ipIdx + 7);   // TODO separate IP & port?
        type = deviceLine.substring(typeIdx + 9, ipIdx);
    }
}
