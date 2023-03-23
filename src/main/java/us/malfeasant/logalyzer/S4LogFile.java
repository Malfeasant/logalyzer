package us.malfeasant.logalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

/**
 * Represents a single S4 log file- performs analysis and holds statistics
 * Should be able to handle files over a gig (it can happen)
 */
public class S4LogFile {
    private final BufferedReader reader;

    public S4LogFile(Path file) throws IOException {
        if (!Files.isReadable(file)) {
            Logger.error("File " + file + " is not readable.");
            throw new IOException("File is not readable.");
        }
        // TODO sanity checks? Make sure it's an S4 log file?
        reader = Files.newBufferedReader(file);
        var lc = reader.lines().count();
        Logger.info("Opened file " + file + " containing " + lc + " lines.");
    }
}
