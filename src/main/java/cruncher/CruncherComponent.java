package cruncher;

import file_input.FileInputResult;
import output.OutputComponent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CruncherComponent implements Runnable {

    private final int arity;

    private final ForkJoinPool forkJoinPool;

    private final BlockingQueue<FileInputResult> receivedFileInputData;
    private final List<OutputComponent> connectedOutputs;

    public CruncherComponent(int arity, ForkJoinPool forkJoinPool) {
        this.arity = arity;
        this.forkJoinPool = forkJoinPool;
        this.receivedFileInputData = new LinkedBlockingQueue<>();
        this.connectedOutputs = new CopyOnWriteArrayList<>();

        new Thread(this).start();
    }

    public void addToQueue(FileInputResult fileInputResult) {
        try {
            this.receivedFileInputData.put(fileInputResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
