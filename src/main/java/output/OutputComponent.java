package output;

import cruncher.CruncherResult;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import manager.PipelineManager;
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

        new Thread(this).start();
    }

    public void addToQueue(CruncherResult cruncherResult) {
        try {
            this.receivedCruncherData.put(cruncherResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Long> poll(String filename) throws Exception {
        filename = filename.substring(0, filename.indexOf(".txt") + 4);
        Future<Map<String, Long>> resultForFile = data.get(filename);
        if (!resultForFile.isDone())
            return null;

        return resultForFile.get();
    }

    public Future<Map<String, Long>> take(String filename) {
        filename = filename.substring(0, filename.indexOf(".txt") + 4);
        return data.get(filename);
    }

    public void showSortedData(Map<String, Long> data, LineChart<Number, Number> chart) {
        if (!PipelineManager.getInstance().getAcceptingNewWork().get())
            return;

        threadPool.execute(new OutputComponentSortWorkerImpl(data, chart));
    }

    public void calculateSumData(List<String> selected, String resultName) {
        List<Future<Map<String, Long>>> results = new ArrayList<>();
        for (String filename : selected) {
            filename = filename.substring(0, filename.indexOf(".txt") + 4);
            results.add(data.get(filename.replaceAll("\\*", "")));
        }

        Platform.runLater(() -> {
            outputResults.add(resultName + "*");
        });

        if (!PipelineManager.getInstance().getAcceptingNewWork().get())
            return;

        threadPool.execute(new OutputComponentSumWorkerImpl(resultName, outputResults, results));
    }

    public void addResultToDataMap(String filename, Future<Map<String, Long>> result) {
        this.data.put(filename, result);
    }
}
