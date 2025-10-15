package us.malfeasant.logalyzer.scanners;

import us.malfeasant.logalyzer.S4LogFile;

/**
 * Scans a logfile for timestamp weirdness- longish gaps, backward jumps...
 */
public class Timestamp {
    private final S4LogFile file;
    public Timestamp(S4LogFile file) {
        this.file = file;
    }

    
}
