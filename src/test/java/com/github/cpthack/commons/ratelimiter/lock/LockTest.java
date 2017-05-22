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

import com.github.cpthack.commons.ratelimiter.config.CustomRateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;

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
	
	private final static Logger	logger = LoggerFactory.getLogger(LockTest.class);
	
	private static Lock			lock   = null;
	
	public static void main(String[] args) {
		RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();
		lock = new SimpleLock(rateLimiterConfig);
		simulateConcurrentThread(); // 模拟并发线程
	}
	
	private static void simulateConcurrentThread() {
		DoThing dt = null;
		Thread t = null;
		for (int i = 0; i < 6; i++) {
			dt = new DoThing("Thread " + i);
			t = new Thread(dt);
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
				if (lock.lock("/lock1")) {// 进行并发控制
					logger.info("Thread Name is [{}] 成功获得锁，正在处理中...", name);
					Thread.currentThread().sleep(2000);
					lock.releaseLock("/test-lock1");
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
