package com.example.demo;


import com.waterflow.rpc.HelloServiceImp;
import com.waterflow.service.HelloService;

public class Application {

    static HelloService helloService = new HelloServiceImp();

    public static void main(String[] args) {

        String words = helloService.hello("cls");
        System.out.println(words);

    }
}