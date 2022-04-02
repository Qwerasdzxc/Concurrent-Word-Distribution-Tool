package output.workers;

import javafx.scene.chart.LineChart;

import java.util.Map;

public abstract class OutputComponentSortWorker implements Runnable {

    protected final Map<String, Long> data;
    protected final LineChart<Number, Number> chart;

    protected OutputComponentSortWorker(Map<String, Long> data, LineChart<Number, Number> chart) {
        this.data = data;
        this.chart = chart;
    }
}
