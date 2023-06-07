package us.malfeasant.logalyzer;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public abstract class LogComponent {

    protected final String name;

    protected LogComponent(String n) {
        name = n;
    }

    public abstract String prettyPrint();

    public static Callback<TreeView<LogComponent>, TreeCell<LogComponent>> getCellFactory()  {
        return (view -> {
            return new TreeCell<LogComponent>() {
                @Override
                protected void updateItem(LogComponent item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(item == null ? "" : item.prettyPrint());
                }
            };
        });
    }
}
