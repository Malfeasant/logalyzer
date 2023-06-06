package us.malfeasant.logalyzer;

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
}
