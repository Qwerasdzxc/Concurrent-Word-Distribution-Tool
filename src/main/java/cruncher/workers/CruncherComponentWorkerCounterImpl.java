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
            if (arity == 1) {
                List<String> words = getWords(data);
                for (final String word : words) {
                    if (results.containsKey(word))
                        results.put(word, results.get(word) + 1);
                    else
                        results.put(word, 1L);
                }
            }

        } catch (OutOfMemoryError e) {
            PipelineManager.getInstance().terminateApplication();
        }

        filesInCrunchingProcess.remove(getFileInputResult().getFilename());

        return results;
    }

    private List<String> getWords(String text) {
        List<String> words = new ArrayList<>();
        BreakIterator breakIterator = BreakIterator.getWordInstance();
        breakIterator.setText(text);
        int lastIndex = breakIterator.first();
        while (BreakIterator.DONE != lastIndex) {
            int firstIndex = lastIndex;
            lastIndex = breakIterator.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                words.add(text.substring(firstIndex, lastIndex));
            }
        }

        return words;
    }
}
