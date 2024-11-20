package com.alibaba.csp.sentinel.demo.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class CommunityImpl implements Community{

    @Autowired
    @Lazy
    private Community community;

    @Override
    @SentinelResource("query")
    public String query(String name) {
        community.floor(name);
        return "ok";
    }

    @Override
    @SentinelResource("find")
    public String find(String name) {
        community.floor(name);
        return null;
    }

    @Override
    @SentinelResource("floor")
    public String floor(String name) {
        community.layer(name);
        return null;
    }

    @Override
    @SentinelResource("layer")
    public String layer(String name) {
        community.people(11);
        return null;
    }

    @Override
    @SentinelResource("people")
    public String people(int num) {
        return null;
    }

    @SentinelResource("limit")
    @Override
    public String limit() {
        community.floor("");
        return "ok";
    }
}
