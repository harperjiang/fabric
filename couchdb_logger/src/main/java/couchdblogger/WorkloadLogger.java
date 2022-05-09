package couchdblogger;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;

public class WorkloadLogger {

    PrintWriter output;

    ThreadLocal<LogRecord> records = ThreadLocal.withInitial(() -> new LogRecord());

    public WorkloadLogger(String logFolder) {
        try {
            output = new PrintWriter(Paths.get(logFolder, "workload.log").toAbsolutePath().toString());
            new FlushThread().start();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void begin(String method, String url) {
        if (url == null) {
            url = "/";
        }
        this.records.get().record(method, url);
    }

    public void end() {
        LogRecord record = this.records.get();
        long elapse = System.currentTimeMillis() - record.startTime;
        output.println(MessageFormat.format("{0},{1},{2},{3}", String.valueOf(record.startTime), String.valueOf(elapse), record.method, record.url));
    }


    static class LogRecord {
        String method;
        String url;
        long startTime = System.currentTimeMillis();

        public void record(String m, String u) {
            this.method = m;
            this.url = u;
            this.startTime = System.currentTimeMillis();
        }
    }

    class FlushThread extends Thread {
        FlushThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                output.flush();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
