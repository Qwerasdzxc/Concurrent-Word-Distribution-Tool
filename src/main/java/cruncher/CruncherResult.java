package cruncher;

import java.util.Map;
import java.util.concurrent.Future;

public class CruncherResult {

    private String filename;

    private Future<Map<String, Long>> result;

    public CruncherResult(String filename, Future<Map<String, Long>> result) {
        this.filename = filename;
        this.result = result;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Future<Map<String, Long>> getResult() {
        return result;
    }

    public void setResult(Future<Map<String, Long>> result) {
        this.result = result;
    }
}
