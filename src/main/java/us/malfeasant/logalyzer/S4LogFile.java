package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.tinylog.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Represents a single S4 log file- performs analysis and holds statistics
 * Should be able to handle files over a gig (it can happen)
 */
public class S4LogFile {
    private final File file;

    final ObservableList<Client> clients = FXCollections.observableArrayList();
    final ObservableList<CashDevice> devices = FXCollections.observableArrayList();

    public S4LogFile(File file) throws FileNotFoundException {
        this.file = file;
        if (!file.isFile()) {
            throw new FileNotFoundException("File " + file + " is not readable or does not exist.");
        }
        // TODO sanity checks? Make sure it's an S4 log file?
    }

    void populateDevices() throws IOException {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                int count = 0;
                try (var raf = new RandomAccessFile(file, "r")) {
                    for (var line = raf.readLine(); line != null; line = raf.readLine()) {
                        if (line.contains(", Device - ")) {
                            // TODO handle clients
                            var dev = new CashDevice(S4LogFile.this, line);
                            Platform.runLater(() -> {
                                devices.add(dev);
                            });
                            ++count;
                        }
                    }
                }
                return count;
            }
        };
        task.setOnSucceeded(e -> {
            Logger.debug("Added " + devices.size() + " devices.");
        });
        devices.addListener((ListChangeListener.Change<? extends CashDevice> c) -> {
            c.next();
            Logger.debug(c.getAddedSubList());
        });
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        // TODO- an Executor?  Seems like a service would be best- keep the same thread running,
        // just the task changes...

    }
}
