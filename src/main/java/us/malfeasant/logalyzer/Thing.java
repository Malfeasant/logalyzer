package us.malfeasant.logalyzer;

public abstract class Thing {
    protected enum Type {
        FILE, CLIENT, DEVICE;
    }

    protected final Type type;

    protected Thing(Type t) {
        // This should never happen, but just to make sure-
        if (t == null) throw new NullPointerException(
            "Constructed " + Thing.class.getSimpleName() + " with a null type.");
        type = t;
    }
}
