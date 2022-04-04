package output.workers;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public abstract class OutputComponentSumWorker implements Runnable {

    protected final String resultName;

    protected final ObservableList<String> outputResults;

    protected final List<Future<Map<String, Long>>> data;

    protected final Pane pane;

    protected OutputComponentSumWorker(String resultName, ObservableList<String> outputResults, List<Future<Map<String, Long>>> data, Pane pane) {
        this.resultName = resultName;
        this.outputResults = outputResults;
        this.data = data;
        this.pane = pane;
    }
}
