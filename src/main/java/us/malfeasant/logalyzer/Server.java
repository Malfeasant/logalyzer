package us.malfeasant.logalyzer;

import java.net.InetAddress;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

/**
 * Holds info about the S4 server- should only be one per log file...
 */
public class Server {
    private final String name;  // hostname (should remain constant)
    private String version;
    private final ListProperty<InetAddress> ipList;
    private final ListProperty<String> coreList;    // this can change- 
    
    public Server(String name) {
        this.name = name;
        ipList = new SimpleListProperty<>();
        coreList = new SimpleListProperty<>();
    }
}
