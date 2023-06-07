package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

import org.tinylog.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Represents a single S4 log file- performs analysis and holds statistics
 * Should be able to handle files over a gig (it can happen)
 */
public class S4LogFile extends LogComponent {
    private final File file;

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

    void populateDevices(Consumer<DeviceLine> dlConsumer) throws IOException {
        // To avoid bogging down the JavaFX event thread while the file is read and parsed,
        // we use a single worker thread.
        Exec.getService().submit(new Task<>() {
            @Override
            protected Integer call() throws FileNotFoundException, IOException {
                int count = 0;
//                try (var readFile = new RandomAccessFile(file, "r")) {
                try (var readFile = Files.newBufferedReader(file.toPath(), StandardCharsets.US_ASCII)) {
                    for (var line = readFile.readLine(); line != null; line = readFile.readLine()) {
                        if (isCancelled() || Thread.interrupted()) return -1;
                        
                        if (line.contains(", Device - ")) {
                            var devLine = new DeviceLine(line);
                            // Pass it back to Event thread
                            Platform.runLater(() -> {
                                dlConsumer.accept(devLine);
                            });
                            ++count;
                        }
                    }
                }
                Logger.debug("Added {} devices.", count);
                return count;
            }
        });
    }
}
