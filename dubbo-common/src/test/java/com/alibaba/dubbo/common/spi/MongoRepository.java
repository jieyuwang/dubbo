package com.alibaba.dubbo.common.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class MongoRepository implements IRepository {
    public void save(String data) {
        System.out.println("Save " + data + " to Mysql");
    }


    public static void main(String[] args) {
        ServiceLoader<IRepository> serviceLoader = ServiceLoader.load(IRepository.class);
        Iterator<IRepository> it = serviceLoader.iterator();
        while (it != null && it.hasNext()){
            IRepository demoService = it.next();
            System.out.println("class:" + demoService.getClass().getName());
            demoService.save("tom");
        }

    }
}
