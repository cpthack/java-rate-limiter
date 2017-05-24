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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.cpthack.commons.ratelimiter.bean.LockBean;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.constants.RateLimiterConstants;

/**
 * <b>SingleLock.java</b></br>
 * 
 * <pre>
 * 单机版并发锁 实现类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 22, 2017 1:31:31 AM
 * @since JDK 1.7
 */
public class SingleLock implements Lock {
	
	private final static Logger logger = LoggerFactory.getLogger(SingleLock.class);
	private static Map<String, Semaphore> lockMap = null;
	
	public SingleLock() {
		this(null);
	}
	
	public SingleLock(RateLimiterConfig rateLimiterConfig) {
		if (null == rateLimiterConfig) {
			rateLimiterConfig = new RateLimiterConfig();
		}
		initLockMap(rateLimiterConfig);
	}
	
	protected void initLockMap(RateLimiterConfig rateLimiterConfig) {
		if (null != lockMap)
			return;
		lockMap = new HashMap<String,Semaphore>();
		List<LockBean> lockList = rateLimiterConfig.getLockList();
		Semaphore semaphore = null;
		for(LockBean lockBean : lockList){
			semaphore = new Semaphore(lockBean.getPermits());
			lockMap.put(lockBean.getUniqueKey(), semaphore);
			logger.debug("单机并发锁-加载并发配置>>>uniqueKey = [{}],time = [{}],count = [{}]",lockBean.getUniqueKey(), lockBean.getExpireTime(), lockBean.getPermits());
		}
	}
	
	@Override
	public boolean lock(String uniqueKey) {
		return lock(uniqueKey, RateLimiterConstants.LOCK_DEFAULT_EXPIRE_TIME);
	}
	
	@Override
	public boolean lock(String uniqueKey, int expireTime) {
		return lock(uniqueKey, expireTime, RateLimiterConstants.LOCK_DEFAULT_PERMITS_NUM);
	}
	
	@Override
	public boolean lock(String uniqueKey, int expireTime, int permits) {
		Semaphore semaphore = lockMap.get(uniqueKey);
		
		if (null != semaphore)
			return semaphore.tryAcquire();
		
		boolean isGetLock = dynamicAddLock(uniqueKey, expireTime, permits);
		
		return isGetLock;
	}
	
	/**
	 * 
	 * <b>dynamicAddLock</b> <br/>
	 * <br/>
	 * 
	 * 动态添加锁配置<br/>
	 * 当多个线程同时进行动态配置时会发生并发问题，所以需要利用常量池特性[ uniqueKey.intern() ]进行仅同一路由加锁。
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param uniqueKey
	 *            加锁唯一键标识
	 * @param expireTime
	 *            过期时间，单位秒
	 * @param permits
	 *            许可数量(最大并发量，默认为1)
	 * @return 加锁/获取锁成功则返回true，否则返回false
	 *
	 */
	public boolean dynamicAddLock(String uniqueKey, int expireTime, int permits) {
		synchronized (uniqueKey.intern()) {
			Semaphore semaphore = lockMap.get(uniqueKey);
			if (null != semaphore)
				return semaphore.tryAcquire();
			semaphore = new Semaphore(permits);
			lockMap.put(uniqueKey, semaphore);
			return semaphore.tryAcquire();
		}
	}
	
	@Override
	public boolean releaseLock(String uniqueKey) {
		Semaphore semaphore = lockMap.get(uniqueKey);
		if (null != semaphore)
			semaphore.release();
		return true;
	}
	
}
