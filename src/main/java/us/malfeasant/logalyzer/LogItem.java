package us.malfeasant.logalyzer;

import javafx.scene.control.TreeItem;

public class LogItem extends TreeItem<LogComponent> {
    public LogItem(LogComponent value) {
        super(value);
    }
}
