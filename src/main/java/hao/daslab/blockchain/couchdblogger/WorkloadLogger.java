package hao.daslab.blockchain.couchdblogger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Arrays;

public class WorkloadLogger {

    PrintWriter output;

    public WorkloadLogger() {
        try {
            output = new PrintWriter("workload.log");
            new FlushThread().start();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String method, String url) {
        String[] pieces = url.split("/");

        long numVars = Arrays.stream(pieces).filter(p -> !p.startsWith("_")).count();
        if (numVars == 1) {// DB
            output.print(MessageFormat.format("DB:{0}:{1}", method, url));
        }
        if (numVars == 2) {// Doc
            output.print(MessageFormat.format("DOC:{0}:{1}", method, url));
        }
        if (numVars == 3) {// Attachment
            output.print(MessageFormat.format("ATTA:{0}:{1}", method, url));
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
