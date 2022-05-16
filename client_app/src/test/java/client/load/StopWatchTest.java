package client.load;

import org.junit.jupiter.api.Test;

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
    }

    @Test
    void testOutput() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("abc");
        Thread.sleep(1000);
        stopWatch.record();

    }
}