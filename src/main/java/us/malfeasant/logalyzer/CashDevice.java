package us.malfeasant.logalyzer;

import org.tinylog.Logger;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class CashDevice {
    private final S4LogFile file;
    private final String deviceId;
    private final String deviceIp;

    /**
     * Pass in the log file, and the line from that file which details
     * this device.
     */
    CashDevice(S4LogFile file, String deviceLine) {
        if (file == null || deviceLine == null) {
            throw new IllegalArgumentException(
                "CashDevice constructor called with nulls.  Bad."
            );
        }
        this.file = file;
        var clientIdx = deviceLine.indexOf("Client - ");
        var deviceIdx = deviceLine.indexOf(", Device - ");
        var typeIdx = deviceLine.indexOf(", Type - ");
        var ipIdx = deviceLine.indexOf(", IP - ");
        
        // TODO fix this up
        var client = deviceLine.substring(clientIdx + 9, deviceIdx);
        deviceId = deviceLine.substring(deviceIdx + 11, typeIdx);
        deviceIp = deviceLine.substring(ipIdx + 7);   // TODO separate IP & port?

        Logger.debug("New device for client {} - ID: {} - at IP address {}",
            client, deviceId, deviceIp);
    }

    public static void setCellFactory(ListView<CashDevice> list) {
        list.setCellFactory(c -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(CashDevice item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(item == null ? "" : item.deviceId);
                }
            };
        });
    }
}
