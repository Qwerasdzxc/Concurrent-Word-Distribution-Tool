package cruncher;

import java.util.Map;
import java.util.concurrent.Future;

public class CruncherResult {

    private String filename;

    private int arity;

    private Future<Map<String, Long>> result;

    public CruncherResult(String filename, int arity, Future<Map<String, Long>> result) {
        this.filename = filename;
        this.arity = arity;
        this.result = result;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getArity() {
        return arity;
    }

    public void setArity(int arity) {
        this.arity = arity;
    }

    public Future<Map<String, Long>> getResult() {
        return result;
    }

    public void setResult(Future<Map<String, Long>> result) {
        this.result = result;
    }
}
