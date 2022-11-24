package com.waterflow.ccopen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    Logger logger = LoggerFactory.getLogger(HelloService.class);

    public String hello(String name) {
        String dist = String.format("HelloService %s!", name);
        logger.info(dist);
        return dist;
    }

}
