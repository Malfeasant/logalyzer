package us.malfeasant.logalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.tinylog.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
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

    //private final ListView<Client> clientList = new ListView<>();
    //private final ListView<CashDevice> machineList = new ListView<>();
    private final TreeView<LogComponent> deviceTree = new TreeView<>();

    public App() {
        Label label = new Label(System.getProperty("java.version")); // TODO something more useful
        BorderPane pane = new BorderPane(label);
        //var leftBox = new VBox(clientList, machineList);
        pane.setLeft(deviceTree);
        pane.setOnDragOver(e -> handleDragOver(e));
        pane.setOnDragDropped(e -> handleDrop(e));
        pane.setTop(createMenu());
        scene = new Scene(pane);
        deviceTree.setCellFactory(LogComponent.getCellFactory());
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

        if (files != null && !files.isEmpty()) {
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

    private void open(List<File> files) {
        TreeItem<LogComponent> root = new TreeItem<LogComponent>(null);
        deviceTree.setRoot(root);
        deviceTree.setShowRoot(false);
        for (var f : files) {
            try {
                var logFile = new S4LogFile(f);
                TreeItem<LogComponent> fileItem = new TreeItem<LogComponent>(logFile);
                root.getChildren().add(fileItem);
                var clients = new HashMap<String, TreeItem<LogComponent>>();  // map of client name to its TreeItem
                try {
                    logFile.populateDevices((c, d) -> {
                        // Make sure client added only once
                        TreeItem<LogComponent> clientItem = clients.get(c);
                        if (clientItem == null) {
                            clientItem = new TreeItem<>(new Client(c));
                            clients.put(c, clientItem);
                            fileItem.getChildren().add(clientItem);
                        }
                        clientItem.getChildren().add(new TreeItem<LogComponent>(d));
/*                        c -> {
                            item.getChildren().add(new TreeItem<LogComponent>(c));
                        }, d -> {
                            //item.getChildren().add(new TreeItem<LogComponent>(d));
*/                        });
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
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}