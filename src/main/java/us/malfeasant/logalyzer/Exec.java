package us.malfeasant.logalyzer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Exec {
    /**
     * There can be only one.  This is ugly, but as long as it's not public, meh...
     * Makes sure only one service gets created, avoids the need to worry about the
     * gory detils- Enum instances are guaranteed to be unique.
     */
    private enum Singleton {
        SINGLETON;

        private ExecutorService service = Executors.newSingleThreadExecutor();
    }

    public static ExecutorService getService() {
        return Singleton.SINGLETON.service;
    }

    private Exec() {}; // not meant to be instantiated.
}
