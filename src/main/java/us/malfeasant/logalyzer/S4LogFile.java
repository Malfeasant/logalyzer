package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.BiConsumer;

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
        super(Type.FILE, file.getName());

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

    void populateDevices(BiConsumer<String, CashDevice> clientDevices)
        throws IOException {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                int count = 0;
                try (var raf = new RandomAccessFile(file, "r")) {
                    for (var line = raf.readLine(); line != null; line = raf.readLine()) {
                        if (line.contains(", Device - ")) {
                            var devLine = new DeviceLine(line);
                            var client = devLine.client;
                            var dev = new CashDevice(devLine);
                            Platform.runLater(() -> {
                                clientDevices.accept(client, dev);
                            });
                            ++count;
                        }
                    }
                }
                Logger.debug("Added {} devices.", count);
                return count;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        // TODO- an Executor?  Seems like a service would be best- keep the same thread running,
        // just the task changes...
        // in fact as is right now this is ripe for race conditions
    }
}
