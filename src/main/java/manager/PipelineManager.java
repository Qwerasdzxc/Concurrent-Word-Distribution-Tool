package manager;

import cruncher.CruncherComponent;
import cruncher.CruncherComponentCounterImpl;
import file_input.FileInputComponent;
import file_input.FileInputComponentAsciiImpl;
import javafx.collections.ObservableList;
import model.Directory;
import model.Disk;
import output.OutputComponent;
import output.OutputComponentCacheImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class PipelineManager {

    private static PipelineManager instance;

    private final ExecutorService fileInputThreadPool = Executors.newCachedThreadPool();
    private final ForkJoinPool cruncherForkJoinPool = ForkJoinPool.commonPool();
    private final ExecutorService outputThreadPool = Executors.newCachedThreadPool();

    private final List<FileInputComponent> fileInputComponents = new ArrayList<>();
    private final List<CruncherComponent> cruncherComponents = new ArrayList<>();
    private final List<OutputComponent> outputComponents = new ArrayList<>();

    public void addNewFileInputComponent(Disk disk) {
        FileInputComponent fileInputComponent = new FileInputComponentAsciiImpl(disk, fileInputThreadPool);
        fileInputComponents.add(fileInputComponent);
    }

    public FileInputComponent getFileInputComponent(Disk disk) {
        for (FileInputComponent fip : fileInputComponents) {
            if (fip.getDisk() == disk)
                return fip;
        }

        return null;
    }

    public void removeFileInputComponent(Disk disk) {
        fileInputComponents.removeIf(fileInputComponent -> fileInputComponent.getDisk() == disk);
    }

    public void addDirectoryToFileInputComponent(Disk disk, Directory directory) {
        FileInputComponent fip = getFileInputComponent(disk);
        fip.getDirectories().add(directory);
    }

    public void removeDirectoryFromFileInputComponent(Disk disk, Directory directory) {
        FileInputComponent fip = getFileInputComponent(disk);
        fip.getDirectories().remove(directory);
    }

    public void addNewCruncherComponent(int arity) {
        CruncherComponent cruncherComponent = new CruncherComponentCounterImpl(arity, cruncherForkJoinPool);
        cruncherComponents.add(cruncherComponent);
    }

    public void removeCruncherComponent(int arity) {
        cruncherComponents.removeIf(cruncherComponent -> cruncherComponent.getArity() == arity);
    }

    public CruncherComponent getCruncherComponent(int arity) {
        for (CruncherComponent cc : cruncherComponents) {
            if (cc.getArity() == arity)
                return cc;
        }

        return null;
    }

    public void attachFileInputComponentToCruncherComponent(Disk disk, int arity) {
        FileInputComponent fip = getFileInputComponent(disk);
        CruncherComponent cc = getCruncherComponent(arity);

        fip.connectCruncherComponent(cc);
    }

    public void detachFileInputComponentFromCruncherComponent(Disk disk, int arity) {
        FileInputComponent fip = getFileInputComponent(disk);
        CruncherComponent cc = getCruncherComponent(arity);

        fip.disconnectCruncherComponent(cc);
    }

    public void addNewOutputComponent(ObservableList<String> outputResults) {
        OutputComponent outputComponent = new OutputComponentCacheImpl(outputThreadPool, outputResults);
        outputComponents.add(outputComponent);
    }

    public OutputComponent getOutputComponent() {
        return outputComponents.get(0);
    }

    public void attachCruncherComponentToOutputComponent(int arity) {
        CruncherComponent cc = getCruncherComponent(arity);
        OutputComponent op = getOutputComponent();

        cc.connectOutputComponent(op);
    }

    public void detachCruncherComponentFromOutputComponent(int arity) {
        CruncherComponent cc = getCruncherComponent(arity);
        OutputComponent op = getOutputComponent();

        cc.disconnectOutputComponent(op);
    }

    public static PipelineManager getInstance() {
        if (instance == null)
            instance = new PipelineManager();

        return instance;
    }
}
