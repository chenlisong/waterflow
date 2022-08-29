package com.example.rpc;

import com.waterflow.service.HelloService;

public class HelloServiceImp implements HelloService {
    @Override
    public String hello(String name) {
        return "hello world. " + name;
    }
}