package load.tester.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import load.tester.config.MainConfigurations;
import load.tester.threads.LoadThread;
import lombok.extern.log4j.Log4j2;
@Log4j2
@Component
public class RunThread {

//    @Autowired
//    LoadThread loadThread;
    
    @Autowired
    MainConfigurations mainConfig;
    
    private RestTemplate rt;
    private String url;
    private HttpEntity<String> request;
    private int numberOfRequestsPerThread;
    private int numberOfThreads;

    @PostConstruct
    public void init() {
        rt = new RestTemplate();
        String jsonRequest = null;
        try {
            jsonRequest = Utils.loadFile("/MPI-enrollment-request");
//            jsonRequest = Utils.loadFile("/auth-request");
        } catch (IOException e) {
            log.error("Could not load payload file. " + e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<String>(jsonRequest, headers);
        url = mainConfig.getUrl();
        numberOfRequestsPerThread = mainConfig.getNumberOfRequestsPerThread();
        numberOfThreads = mainConfig.getNumberOfThreads();
    }
    
    
    public int run() throws InterruptedException {
        log.info(System.getProperty("line.separator") + "@@@@ Starting Load Tester @@@@" + System.getProperty("line.separator") +
    "Nummber of threads: " + numberOfThreads + System.getProperty("line.separator") +
    "Number of request per thread: " + numberOfRequestsPerThread);
        List<String> timeList = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch readyThreads = new CountDownLatch(numberOfThreads);
        CountDownLatch threadBlock = new CountDownLatch(1);
        CountDownLatch compleatThreads = new CountDownLatch(numberOfThreads);
        int i = 1;
        List<Thread> times = Stream
          .generate(() -> new Thread(new LoadThread(numberOfRequestsPerThread, url, rt, timeList, request, readyThreads, threadBlock, compleatThreads)))
          .limit(numberOfThreads)
          .collect(Collectors.toList());
        for (Thread thread : times) {
            thread.setName(String.valueOf(i));
            i++;
            thread.start();
        }
        readyThreads.await(); 
        log.info("All threads ready, starting to send requests");
        threadBlock.countDown(); 
        compleatThreads.await(); 
        log.info("All threads compleated");
        log.info(timeList.toString());
        return 0;
    } 
    
   
}
