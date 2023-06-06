package us.malfeasant.logalyzer;

/**
 * Some log files can have multiple clients- ShareOne most notably.  This class represents a client.
 */
public class Client extends LogComponent {

    public Client(String name) {
        super(Type.CLIENT, name);
    }

    @Override
    public String prettyPrint() {
        return "Client: " + name;
    }
}
