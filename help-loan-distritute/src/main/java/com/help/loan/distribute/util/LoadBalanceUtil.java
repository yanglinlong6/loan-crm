package com.help.loan.distribute.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮训工具类
 *
 * @author Yanglinlong
 * @date 2023/10/25 16:45
 */
public class LoadBalanceUtil {
    private static Integer index = 0;

    /**
     * 加锁同步实现线程安全的轮询负载均衡算法
     *
     * @param numList 轮询集合
     * @return
     */
    public static String doSelect(List<String> numList) {
        synchronized (index) {
            if (index >= numList.size()) {
                index = 0;
            }
            String ip = numList.get(index);
            index++;
            return ip;
        }
    }


    private static AtomicInteger index_ = new AtomicInteger(0);

    /**
     * 原子类实现线程安全的轮询负载均衡算法
     *
     * @param numList 轮询集合
     * @return
     */
    public static String doSelect2(List<String> numList) {

        if (index_.get() >= numList.size()) {
            index_ = new AtomicInteger(0);
        }
        String ip = numList.get(index_.get());
        index_.incrementAndGet();
        return ip;
    }

    /**
     * 原子类实现线程安全的轮询负载均衡算法
     *
     * @param numList 轮询集合
     * @return
     */
    public static Integer doSelect2ForInt(List<Integer> numList) {

        if (index_.get() >= numList.size()) {
            index_ = new AtomicInteger(0);
        }
        Integer ip = numList.get(index_.get());
        index_.incrementAndGet();
        return ip;
    }
}
