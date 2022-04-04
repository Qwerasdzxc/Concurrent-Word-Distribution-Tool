package output.workers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import manager.PipelineManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class OutputComponentSumWorkerImpl extends OutputComponentSumWorker {

    public OutputComponentSumWorkerImpl(String resultName, ObservableList<String> outputResults, List<Future<Map<String, Long>>> data, Pane pane) {
        super(resultName, outputResults, data, pane);
    }

    @Override
    public void run() {
        try {
            Map<String, Long> summedResult = new HashMap<>();
            List<Map<String, Long>> finishedResults = new ArrayList<>();

            Text sortText = new Text("Summing...");
            ProgressBar progressBar = new ProgressBar(0);

            Platform.runLater(() -> {
                pane.getChildren().add(sortText);
                pane.getChildren().add(progressBar);
            });

            for (final Future<Map<String, Long>> result : data) {
                try {
                    finishedResults.add(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            AtomicInteger currentProgress = new AtomicInteger(0);
            for (final Map<String, Long> result : finishedResults) {
                for (Map.Entry<String, Long> entry : result.entrySet()) {
                    summedResult.merge(entry.getKey(), entry.getValue(), Long::sum);
                }

                currentProgress.addAndGet(1);
                Platform.runLater(() -> {
                    progressBar.setProgress((double) currentProgress.get() / (double) finishedResults.size());
                });
            }

            PipelineManager.getInstance().getOutputComponent().addResultToDataMap(resultName, CompletableFuture.completedFuture(summedResult));

            Platform.runLater(() -> {
                outputResults.remove(resultName + "*");
                outputResults.add(resultName);

                pane.getChildren().remove(sortText);
                pane.getChildren().remove(progressBar);
            });
        } catch (OutOfMemoryError e) {
            PipelineManager.getInstance().terminateApplication();
        }
    }
}
