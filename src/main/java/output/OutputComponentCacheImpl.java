package output;

import cruncher.CruncherResult;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import manager.PipelineManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class OutputComponentCacheImpl extends OutputComponent {

    public OutputComponentCacheImpl(ExecutorService threadPool, ObservableList<String> outputResults) {
        super(threadPool, outputResults);

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                CruncherResult cruncherResult = receivedCruncherData.take();
                data.put(cruncherResult.getFilename(), cruncherResult.getResult());

                Platform.runLater(() -> {
                    outputResults.add(cruncherResult.getFilename() + "-arity" + cruncherResult.getArity() + "*");
                });

                if (!PipelineManager.getInstance().getAcceptingNewWork().get())
                    return;

                threadPool.execute(() -> {
                    try {
                        cruncherResult.getResult().get();
                        System.out.println("Output received and finished: " + cruncherResult.getFilename());
                        Platform.runLater(() -> {
                            outputResults.remove(cruncherResult.getFilename() + "-arity" + cruncherResult.getArity() + "*");
                            outputResults.add(cruncherResult.getFilename() + "-arity" + cruncherResult.getArity());
                        });
                    } catch (InterruptedException | ExecutionException e) {
                        System.out.println("Worker thread interrupted.");
                    }

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
