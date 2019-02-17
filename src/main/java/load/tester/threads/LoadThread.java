package load.tester.threads;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;
@Log4j2
public class LoadThread implements Runnable {

    private int numberOfRequestsPerThread;
    private String url;
    private RestTemplate rt;
    private List<String> timeList;
    private HttpEntity<String> request;
    
    private CountDownLatch readyThreadCounter;
    private CountDownLatch callingThreadBlocker;
    private CountDownLatch completedThreadCounter;
 
    public LoadThread(int numberOfRequestsPerThread, String url, RestTemplate rt,
            List<String> timeList, HttpEntity<String> request, CountDownLatch readyThreadCounter, CountDownLatch callingThreadBlocker,
            CountDownLatch completedThreadCounter) {
        super();
        this.numberOfRequestsPerThread = numberOfRequestsPerThread;
        this.url = url;
        this.request = request;
        this.rt = rt;
        this.timeList = timeList;
        this.readyThreadCounter = readyThreadCounter;
        this.callingThreadBlocker = callingThreadBlocker;
        this.completedThreadCounter = completedThreadCounter;
    }

    public void run() {
        readyThreadCounter.countDown();
        try {
            callingThreadBlocker.await();
            String total = send(numberOfRequestsPerThread);
            timeList.add(total);
        } catch (IOException | InterruptedException e) {
            log.error("Exception with thread. " + e);
        } finally {
            completedThreadCounter.countDown();
        }
    }
    
    public String send(int numberOfTimes) throws IOException {

        double threadStartTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        int threadCounter = 0;
        for (int i = 0; i < numberOfTimes; i++) {
            double before = System.currentTimeMillis();
            ResponseEntity<String> response = rt.exchange(url, HttpMethod.POST, request, String.class);
            String responseString = response.getBody();
            log.info(responseString);
            double after = System.currentTimeMillis();
            log.info(threadName + ": " + (after - before) + " ms");
            threadCounter += (after - before);
        }
        double threadEndTime = System.currentTimeMillis();
        String avarageRequestsTime = "Avarage request time: " + threadCounter/Double.valueOf(numberOfTimes) + "ms";
        String totalThreadTime = "Total time: " + (threadEndTime - threadStartTime)/1000 + " s. "; 
        log.info(avarageRequestsTime);
        log.info(totalThreadTime);
        String total = System.getProperty("line.separator") + "Thread ["+threadName+"]:" + totalThreadTime + avarageRequestsTime ;
        return total;
    } 
}
