package cruncher;

import file_input.FileInputResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import output.OutputComponent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CruncherComponent extends Thread {

    private final int arity;

    private final ForkJoinPool forkJoinPool;

    private final BlockingQueue<FileInputResult> receivedFileInputData;
    private final List<OutputComponent> connectedOutputs;

    private final ObservableList<String> filesInCrunchingProcess;

    private final Text statusLabel;

    public CruncherComponent(int arity, ForkJoinPool forkJoinPool, Text statusLabel) {
        this.arity = arity;
        this.forkJoinPool = forkJoinPool;
        this.statusLabel = statusLabel;
        this.receivedFileInputData = new LinkedBlockingQueue<>();
        this.connectedOutputs = new CopyOnWriteArrayList<>();
        this.filesInCrunchingProcess = FXCollections.observableArrayList();

        filesInCrunchingProcess.addListener((ListChangeListener<String>) change -> {
            Platform.runLater(() -> {
                StringBuilder activeFiles = new StringBuilder();

                for (final String file : filesInCrunchingProcess) {
                    activeFiles.append(file).append("\n");
                }

                if (activeFiles.length() > 0)
                    statusLabel.setText("Crunching:\n" + activeFiles);
                else
                    statusLabel.setText("");
            });
        });

        start();
    }

    public void addToQueue(FileInputResult fileInputResult) {
        try {
            this.receivedFileInputData.put(fileInputResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopCruncher() {
        interrupt();
    }

    protected BlockingQueue<FileInputResult> getReceivedFileInputData() {
        return receivedFileInputData;
    }

    public int getArity() {
        return arity;
    }

    public ForkJoinPool getForkJoinPool() {
        return forkJoinPool;
    }

    public Text getStatusLabel() {
        return statusLabel;
    }

    public ObservableList<String> getFilesInCrunchingProcess() {
        return filesInCrunchingProcess;
    }

    public void connectOutputComponent(OutputComponent op) {
        this.connectedOutputs.add(op);
    }

    public void disconnectOutputComponent(OutputComponent op) {
        this.connectedOutputs.remove(op);
    }

    public List<OutputComponent> getConnectedOutputs() {
        return connectedOutputs;
    }
}
