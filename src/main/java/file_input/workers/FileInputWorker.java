package file_input.workers;

import model.Disk;

import java.io.File;
import java.util.concurrent.Callable;

public abstract class FileInputWorker implements Callable<String> {

    private Disk disk;

    private File file;

    public FileInputWorker(Disk disk, File file) {
        this.disk = disk;
        this.file = file;
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
