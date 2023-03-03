package jp.mincra.discord;

import java.util.HashMap;
import java.util.Map;

public class ThreadManager {
    private final Map<String, Thread> idToThread = new HashMap<>();

    public void addThread(Thread thread) {
        idToThread.put(thread.getChannel().getId(), thread);

    }

    public boolean isRegistered(String id) {
        return idToThread.containsKey(id);
    }

    public Thread getThread(String id) {
        return idToThread.get(id);
    }
}
