package file_input;

import cruncher.CruncherComponent;
import file_input.workers.FileInputWorkerAsciiImpl;
import javafx.scene.text.Text;
import manager.PipelineManager;
import model.Directory;
import model.Disk;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FileInputComponentAsciiImpl extends FileInputComponent {

    private final Map<String, Long> filesRead = new ConcurrentHashMap<>();

    private static final String FILE_EXTENSION = ".txt";

    public FileInputComponentAsciiImpl(Disk disk, ExecutorService threadPool, Text statusLabel) {
        super(disk, threadPool, statusLabel);
    }

    @Override
    public void run() {
        while (!shouldExit()) {
            System.out.println("Started scan");

            for (Directory value : getDirectories()) {
                File directory = value.getDirectory();

                try {
                    readDirectory(directory);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            System.out.println("Stopped scan");

            try {
                synchronized (this) {
                    if (isRunning())
                        wait(2000);
                    else
                        wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("FileInput shut down.");
    }

    private void readDirectory(File directory) throws InterruptedException {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                readDirectory(file);
            } else if (file.getName().endsWith(FILE_EXTENSION)) {
                if (filesRead.containsKey(file.getAbsolutePath())) {
                    if (filesRead.get(file.getAbsolutePath()) < file.lastModified()) {
                        createFileInputWorker(file);
                    }
                } else {
                    createFileInputWorker(file);
                }
            }
        }
    }

    private void createFileInputWorker(File file) {
        if (!PipelineManager.getInstance().getAcceptingNewWork().get())
            return;

        Future<String> fileData = getThreadPool().submit(new FileInputWorkerAsciiImpl(getDisk(), file, getStatusLabel()));
        try {
            forwardDataToCruncherComponent(new FileInputResult(file.getName(), fileData.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        filesRead.put(file.getAbsolutePath(), file.lastModified());
    }

    private void forwardDataToCruncherComponent(FileInputResult fileInputResult) {
        for (CruncherComponent cruncherComponent : getConnectedCrunchers())
            cruncherComponent.addToQueue(fileInputResult);
    }
}
