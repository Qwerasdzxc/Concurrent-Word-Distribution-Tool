package cruncher.workers;

import file_input.FileInputResult;

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

    public CruncherComponentWorkerCounterImpl(int arity, FileInputResult fileInputResult) {
        super(arity, fileInputResult);

        this.length = fileInputResult.getData().length();
        this.data = fileInputResult.getData();
    }

    @Override
    protected Map<String, Long> compute() {
        if (length == 0)
            return new HashMap<>();

        Map<String, Long> results = new ConcurrentHashMap<>();

        if (arity == 1) {
            List<String> words = getWords(data);
            for (final String word : words) {
                if (results.containsKey(word))
                    results.put(word, results.get(word) + 1);
                else
                    results.put(word, 1L);
            }
        }

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
