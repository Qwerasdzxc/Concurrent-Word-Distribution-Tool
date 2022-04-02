package output.workers;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public abstract class OutputComponentSumWorker implements Runnable {

    protected final String resultName;

    protected final ObservableList<String> outputResults;

    protected final List<Future<Map<String, Long>>> data;

    protected OutputComponentSumWorker(String resultName, ObservableList<String> outputResults, List<Future<Map<String, Long>>> data) {
        this.resultName = resultName;
        this.outputResults = outputResults;
        this.data = data;
    }
}
