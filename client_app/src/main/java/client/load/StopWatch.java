package client.load;

import java.util.ArrayList;
import java.util.List;

public class StopWatch {
    private List<Record> records = new ArrayList<>();

    private String currentName;

    private long startTime;
    public void start(String name) {
        this.currentName = name;
        this.startTime = System.currentTimeMillis();
    }

    public void record() {

    }

    static class Record {
        long start;
        long elapse;
        String name;

        public Record(String name) {
            this.start = System.currentTimeMillis();
            this.name = name;
        }
    }

}
