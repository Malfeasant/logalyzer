package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private final Scene scene;
    private Stage stage; // this is needed for modal dialogs...

    private final TreeView<LogComponent> deviceTree = new TreeView<>();

    // properties to display a header
    public final StringProperty versionProperty = new SimpleStringProperty();
    public final StringProperty coreProperty = new SimpleStringProperty();
    // and footer
    public final StringProperty lineCountProperty = new SimpleStringProperty();
    public final StringProperty deviceCountProperty = new SimpleStringProperty();

    public App() {
        BorderPane pane = new BorderPane();
        pane.setLeft(deviceTree);
        pane.setOnDragOver(e -> handleDragOver(e));
        pane.setOnDragDropped(e -> handleDrop(e));
        
        var versionLabel = new Label();
        versionLabel.textProperty().bind(
            Bindings.concat("S4 Version: ",
                Bindings.when(
                    Bindings.isNotNull(versionProperty))
                        .then(versionProperty)
                        .otherwise("No file.")));
        var coreLabel = new Label();
        coreLabel.textProperty().bind(
            Bindings.concat("Core: ",
                Bindings.when(
                    Bindings.isNotNull(coreProperty))
                        .then(coreProperty)
                        .otherwise("No file.")));

        var lineCountLabel = new Label();
        lineCountLabel.textProperty().bind(
            Bindings.concat("File lines: ",
                Bindings.when(
                    Bindings.isNotNull(lineCountProperty))
                        .then(lineCountProperty)
                        .otherwise("Coming soon..."))); // TODO takes too long...
        var deviceCountLabel = new Label();
        deviceCountLabel.textProperty().bind(
            Bindings.concat("Devices: ",
                Bindings.when(
                    Bindings.isNotNull(deviceCountProperty))
                        .then(deviceCountProperty)
                        .otherwise("No file.")));
                
        var header = new BorderPane();
        header.setLeft(versionLabel);
        header.setRight(coreLabel);
        var top = new VBox(createMenu(), header);
        pane.setTop(top);

        var footer = new BorderPane();
        footer.setLeft(lineCountLabel);
        footer.setRight(deviceCountLabel);
        pane.setBottom(footer);

        scene = new Scene(pane);
        deviceTree.setCellFactory(LogComponent.getCellFactory());
    }

    private void closeAll() {
        deviceTree.setRoot(null);   // I think this will free all items?
        
        versionProperty.unbind();   // unbind leaves it with last value...
        versionProperty.set(null);  // null property triggers "no file open" label...
        coreProperty.unbind();
        coreProperty.set(null);
        deviceCountProperty.unbind();
        deviceCountProperty.set(null);
        lineCountProperty.unbind();
        lineCountProperty.set(null);

        // TODO anything else?
    }

    private MenuBar createMenu() {
        MenuItem open = new MenuItem("Open...");
        MenuItem close = new MenuItem("Close");
        MenuItem exit = new MenuItem("Exit");
        Menu file = new Menu("File");
        file.getItems().addAll(open, close, new SeparatorMenuItem(), exit);
        MenuBar menuBar = new MenuBar(file);
        open.setOnAction(e -> showChooser(e));
        close.setOnAction(e -> closeAll());
        exit.setOnAction(e -> shutdown());
        return menuBar;
    }

    private void showChooser(ActionEvent event) {
        var chooser = new FileChooser();
        chooser.setTitle("Open S4 Log File(s)");
        var files = chooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            open(files);
        }
    }

    private void handleDragOver(DragEvent event) {
        var db = event.getDragboard();
        if (hasFiles(db)) {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        }
    }

    private boolean hasFiles(Dragboard db) {
        var anyFiles = false;
        if (db.hasFiles()) {
            for (var f : db.getFiles()) {
                if (f.isDirectory()) return false;
                else anyFiles = true;
            }
        }
        return anyFiles;
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

    private void open(List<File> files) {
        // First, unload whatever we have loaded-
        closeAll();
        // then, start building new tree...
        var root = new LogItem(null);
        deviceTree.setRoot(root);
        deviceTree.setShowRoot(false);
        for (var f : files) {
            try {
                var logFile = new S4LogFile(f);
                deviceCountProperty.bind(Bindings.convert(logFile.deviceCountProperty));
                versionProperty.bind(logFile.versionProperty);
                coreProperty.bind(logFile.coreProperty);

                var fileItem = new LogItem(logFile);
                root.getChildren().add(fileItem);
                var clients = new HashMap<String, LogItem>();  // map of client name to its TreeItem
                try {
                    // setup header- note, in the case of multiple files, last one wins.  TODO?
                    logFile.getServerInfo();
                    logFile.populateDevices(line -> {
                        // Make sure client added only once
                        var clientItem = clients.get(line.client);
                        if (clientItem == null) {
                            clientItem = new LogItem(new Client(line.client));
                            clients.put(line.client, clientItem);
                            fileItem.getChildren().add(clientItem);
                        }
                        clientItem.getChildren().add(new LogItem(new CashDevice(line)));
                    });
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                // TODO anything?
                new Alert(AlertType.ERROR, 
                    "File " + f + " could not be opened.").showAndWait();
            }
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage; // this is needed for dialogs...
        stage.setOnCloseRequest(e -> {
            shutdown();
        });
        stage.setScene(scene);
        stage.show();
    }

    private void shutdown() {
        Logger.info("Shutting down.");
        Exec.getService().shutdownNow();
        stage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}