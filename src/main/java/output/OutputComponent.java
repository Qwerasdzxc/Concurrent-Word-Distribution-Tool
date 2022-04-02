package output;

import cruncher.CruncherResult;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import output.workers.OutputComponentSortWorkerImpl;
import output.workers.OutputComponentSumWorkerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public abstract class OutputComponent implements Runnable {

    protected final ExecutorService threadPool;

    protected final Map<String, Future<Map<String, Long>>> data;

    protected final ObservableList<String> outputResults;

    protected final BlockingQueue<CruncherResult> receivedCruncherData;

    protected OutputComponent(ExecutorService threadPool, ObservableList<String> outputResults) {
        this.threadPool = threadPool;
        this.outputResults = outputResults;
        this.receivedCruncherData = new LinkedBlockingQueue<>();
        this.data = new ConcurrentHashMap<>();
    }

    public void addToQueue(CruncherResult cruncherResult) {
        try {
            this.receivedCruncherData.put(cruncherResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Long> getFinishedResult(String filename) throws Exception {
        Future<Map<String, Long>> resultForFile = data.get(filename);
        if (!resultForFile.isDone())
            throw new Exception("File " + filename + "is still in Cruncher phase!");

        return resultForFile.get();
    }

    public void showSortedData(Map<String, Long> data, LineChart<Number, Number> chart) {
        threadPool.execute(new OutputComponentSortWorkerImpl(data, chart));
    }

    public void calculateSumData(List<String> selected, String resultName) {
        List<Future<Map<String, Long>>> results = new ArrayList<>();
        for (final String filename : selected) {
            results.add(data.get(filename.replaceAll("\\*", "")));
        }

        Platform.runLater(() -> {
            outputResults.add(resultName + "*");
        });

        threadPool.execute(new OutputComponentSumWorkerImpl(resultName, outputResults, results));
    }

    public void addResultToDataMap(String filename, Future<Map<String, Long>> result) {
        this.data.put(filename, result);
    }
}
