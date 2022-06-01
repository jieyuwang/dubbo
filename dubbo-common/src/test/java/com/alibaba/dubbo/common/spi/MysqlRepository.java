package com.alibaba.dubbo.common.spi;

public class MysqlRepository implements IRepository {
    public void save(String data) {
        System.out.println("Save " + data + " to Mongo");
    }
}
