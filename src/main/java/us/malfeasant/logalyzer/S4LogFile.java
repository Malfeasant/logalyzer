package us.malfeasant.logalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.tinylog.Logger;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

/**
 * Represents a single S4 log file- performs analysis and holds statistics
 * Should be able to handle files over a gig (it can happen)
 */
public class S4LogFile {
    private final BufferedReader reader;

    final ListProperty<CashDevice> devices = new SimpleListProperty<>();
    
    public S4LogFile(Path file) throws IOException {
        if (!Files.isReadable(file)) {
            Logger.error("File " + file + " is not readable.");
            throw new IOException("File is not readable.");
        }
        // TODO sanity checks? Make sure it's an S4 log file?
        // charset arg needed because default is utf-8, so 
        // MalformedInputException is a possibility- ISO-8859 will not do that.
        reader = Files.newBufferedReader(file, Charset.forName("ISO-8859-1"));
        Logger.info("Opened file " + file + 
            " containing " + reader.lines().count() + " lines.");
    }

    Stream<String> lines() {
        return reader.lines();
    }

    void populateDevices() {
        lines().filter(line -> line.contains(", Device - "))
            .forEach(line -> devices.add(new CashDevice(this, line)));
        Logger.debug("Added " + devices.size() + " devices.");
        Logger.debug("First line: " + lines().findFirst().get());
    }
}
