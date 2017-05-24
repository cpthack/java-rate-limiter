/**
 * Copyright (c) 2013-2020, cpthack 成佩涛 (cpt@jianzhimao.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cpthack.commons.ratelimiter.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cpthack.commons.rdclient.config.RedisConfig;
import com.github.cpthack.commons.ratelimiter.config.CustomRateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.config.RateRedisConfig;

/**
 * <b>LockTest.java</b></br>
 * 
 * <pre>
 * 并发锁测试类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 22, 2017 10:37:45 AM
 * @since JDK 1.7
 */
public class LockTest {
	
	private final static Logger	logger	   = LoggerFactory.getLogger(LockTest.class);
	
	private static String		UNIQUE_KEY = "/lock1";
	private static Lock			lock	   = null;
	
	private volatile static int	successNum = 0;
	
	public static void main(String[] args) {
		RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();
		// lock = getSingleLock(rateLimiterConfig);
		RedisConfig redisConfig = new RateRedisConfig();
		
		lock = getDistributedLock(rateLimiterConfig, redisConfig);
		
		// RedisClientFactory.getClient(redisConfig).set("/lock1", "10");//
		// 模拟releaseLock没有执行导致的缓存中存在较多正数值得锁KEY
		
		simulateConcurrentThread(80); // 模拟并发线程
	}
	
	private static Lock getSingleLock(RateLimiterConfig rateLimiterConfig) {
		return LockFactory.getInstance().single(rateLimiterConfig);
	}
	
	private static Lock getDistributedLock(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
		return LockFactory.getInstance().distributed(rateLimiterConfig, redisConfig);
	}
	
	private static void simulateConcurrentThread(int threadNum) {
		DoThing dt = null;
		Thread t = null;
		for (int i = 0; i < threadNum; i++) {
			dt = new DoThing("Thread " + i);
			t = new Thread(dt);
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}// 模拟程序执行时间
			t.start();
		}
	}
	
	/**
	 * 自定义线程，用于模拟并发
	 */
	static class DoThing implements Runnable {
		String name;
		
		public DoThing(String name) {
			this.name = name;
		}
		
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			try {
				if (lock.lock(UNIQUE_KEY)) {// 进行并发控制
					
					logger.info("Thread Name is [{}] 成功获得锁，正在处理中...", name);
					
					successNum++;
					logger.info("当前成功并发数successNum的值为 [" + successNum + "]");
					Thread.currentThread().sleep(2000);// 模拟程序执行时间
					
					successNum--;
					lock.releaseLock(UNIQUE_KEY);
				}
				else {
					logger.warn("Thread Name is [{}] 尝试获得锁失败", name);
				}
			}
			catch (InterruptedException e) {
				logger.error("Thread Name [{}] is Error.", name, e);
			}
		}
	}
}
