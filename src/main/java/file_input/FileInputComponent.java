package file_input;

import cruncher.CruncherComponent;
import model.Directory;
import model.Disk;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public abstract class FileInputComponent implements Runnable {

    private final Disk disk;

    private final ExecutorService threadPool;

    private List<Directory> directories = new CopyOnWriteArrayList<>();
    private List<CruncherComponent> connectedCrunchers = new CopyOnWriteArrayList<>();

    public FileInputComponent(Disk disk, ExecutorService threadPool) {
        this.disk = disk;
        this.threadPool = threadPool;
    }

    public void start() {
        new Thread(this).start();
    }

    public Disk getDisk() {
        return disk;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public List<Directory> getDirectories() {
        return directories;
    }

    public void setDirectories(List<Directory> directories) {
        this.directories = directories;
    }

    public List<CruncherComponent> getConnectedCrunchers() {
        return connectedCrunchers;
    }

    public void connectCruncherComponent(CruncherComponent cruncherComponent) {
        this.connectedCrunchers.add(cruncherComponent);
    }

    public void disconnectCruncherComponent(CruncherComponent cruncherComponent) {
        this.connectedCrunchers.add(cruncherComponent);
    }
}
