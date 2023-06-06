package us.malfeasant.logalyzer;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public abstract class LogComponent {
    protected enum Type {
        FILE, CLIENT, DEVICE;
    }

    protected final String name;
    protected final Type type;

    protected LogComponent(Type t, String n) {
        // This should never happen, but just to make sure-
        if (t == null) throw new NullPointerException(
            "Constructed " + LogComponent.class.getSimpleName() + " with a null type.");
        type = t;
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
