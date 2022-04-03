package file_input.workers;

import javafx.scene.text.Text;
import model.Disk;

import java.io.File;
import java.util.concurrent.Callable;

public abstract class FileInputWorker implements Callable<String> {

    private Disk disk;

    private File file;

    private Text statusLabel;

    public FileInputWorker(Disk disk, File file, Text statusLabel) {
        this.disk = disk;
        this.file = file;
        this.statusLabel = statusLabel;
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

    public Text getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(Text statusLabel) {
        this.statusLabel = statusLabel;
    }
}
