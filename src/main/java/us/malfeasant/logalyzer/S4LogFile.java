package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.tinylog.Logger;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

/**
 * Represents a single S4 log file- performs analysis and holds statistics
 * Should be able to handle files over a gig (it can happen)
 */
public class S4LogFile {
    private final File file;

    final ListProperty<CashDevice> devices = new SimpleListProperty<>();
    
    public S4LogFile(File file) throws FileNotFoundException {
        this.file = file;
        if (!file.isFile()) {
            throw new FileNotFoundException("File " + file + " is not readable or does not exist.");
        }
        // TODO sanity checks? Make sure it's an S4 log file?
    }

    void populateDevices() throws IOException {
        try (var raf = new RandomAccessFile(file, "r")) {
            for (var line = raf.readLine(); line != null; line = raf.readLine()) {
                if (line.contains(", Device - ")) {
                    devices.add(new CashDevice(this, line));
                    Logger.debug("Added device line: {}", line);
                }
            }
        }

        Logger.debug("Added " + devices.size() + " devices.");
    }
}
