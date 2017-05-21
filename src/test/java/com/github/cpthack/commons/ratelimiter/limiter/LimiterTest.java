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
package com.github.cpthack.commons.ratelimiter.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cpthack.commons.rdclient.config.RedisConfig;
import com.github.cpthack.commons.ratelimiter.config.CustomRateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.config.RateRedisConfig;

/**
 * <b>LimiterTest.java</b></br>
 * 
 * <pre>
 * 限流测试类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 18, 2017 11:13:25 PM
 * @since JDK 1.7
 */
public class LimiterTest {
	private final static Logger	logger		= LoggerFactory.getLogger(LimiterTest.class);
	private static Limiter		limiter		= null;
	private volatile static int	maxNum		= 0;
	private final static String	ROUTER_NAME	= "/thread-test";
	
	public static void main(String[] args) {
		RedisConfig redisConfig = new RateRedisConfig();
		RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();
		
		singleLimiter(rateLimiterConfig);// 实例化单机限流对象
		
		// DistributedLimiter(rateLimiterConfig, redisConfig); // 示例化分布式限流对象
		
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
	
	private static void singleLimiter(RateLimiterConfig rateLimiterConfig) {
		limiter = LimiterFactory.single(rateLimiterConfig);
	}
	
	private static void DistributedLimiter(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
		limiter = LimiterFactory.distributed(rateLimiterConfig, redisConfig);
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
				for (int i = 0; i < 20; i++) {
					
					if (!limiter.execute(ROUTER_NAME, 4, 1)) {// 进行限流控制
						
						logger.info("Thread Name is [{}]，调用频率太高了.", name);
						Thread.currentThread().sleep(1000);
						continue;
					}
					maxNum++;
					logger.info("Thread Name is [{}]，最新maxNum的值 = [" + maxNum + "]", name);
				}
			}
			catch (InterruptedException e) {
				logger.error("Thread Name [{}] is Error.", name, e);
			}
		}
	}
	
}
