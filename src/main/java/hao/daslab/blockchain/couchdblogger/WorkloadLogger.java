package hao.daslab.blockchain.couchdblogger;

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
        url = url.replaceFirst("^/", "");
        String[] pieces = url.split("/");

        long numVars = Arrays.stream(pieces).filter(p -> !p.isEmpty() && !p.startsWith("_")).count();
        if (numVars == 1) {// DB
            output.println(MessageFormat.format("DB:{0}:{1}", method, url));
        }
        if (numVars == 2) {// Doc
            output.println(MessageFormat.format("DOC:{0}:{1}", method, url));
        }
        if (numVars == 3) {// Attachment
            output.println(MessageFormat.format("ATTA:{0}:{1}", method, url));
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
