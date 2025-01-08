package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

import org.tinylog.Logger;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

/**
 * Represents a single S4 log file- performs analysis and holds statistics
 * Should be able to handle files over a gig (it can happen)
 */
public class S4LogFile extends LogComponent {
    private final File file;

    public final IntegerProperty deviceCountProperty = new SimpleIntegerProperty();
    public final StringProperty versionProperty = new SimpleStringProperty();
    public final StringProperty coreProperty = new SimpleStringProperty();

    public S4LogFile(File file) throws FileNotFoundException {
        super(file.getName());

        this.file = file;
        if (!file.isFile()) {
            throw new FileNotFoundException("File " + file + " is not readable or does not exist.");
        }
        // TODO sanity checks? Make sure it's an S4 log file?
    }

    @Override
    public String prettyPrint() {
        return "File: " + file.getName();
    }

    /**
     * Gets things like the s4 version, which core is selected...  
     */
    void getServerInfo() {
        // To avoid bogging down the JavaFX event thread while the file is read and parsed,
        // we use a single worker thread.
        Exec.getService().submit(new Task<>() {
            @Override
            protected Integer call() throws FileNotFoundException, IOException {
                // First, get number of lines in file- should be relatively quick no matter how large...
            /*    long lineCount;
                try (var readFile = Files.lines(file.toPath(), StandardCharsets.US_ASCII)) {
                    lineCount = readFile.count();
                }
                Platform.runLater(() -> {
                    app.lineCountProperty.set(Long.toString(lineCount));
                });*/ // TODO takes too long- better way?  Or replace with file size?

                // Next, grab the S4 version and Core-
                int count = 0;  // No telling where in the file the version and core lines will be-
                // can easily be thousands of lines in- so we count matches...
                try (var readFile = Files.newBufferedReader(file.toPath(), StandardCharsets.US_ASCII)) {
                    for (var line = readFile.readLine(); line != null; line = readFile.readLine()) {
                        if (isCancelled() || Thread.interrupted()) return -1;
                        
                        if (line.contains("CFMS4Version is CFM S4")) {
                            var idx = line.indexOf("CFMS4Version is CFM S4") + 23;
                            var text = line.substring(idx).split(" ")[0]; // Dirty hack
                            // because we started adding spaces & identifier to log lines
                            // ca 23.something so can't just go 'til EOL...
                            Platform.runLater(() -> {
                                versionProperty.set(text);
                            });
                            count++;
                            Logger.debug("Got version {}", text);
                        } else if (line.contains("Core Application : ")) {
                            var start = line.indexOf("Core Application : ") + 19;
                            var end = line.indexOf("Core Provider : "); // + 16;
                            var text = line.substring(start, end).trim();   // sometimes ends w/ spaces...
                            Platform.runLater(() -> {
                                coreProperty.set(text);
                            });
                            count++;
                            Logger.debug("Got Core name {}", text);
                        }

                        if (count >= 2) break;   // don't want to keep reading if we have all we need...
                    }
                }
                return count;
            }
        });
    }

    void populateDevices(Consumer<DeviceLine> dlConsumer) throws IOException {
        // To avoid bogging down the JavaFX event thread while the file is read and parsed,
        // we use a single worker thread.
        Exec.getService().submit(new Task<>() {
            @Override
            protected Integer call() throws FileNotFoundException, IOException {
                int count = 0;
                int sinceLast = 0;  // keep track of how many lines we've seen without a device
                // since devices are listed near the beginning of the file, no need to read all the
                // way to the end- but, since there can be non-device lines in between, we can't
                // just quit after seeing lines without devices...
                try (var readFile = Files.newBufferedReader(file.toPath(), StandardCharsets.US_ASCII)) {
                    for (var line = readFile.readLine(); line != null; line = readFile.readLine()) {
                        if (isCancelled() || Thread.interrupted()) return -1;
                        
                        if (line.contains(", Device - ")) {
                            ++count;
                            var finalCount = count; // must be effectively final to pass to another thread...
                            var devLine = new DeviceLine(line);
                            // Pass it back to Event thread
                            Platform.runLater(() -> {
                                dlConsumer.accept(devLine);
                                deviceCountProperty.set(finalCount);
                            });
                        } else if (count > 0) {
                            sinceLast++;
                        }
                        if (sinceLast > 500) break;
                    }
                }

                Logger.debug("Added {} devices.", count);
                return count;
            }
        });
    }
}
