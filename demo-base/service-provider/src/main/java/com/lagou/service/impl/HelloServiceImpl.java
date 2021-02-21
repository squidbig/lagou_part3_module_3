package com.lagou.service.impl;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Random;

@Service
public class HelloServiceImpl   implements HelloService {
    @Override
    public String sayHello(String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello:"+name;
    }

    @Override
    public String methodA() {
        try {
            Thread.sleep(getRandom());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello: A";
    }

    @Override
    public String methodB() {
        try {
            Thread.sleep(getRandom());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello: B";
    }

    @Override
    public String methodC() {
        try {
            Thread.sleep(getRandom());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello: C";
    }

    //获得随机等待时间
    private int getRandom() {
        Random rn=new Random();
        return rn.nextInt(101);
    }
}
