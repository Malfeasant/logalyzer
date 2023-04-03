package us.malfeasant.logalyzer;

import java.util.Optional;

public class CashDevice {
    private final S4LogFile file;
    private final String deviceId;
    private final boolean hasNorm;
    private final String deviceIp;
    private final Optional<String> normIp;

    CashDevice(S4LogFile file, String deviceLine) {
        if (file == null || deviceLine == null) {
            throw new IllegalArgumentException(
                "CashDevice constructor called with nulls.  Bad."
            );
        }
        this.file = file;

        
    }
}
