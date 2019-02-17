package load.tester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import load.tester.config.MainConfigurations;
import load.tester.service.Utils;
import lombok.extern.log4j.Log4j2;
import load.tester.service.RunThread;

@SpringBootApplication
@Log4j2
public class LoaderTesterApplication {

    
	public static void main(String[] args) {
//		SpringApplication.run(LoaderTesterApplication.class, args);
	    ConfigurableApplicationContext ctx = SpringApplication.run(LoaderTesterApplication.class, args);
        try {
            int exitCode = ctx.getBean(RunThread.class).run();
            log.info("@@@@ Loader tester finished with SUCCESS. @@@@");
            System.exit(exitCode);
        } catch (Exception e) {
            log.error("@@@@ Loeader tester exits with FAILURE. @@@@" + e);
            e.printStackTrace();

        }
    
	}
}
