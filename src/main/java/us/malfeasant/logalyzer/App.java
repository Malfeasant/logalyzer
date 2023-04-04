package us.malfeasant.logalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private final Scene scene;
    private Stage stage; // this is needed for modal dialogs...

    public App() {
        Label label = new Label(System.getProperty("java.version"));
        BorderPane pane = new BorderPane(label);
        pane.setOnDragOver(e -> handleDragOver(e));
        pane.setOnDragDropped(e -> handleDrop(e));
        pane.setTop(createMenu());
        scene = new Scene(pane);
    }

    private MenuBar createMenu() {
        MenuItem open = new MenuItem("Open...");
        Menu file = new Menu("File");
        file.getItems().addAll(open);
        MenuBar menuBar = new MenuBar(file);
        open.setOnAction(e -> showChooser(e));
        return menuBar;
    }

    private void showChooser(ActionEvent event) {
        var chooser = new FileChooser();
        chooser.setTitle("Open S4 Log File(s)");
        var files = chooser.showOpenMultipleDialog(stage);

        if (!files.isEmpty()) {
            open(files);
        }
    }

    private void handleDragOver(DragEvent event) {
        var db = event.getDragboard();
        if (db.hasFiles() || db.hasString()) {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        }
    }

    private void handleDrop(DragEvent event) {
        var db = event.getDragboard();
        if (db.hasFiles()) {
            event.setDropCompleted(true);
            event.consume();
            Logger.info("Files dropped: " + db.getFiles());
            open(db.getFiles());
        } // TODO else if? Can we drop Strings? Folder?
        else Logger.warn("Can't handle this drop: " + db.getContentTypes());
    }

    private List<S4LogFile> logFiles;
    private void open(List<File> files) {
        logFiles = new ArrayList<>();
        for (var file : files) {
            S4LogFile s4log = null;
            try {
                Logger.debug("Adding file " + file);
                s4log = new S4LogFile(file.toPath());
            } catch (IOException e) {
                // TODO: handle exception- probably just log it and reset UI?
            }
            if (s4log != null) {
                logFiles.add(s4log);
                s4log.populateDevices();
            }
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage; // this is needed for dialogs...
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}