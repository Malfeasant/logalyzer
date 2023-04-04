package us.malfeasant.logalyzer;

import java.util.Optional;

public class CashDevice {
    private final S4LogFile file;
    private final String deviceId;
    private final boolean hasNorm;
    private final String deviceIp;
    private final Optional<String> normIp;

    /**
     * Pass in the log file, and the line from that file which details
     * this device.
     */
    CashDevice(S4LogFile file, String deviceLine) {
        if (file == null || deviceLine == null) {
            throw new IllegalArgumentException(
                "CashDevice constructor called with nulls.  Bad."
            );
        }
        this.file = file;

        // TODO
        deviceId = "";
        hasNorm = false;
        deviceIp = "";
        normIp = Optional.empty();
    }
}
