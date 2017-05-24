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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cpthack.commons.rdclient.config.RedisConfig;
import com.cpthack.commons.rdclient.core.RedisClient;
import com.cpthack.commons.rdclient.core.RedisClientFactory;
import com.github.cpthack.commons.ratelimiter.bean.LockBean;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.constants.RateLimiterConstants;

/**
 * <b>DistributedLock.java</b></br>
 * 
 * <pre>
 * 分布式锁 实现类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 22, 2017 12:41:02 PM
 * @since JDK 1.7
 */
public class DistributedLock implements Lock {
	
	private final static Logger			 logger		 = LoggerFactory.getLogger(DistributedLock.class);
	
	private static RedisClient<?>		 redisClient = null;
	private static Map<String, LockBean> lockBeanMap = null;
	
	public DistributedLock() {
		this(null, null);
	}
	
	public DistributedLock(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
		if (null == rateLimiterConfig)
			rateLimiterConfig = new RateLimiterConfig();
		initLockConfig(rateLimiterConfig, redisConfig);
	}
	
	/**
	 * 
	 * <b>initLockConfig</b> <br/>
	 * <br/>
	 * 
	 * 初始化并发锁配置<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param rateLimiterConfig
	 * @param redisConfig
	 *            void
	 *
	 */
	protected void initLockConfig(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
		if (null != redisClient)	// 当redisClient不为空，意味着限流配置已经初始化到缓存中
			return;
		redisClient = RedisClientFactory.getClient(redisConfig);
		lockBeanMap = new HashMap<String, LockBean>();
		
		List<LockBean> lockList = rateLimiterConfig.getLockList();
		for (LockBean lockBean : lockList) {
			logger.debug("分布式并发锁-加载并发配置>>>uniqueKey = [{}],time = [{}],count = [{}]", lockBean.getUniqueKey(), lockBean.getExpireTime(), lockBean.getPermits());
			redisClient.setnx(lockBean.getUniqueKey(), "0", lockBean.getExpireTime());
			lockBeanMap.put(lockBean.getUniqueKey(), lockBean);
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
	
	private LockBean getLockBean(String uniqueKey, int expireTime, int permits) {
		LockBean lockBean = lockBeanMap.get(uniqueKey);
		if (null == lockBean) {
			lockBean = new LockBean();
			lockBean.setUniqueKey(uniqueKey);
			lockBean.setExpireTime(expireTime);
			lockBean.setPermits(permits);
			lockBeanMap.put(uniqueKey, lockBean);
		}
		return lockBean;
	}
	
	@Override
	public boolean lock(String uniqueKey, int expireTime, int permits) {
		LockBean lockBean = getLockBean(uniqueKey, expireTime, permits);
		expireTime = lockBean.getExpireTime();
		permits = lockBean.getPermits();
		long result = redisClient.setnx(uniqueKey, "0", lockBean.getExpireTime());
		result = redisClient.incr(uniqueKey);
		
		// if (result <= 0) {// 应对因当releaseLock操作执行多次等问题而导致缓存中驻留着value小于0的数据
		// logger.warn("并发锁，检测到当前KEY = [{}] ,VALUE = [{}] ,强制执行清除命令，便于后续请求顺利进行.", uniqueKey,
		// result);
		// return lock(uniqueKey, expireTime, permits);
		// }
		
		if (result > permits) {
			redisClient.decr(uniqueKey);
			logger.warn("并发锁，检测到当前KEY = [{}] , VALUE = [{}] , permits = [{}]，超过许可范围，因而获取锁失败.", uniqueKey, result, permits);
			return false;
		}
		
		if (result > 0 && result <= permits) {
			logger.info("并发锁，检测到当前KEY = [{}] , VALUE = [{}] , permits = [{}]，满足许可范围，因为成功获得锁.", uniqueKey, result, permits);
			return true;
		}
		
		logger.debug("并发锁，检测到当前KEY = [{}] , VALUE = [{}] , permits = [{}]，异常逻辑导致获得锁失败.", uniqueKey, result, permits);
		return false;
	}
	
	@Override
	public boolean releaseLock(String uniqueKey) {
		LockBean lockBean = lockBeanMap.get(uniqueKey);
		if (null == lockBean) {
			// TODO 抛出异常
			return false;
		}
		long result = redisClient.setnx(uniqueKey, "0", lockBean.getExpireTime());
		if (result == 0) {
			redisClient.decr(uniqueKey);
		}
		return true;
	}
	
}
