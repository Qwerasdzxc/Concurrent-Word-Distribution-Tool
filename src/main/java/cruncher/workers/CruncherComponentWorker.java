package cruncher.workers;

import file_input.FileInputResult;

import java.util.Map;
import java.util.concurrent.RecursiveTask;

public abstract class CruncherComponentWorker extends RecursiveTask<Map<String, Long>> {

    protected final int arity;

    protected final FileInputResult fileInputResult;

    protected CruncherComponentWorker(int arity, FileInputResult fileInputResult) {
        this.arity = arity;
        this.fileInputResult = fileInputResult;
    }

    public int getArity() {
        return arity;
    }

    public FileInputResult getFileInputResult() {
        return fileInputResult;
    }
}
