package client.load;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class StopWatchTest {

    @Test
    void testStart() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("abc");
        assertEquals(1, stopWatch.records.size());
        StopWatch.Record last = stopWatch.records.get(stopWatch.records.size() - 1);
        assertEquals("abc", last.name);

        stopWatch.start("ddd");
        assertEquals(2, stopWatch.records.size());
        last = stopWatch.records.get(stopWatch.records.size() - 1);
        assertEquals("ddd", last.name);
    }

    @Test
    void testRecord() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("abc");
        Thread.sleep(1000);
        stopWatch.record();

        assertEquals(1, stopWatch.records.size());
        StopWatch.Record last = stopWatch.records.get(stopWatch.records.size() - 1);
        assertEquals("abc", last.name);
        assertTrue(last.elapse > 1000);

        stopWatch.record();

        assertEquals(1, stopWatch.records.size());
        last = stopWatch.records.get(stopWatch.records.size() - 1);
        assertEquals("abc", last.name);
        assertTrue(last.elapse > 1000);
    }

    @Test
    void testOutput() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("abc");
        Thread.sleep(1000);
        stopWatch.record();

        stopWatch.start("ddd");
        Thread.sleep(2000);
        stopWatch.record();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(buffer);

        stopWatch.output(pw);
        pw.close();

        String data = new String(buffer.toByteArray());
        String[] lines = data.split("\n");
        assertEquals(2,lines.length);

        Pattern ptn1 = Pattern.compile("abc,\\d+,\\d+");
        Pattern ptn2 = Pattern.compile("ddd,\\d+,\\d+");

        assertTrue(ptn1.matcher(lines[0]).matches());
        assertTrue(ptn2.matcher(lines[1]).matches());
    }
}