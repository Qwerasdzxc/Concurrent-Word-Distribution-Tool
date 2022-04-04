package output.workers;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import manager.PipelineManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OutputComponentSortWorkerImpl extends OutputComponentSortWorker {

    public OutputComponentSortWorkerImpl(Map<String, Long> data, LineChart<Number, Number> chart, ProgressBar progressBar, int sortProgressLimit) {
        super(data, chart, progressBar, sortProgressLimit);
    }

    @Override
    public void run() {
        try {
            Platform.runLater(() -> {
                progressBar.setProgress(0);
                progressBar.setVisible(true);
            });

            int n = data.keySet().size();

            List<Map.Entry<String, Long>> list = new ArrayList<>(data.entrySet());

            AtomicInteger currentProgress = new AtomicInteger(0);
            list.sort(Map.Entry.comparingByValue((first, second) -> {
                if (currentProgress.get() % sortProgressLimit == 0) {
                    Platform.runLater(() -> {
                        progressBar.setProgress((double) currentProgress.get() / (n * Math.log10(n)));
                    });
                }

                currentProgress.addAndGet(1);
                return (int) (second - first);
            }));

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            int i = 0;
            for (Map.Entry<String, Long> entry : list) {
                if (i > 100)
                    break;

                series.getData().add(new XYChart.Data<>(i++, entry.getValue()));
            }

            Platform.runLater(() -> {
                chart.getData().clear();
                chart.getData().addAll(series);
                progressBar.setProgress(0);
                progressBar.setVisible(false);
            });
        } catch (OutOfMemoryError e) {
            PipelineManager.getInstance().terminateApplication();
        }
    }
}
