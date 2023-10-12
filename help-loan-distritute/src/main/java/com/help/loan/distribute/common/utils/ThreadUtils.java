/*
 * 文件名：ThreadUtils.java 版权：深圳融信信息咨询有限公司 修改人：zhangqiuping 修改时间：@create_date
 * 2017年7月20日 上午9:16:53 修改内容：新增
 */
package com.help.loan.distribute.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangqiuping
 * @create_date 2017年7月20日 上午9:16:53
 * @since 2.2.4
 */
public class ThreadUtils {

    /**
     * 调测日志记录器。
     */
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtils.class);

    /**
     * 固定大小的线程池
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(200);

    /**
     * 固定大小延迟执行线程池
     */
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(200);


    /**
     * 执行一个线程，如果有空闲的线程资源，则立即执行
     *
     * @param runnable 实现Runnable接口的线程对象
     * @author zhangqiuping
     */
    public static void runThread(Runnable runnable) {
        if(null == runnable) {
            logger.error("待执行的线程对象是null......");
            return;
        }
        Thread thread = new Thread(runnable);
        thread.setName(runnable.getClass().getSimpleName());
        executorService.execute(thread);
    }

    /**
     * 延迟执行线程
     *
     * @param runnable 实现Runnable接口的线程对象
     * @param delay    延迟时长
     * @param unit     延迟时长单位
     * @author zhangqiuping
     */
    public static void runScheduledThread(Runnable runnable, Long delay, TimeUnit unit) {
        if(null == runnable) {
            logger.error("待执行的线程对象是null......");
            return;
        }
        Thread thread = new Thread(runnable);
        thread.setName(runnable.getClass().getSimpleName());
        scheduledExecutorService.schedule(thread, delay, unit);
    }

}
