package com.alibaba.csp.sentinel.demo.service;

public interface Community {

    String query(String name);

    String find(String name);

    String floor(String name);

    String layer(String name);

    String people(int num);

    String limit();
}
