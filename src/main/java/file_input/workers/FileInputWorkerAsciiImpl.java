package file_input.workers;

import model.Disk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileInputWorkerAsciiImpl extends FileInputWorker {

    public FileInputWorkerAsciiImpl(Disk disk, File file) {
        super(disk, file);
    }

    @Override
    public String call() throws Exception {
        synchronized (getDisk()) {
            System.out.println(getFile().getName());
            try {
                return new String(Files.readAllBytes(Paths.get(getFile().getPath())));
            } catch (OutOfMemoryError e) {
                // TODO: Stop app
                System.out.println("No memory");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
