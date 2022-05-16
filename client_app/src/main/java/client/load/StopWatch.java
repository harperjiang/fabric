package client.load;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class StopWatch {
    protected List<Record> records = new ArrayList<>();

    public void start(String name) {
        Record record = new Record(name);
        records.add(record);
    }

    public void record() {
        if (records.isEmpty())
            return;
        Record rec = records.get(records.size() - 1);
        rec.elapse();
    }

    public void output(PrintWriter to) {
        for (Record rec : records) {
            to.write(rec.toString());
        }
        records.clear();
    }

    static class Record {
        long start;
        long elapse;
        String name;

        public Record(String name) {
            this.start = System.currentTimeMillis();
            this.name = name;
        }

        public void elapse() {
            this.elapse = System.currentTimeMillis() - start;
        }

        @Override
        public String toString() {
            return MessageFormat.format("{0},{1},{2}", name, start, elapse);
        }
    }

}
