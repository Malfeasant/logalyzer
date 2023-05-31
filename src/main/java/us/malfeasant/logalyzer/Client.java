package us.malfeasant.logalyzer;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * Some log files can have multiple clients- ShareOne most notably.  This class represents a client.
 */
public class Client {
    private final String name;

    public Client(String name) {
        if (name == null) throw new IllegalArgumentException("Client constructed with nulls.");
        this.name = name;
    }

    public static void setCellFactory(ListView<Client> list) {
        list.setCellFactory(c -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(item == null ? "" : item.name);
                }
            };
        });
    }
}
