package manager;

import cruncher.CruncherComponent;
import cruncher.CruncherComponentCounterImpl;
import file_input.FileInputComponent;
import file_input.FileInputComponentAsciiImpl;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Directory;
import model.Disk;
import output.OutputComponent;
import output.OutputComponentCacheImpl;
import view.MainView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class PipelineManager {

    private static PipelineManager instance;

    private MainView view;

    private final AtomicBoolean acceptingNewWork = new AtomicBoolean(true);

    private final ExecutorService fileInputThreadPool = Executors.newCachedThreadPool();
    private final ForkJoinPool cruncherForkJoinPool = ForkJoinPool.commonPool();
    private final ExecutorService outputThreadPool = Executors.newCachedThreadPool();

    private final List<FileInputComponent> fileInputComponents = new ArrayList<>();
    private final List<CruncherComponent> cruncherComponents = new ArrayList<>();
    private final List<OutputComponent> outputComponents = new ArrayList<>();

    public void addNewFileInputComponent(Disk disk, Text statusLabel) {
        FileInputComponent fileInputComponent = new FileInputComponentAsciiImpl(disk, fileInputThreadPool, statusLabel);
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
        FileInputComponent fip = getFileInputComponent(disk);
        fip.setShouldExit(true);

        fileInputComponents.remove(fip);
    }

    public void addDirectoryToFileInputComponent(Disk disk, Directory directory) {
        FileInputComponent fip = getFileInputComponent(disk);
        fip.getDirectories().add(directory);
    }

    public void removeDirectoryFromFileInputComponent(Disk disk, Directory directory) {
        FileInputComponent fip = getFileInputComponent(disk);
        fip.getDirectories().remove(directory);
    }

    public void addNewCruncherComponent(int arity, Text statusLabel) {
        CruncherComponent cruncherComponent = new CruncherComponentCounterImpl(arity, cruncherForkJoinPool, statusLabel);
        cruncherComponents.add(cruncherComponent);
    }

    public void removeCruncherComponent(int arity) {
        CruncherComponent cruncherComponent = getCruncherComponent(arity);
        cruncherComponent.stopCruncher();

        for (final FileInputComponent fip : fileInputComponents) {
            fip.disconnectCruncherComponent(cruncherComponent);
        }

        cruncherComponents.remove(cruncherComponent);
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

    public void shutDownApplication(Stage shutDownStage) {
        new Thread(() -> {
            acceptingNewWork.set(false);

            this.fileInputThreadPool.shutdownNow();
            this.cruncherForkJoinPool.shutdownNow();
            this.outputThreadPool.shutdownNow();

            while (true) {
                boolean inputWorking = ((ThreadPoolExecutor) fileInputThreadPool).getActiveCount() > 0;
                boolean cruncherWorking = cruncherForkJoinPool.getActiveThreadCount() > 0;
                boolean outputWorking = ((ThreadPoolExecutor) outputThreadPool).getActiveCount() > 0;

                if (!inputWorking && !cruncherWorking && !outputWorking)
                break;
            }

            Platform.runLater(shutDownStage::close);
        }).start();
    }

    public void terminateApplication() {
        Platform.runLater(() -> {
            view.showOutOfMemoryError();
        });

        new Thread(() -> {
            this.fileInputThreadPool.shutdownNow();
            this.cruncherForkJoinPool.shutdownNow();
            this.outputThreadPool.shutdownNow();
        }).start();
    }

    public void setView(MainView view) {
        this.view = view;
    }

    public AtomicBoolean getAcceptingNewWork() {
        return acceptingNewWork;
    }

    public static PipelineManager getInstance() {
        if (instance == null)
            instance = new PipelineManager();

        return instance;
    }
}
