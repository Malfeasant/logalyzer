package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
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
        super(Type.FILE, file.getName());

        this.file = file;
        if (!file.isFile()) {
            throw new FileNotFoundException("File " + file + " is not readable or does not exist.");
        }
        // TODO sanity checks? Make sure it's an S4 log file?
    }

    void populateDevices(Consumer<Client> clients, Consumer<CashDevice> devices)
        throws IOException {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                int count = 0;
                try (var raf = new RandomAccessFile(file, "r")) {
                    var clientSet = new HashSet<>();
                    for (var line = raf.readLine(); line != null; line = raf.readLine()) {
                        if (line.contains(", Device - ")) {
                            var devLine = new DeviceLine(line);
                            var client = devLine.client;
                            if (clientSet.add(client)) {
                                clients.accept(new Client(client));
                            }
                            var dev = new CashDevice(devLine);
                            Platform.runLater(() -> {
                                devices.accept(dev);
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

    }
}
