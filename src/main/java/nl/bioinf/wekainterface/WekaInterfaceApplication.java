package nl.bioinf.wekainterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WekaInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WekaInterfaceApplication.class, args);
    }

}
