package com.loan.wechat.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class SessionLock {
	
	public static Map<String,TimeLock> LOCK_MAP = new HashMap<String,TimeLock>();
	
	private static Logger logger = LoggerFactory.getLogger(SessionLock.class);
	
	public  static void  lock(String openid) {
		TimeLock reentrantLock = null;
		synchronized(LOCK_MAP) {
			reentrantLock = LOCK_MAP.get(openid);
			if(reentrantLock==null) {
				reentrantLock = new TimeLock();
				LOCK_MAP.put(openid, reentrantLock);
			}
		}
		reentrantLock.lock();
		reentrantLock.setTime(System.currentTimeMillis());
	}
	
	public static void unlock(String openid) {
		TimeLock reentrantLock = LOCK_MAP.get(openid);
		if(reentrantLock!=null) {
			reentrantLock.replaceExclusiveOwnerThread(Thread.currentThread());
			reentrantLock.unlock();
		}
		
	}
	
	@Scheduled(fixedRate = 5000)
	public void removeLock() {
		Iterator<String> iterator = LOCK_MAP.keySet().iterator();
		while(iterator.hasNext()) {
			String next = iterator.next();
			TimeLock timeLock = LOCK_MAP.get(next);
			if(System.currentTimeMillis()-timeLock.getTime()>10000&&timeLock.isLocked()) {
				logger.info("lock time out openid:[ {} ] unlock now", next);
				timeLock.replaceExclusiveOwnerThread(Thread.currentThread());
				timeLock.unlock();
			}
			if(System.currentTimeMillis()-timeLock.getTime()>60000) {
				logger.info("wait time out,release lock cache openid:[ {} ] ", next);
				synchronized(LOCK_MAP) {
					LOCK_MAP.remove(next);
				}
			}
		}
	}
	
}
