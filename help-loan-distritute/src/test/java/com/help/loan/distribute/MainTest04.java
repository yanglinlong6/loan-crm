package com.help.loan.distribute;

import com.google.common.collect.Lists;
import com.help.loan.distribute.util.LoadBalanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yanglinlong
 * @date 2023/10/25 16:43
 */
public class MainTest04 {
    public static void main(String[] args) {
        List<String> ips = new ArrayList<>();
        ips.add("192.168.0.1");
        ips.add("192.168.0.2");
        ips.add("192.168.0.3");

        //测试第一种方法
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2(ips));

        //测试第二种方法

        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect(ips));

        List<Integer> list = Lists.newArrayList(14851822, 18872440, 19518988, 16386287, 18594574);
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list));

        List<Integer> list01 = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7);
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
        System.out.println("选择ip:" + LoadBalanceUtil.doSelect2ForInt(list01));
    }
}
