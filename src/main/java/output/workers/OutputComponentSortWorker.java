package output.workers;

import javafx.scene.chart.LineChart;
import javafx.scene.control.ProgressBar;

import java.util.Map;

public abstract class OutputComponentSortWorker implements Runnable {

    protected final Map<String, Long> data;
    protected final LineChart<Number, Number> chart;
    protected final ProgressBar progressBar;
    protected final int sortProgressLimit;

    protected OutputComponentSortWorker(Map<String, Long> data, LineChart<Number, Number> chart, ProgressBar progressBar, int sortProgressLimit) {
        this.data = data;
        this.chart = chart;
        this.progressBar = progressBar;
        this.sortProgressLimit = sortProgressLimit;
    }
}
