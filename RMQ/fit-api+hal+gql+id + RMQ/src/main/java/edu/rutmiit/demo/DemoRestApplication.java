package edu.rutmiit.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"edu.rutmiit.demo", "edu.rutmiit.demo.fit_contract"},
        exclude = {DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DemoRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRestApplication.class, args);
    }

}



// http://localhost:8080/swagger-ui/index.html
// http://localhost:8080/graphiql
// http://localhost:8080/api

// docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
// http://localhost:15672


