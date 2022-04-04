package file_input;

import cruncher.CruncherComponent;
import javafx.scene.text.Text;
import model.Directory;
import model.Disk;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public abstract class FileInputComponent implements Runnable {

    private final Disk disk;

    private volatile boolean isStarted;
    private volatile boolean isRunning;
    private volatile boolean shouldExit;

    private final int sleepTime;

    private final ExecutorService threadPool;

    private final List<Directory> directories = new CopyOnWriteArrayList<>();
    private final List<CruncherComponent> connectedCrunchers = new CopyOnWriteArrayList<>();

    private final Text statusLabel;

    public FileInputComponent(Disk disk, ExecutorService threadPool, Text statusLabel, int sleepTime) {
        this.disk = disk;
        this.threadPool = threadPool;
        this.statusLabel = statusLabel;
        this.sleepTime = sleepTime;
    }

    public synchronized void start() {
        if (!isStarted) {
            new Thread(this).start();
            isStarted = true;
        }

        setRunning(true);
        notify();
    }

    public synchronized void pause() {
        setRunning(false);
        notify();
    }

    public Disk getDisk() {
        return disk;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    public boolean shouldExit() {
        return shouldExit;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public List<Directory> getDirectories() {
        return directories;
    }

    public List<CruncherComponent> getConnectedCrunchers() {
        return connectedCrunchers;
    }

    public Text getStatusLabel() {
        return statusLabel;
    }

    public void connectCruncherComponent(CruncherComponent cruncherComponent) {
        this.connectedCrunchers.add(cruncherComponent);
    }

    public void disconnectCruncherComponent(CruncherComponent cruncherComponent) {
        this.connectedCrunchers.add(cruncherComponent);
    }
}
