package cruncher.workers;

import file_input.FileInputResult;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import manager.PipelineManager;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CruncherComponentWorkerCounterImpl extends CruncherComponentWorker {

    private final int length;

    private final String data;

    private final long L = 10485760L;

    public CruncherComponentWorkerCounterImpl(int arity, FileInputResult fileInputResult, ObservableList<String> filesInCrunchingProcess) {
        super(arity, fileInputResult, filesInCrunchingProcess);

        this.length = fileInputResult.getData().length();
        this.data = fileInputResult.getData();
    }

    @Override
    protected Map<String, Long> compute() {
        if (length == 0)
            return new HashMap<>();

        filesInCrunchingProcess.add(getFileInputResult().getFilename());

        Map<String, Long> results = new ConcurrentHashMap<>();

        try {
            StringBuilder currWord = new StringBuilder();
            int spaces = 0;
            int k = 0;
            for (int i = 0; i < data.length(); i++) {
                char currChar = data.charAt(i);
                if (currChar == ' ') {
                    spaces += 1;

                    if (spaces == 1)
                        k = i;

                    if (spaces == arity) {
                        String word = currWord.toString();
                        if (results.containsKey(word))
                            results.put(word, results.get(word) + 1);
                        else
                            results.put(word, 1L);

                        spaces = 0;
                        i = k;
                        currWord.setLength(0);
                    } else {
                        currWord.append(" ");
                    }
                } else if (i == data.length() - 1) {
                    currWord.append(currChar);
                    String word = currWord.toString();
                    if (results.containsKey(word))
                        results.put(word, results.get(word) + 1);
                    else
                        results.put(word, 1L);

                    spaces = 0;
                    currWord.setLength(0);
                }
                else {
                    currWord.append(currChar);
                }
            }

        } catch (Exception e) {
            PipelineManager.getInstance().terminateApplication();
        }

        filesInCrunchingProcess.remove(getFileInputResult().getFilename());

        return results;
    }
}
