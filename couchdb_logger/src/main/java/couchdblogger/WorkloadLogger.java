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

    public WorkloadLogger(String logFolder) {
        try {
            output = new PrintWriter(Paths.get(logFolder, "workload.log").toAbsolutePath().toString());
            new FlushThread().start();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String method, String url) {
        if (url == null) {
            url = "/";
        }
        output.println(MessageFormat.format("{0},{1},{2}", String.valueOf(System.currentTimeMillis()), method, url));
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
