package load.tester.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "loader")
@Data
public class MainConfigurations {

    @NotEmpty
    private String url;
    @NotEmpty
    @Size(min = 1)
    private int numberOfRequestsPerThread;
    @NotEmpty
    @Size(min = 1)
    private int numberOfThreads;
    
}
