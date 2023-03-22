package us.malfeasant.logalyzer;

public class Launch {
    // This is a hack- rather than starting the JavaFX app directly,
    // we launch this class that then calls main on the App class.
    // Otherwise, JavaFX subsystem doesn't get invoked properly.
    // Only needed because we're packaging into a fat jar.
    public static void main(String[] args) {
        App.main(args);
    }
}
