package cruncher.workers;

import file_input.FileInputResult;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.concurrent.RecursiveTask;

public abstract class CruncherComponentWorker extends RecursiveTask<Map<String, Long>> {

    protected final int arity;

    protected final FileInputResult fileInputResult;

    protected final ObservableList<String> filesInCrunchingProcess;

    protected CruncherComponentWorker(int arity, FileInputResult fileInputResult, ObservableList<String> filesInCrunchingProcess) {
        this.arity = arity;
        this.fileInputResult = fileInputResult;
        this.filesInCrunchingProcess = filesInCrunchingProcess;
    }

    public int getArity() {
        return arity;
    }

    public FileInputResult getFileInputResult() {
        return fileInputResult;
    }
}
