package com.waterflow.rich.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages={"com.waterflow.rich"})
public class ApplicationWeb {
    
    public static void main(String[] args) {
        SpringApplication.run(ApplicationWeb.class, args);
    }

}
