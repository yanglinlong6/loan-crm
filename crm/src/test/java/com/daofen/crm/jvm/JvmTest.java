package com.daofen.crm.jvm;

import java.util.ArrayList;
import java.util.List;

public class JvmTest {


    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000);
        List<JvmDemo> data = new ArrayList();
        for(long i=10000;i<100000;i++){
            System.out.println(i);
            JvmDemo jvmDemo = new JvmDemo(i);
            data.add(jvmDemo);
        }

    }
}
