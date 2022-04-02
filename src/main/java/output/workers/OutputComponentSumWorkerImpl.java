package output.workers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import manager.PipelineManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OutputComponentSumWorkerImpl extends OutputComponentSumWorker {

    public OutputComponentSumWorkerImpl(String resultName, ObservableList<String> outputResults, List<Future<Map<String, Long>>> data) {
        super(resultName, outputResults, data);
    }

    @Override
    public void run() {
        Map<String, Long> summedResult = new HashMap<>();
        List<Map<String, Long>> finishedResults = new ArrayList<>();

        for (final Future<Map<String, Long>> result : data) {
            try {
                finishedResults.add(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        for (final Map<String, Long> result : finishedResults) {
            for (Map.Entry<String, Long> entry : result.entrySet()) {
                summedResult.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }

        PipelineManager.getInstance().getOutputComponent().addResultToDataMap(resultName, CompletableFuture.completedFuture(summedResult));

        Platform.runLater(() -> {
            outputResults.remove(resultName + "*");
            outputResults.add(resultName);
        });
    }
}
