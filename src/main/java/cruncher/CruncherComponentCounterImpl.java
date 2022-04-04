package cruncher;

import cruncher.workers.CruncherComponentWorkerCounterImpl;
import file_input.FileInputResult;
import javafx.scene.text.Text;
import manager.PipelineManager;
import output.OutputComponent;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class CruncherComponentCounterImpl extends CruncherComponent {

    public CruncherComponentCounterImpl(int arity, ForkJoinPool forkJoinPool, Text statusLabel) {
        super(arity, forkJoinPool, statusLabel);
    }

    @Override
    public void run() {
        while (true) {
            try {
                FileInputResult fileInputResult = getReceivedFileInputData().take();

                if (!PipelineManager.getInstance().getAcceptingNewWork().get())
                    return;

                Future<Map<String, Long>> countResult = getForkJoinPool().submit(new CruncherComponentWorkerCounterImpl(getArity(), fileInputResult, getFilesInCrunchingProcess()));

                CruncherResult cruncherResult = new CruncherResult(fileInputResult.getFilename(), getArity(), countResult);
                for (final OutputComponent outputComponent : getConnectedOutputs()) {
                    outputComponent.addToQueue(cruncherResult);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
