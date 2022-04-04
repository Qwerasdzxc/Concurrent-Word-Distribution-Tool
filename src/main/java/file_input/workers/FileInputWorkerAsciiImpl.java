package file_input.workers;

import javafx.application.Platform;
import javafx.scene.text.Text;
import manager.PipelineManager;
import model.Disk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileInputWorkerAsciiImpl extends FileInputWorker {

    public FileInputWorkerAsciiImpl(Disk disk, File file, Text statusLabel) {
        super(disk, file, statusLabel);
    }

    @Override
    public String call() {
        synchronized (getDisk()) {
            Platform.runLater(() -> {
                getStatusLabel().setText("Reading: " + getFile().getName());
            });
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(getFile().getPath()));

                Platform.runLater(() -> {
                    getStatusLabel().setText("Idle");
                });

                return new String(bytes);
            } catch (OutOfMemoryError e) {
                PipelineManager.getInstance().terminateApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
