package output.workers;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import manager.PipelineManager;

import java.util.*;

public class OutputComponentSortWorkerImpl extends OutputComponentSortWorker {

    public OutputComponentSortWorkerImpl(Map<String, Long> data, LineChart<Number, Number> chart) {
        super(data, chart);
    }

    @Override
    public void run() {
        try {
            List<Map.Entry<String, Long>> list = new ArrayList<>(data.entrySet());
            list.sort(Map.Entry.comparingByValue(new Comparator<Long>() {
                @Override
                public int compare(Long first, Long second) {
                    return (int) (second - first);
                }
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

                System.out.println("Sorter worker end");
            });
        } catch (OutOfMemoryError e) {
            PipelineManager.getInstance().terminateApplication();
        }
    }
}
