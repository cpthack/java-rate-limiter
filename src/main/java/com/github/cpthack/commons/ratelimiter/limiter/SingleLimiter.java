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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.cpthack.commons.ratelimiter.bean.LimiterBean;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.google.common.util.concurrent.RateLimiter;

/**
 * <b>SingleLimiter.java</b></br>
 * 
 * <pre>
 * 限流接口 - 单机实现
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 6:04:29 PM
 * @since JDK 1.7
 */
public class SingleLimiter implements Limiter {
	
	private final static Logger				logger		   = LoggerFactory.getLogger(SingleLimiter.class);
	
	private static Map<String, RateLimiter>	rateLimiterMap = null;
	
	public SingleLimiter() {
		this(null);
	}
	
	public SingleLimiter(RateLimiterConfig rateLimiterConfig) {
		if (null == rateLimiterConfig) {
			rateLimiterConfig = new RateLimiterConfig();
		}
		initRateLimiterMap(rateLimiterConfig);
	}
	
	/**
	 * 
	 * <b>initRateLimiterMap</b> <br/>
	 * <br/>
	 * 
	 * 初始化限流配置<br/>
	 * 初始化的限流配置在存储在Map中，支持后期动态添加。动态添加路由配置请参考 [method：dynamicAddRouter]
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param rateLimiterConfig
	 *            限流配置
	 *
	 */
	protected void initRateLimiterMap(RateLimiterConfig rateLimiterConfig) {
		if (null != rateLimiterMap)
			return;
		List<LimiterBean> limiterList = rateLimiterConfig.getLimiterList();
		rateLimiterMap = new ConcurrentHashMap<String, RateLimiter>();
		for (LimiterBean limiterBean : limiterList) {
			rateLimiterMap.put(limiterBean.getRouter(), RateLimiter.create(limiterBean.getCount() * 1.0 / limiterBean.getTime()));
			logger.debug("单机限流-加载限流配置>>>router = [{}],time = [{}],count = [{}]", limiterBean.getRouter(), limiterBean.getTime(), limiterBean.getCount());
		}
	}
	
	@Override
	public boolean execute(String routerName) {
		RateLimiter rateLimiter = rateLimiterMap.get(routerName);
		if (null == rateLimiter) {
			return true;
		}
		return rateLimiter.tryAcquire(1);
	}
	
	@Override
	public boolean execute(String routerName, int limitCount) {
		return execute(routerName, limitCount, Integer.MAX_VALUE);
	}
	
	@Override
	public boolean execute(String routerName, int limitCount, int time) {
		RateLimiter rateLimiter = rateLimiterMap.get(routerName);
		if (null != rateLimiter) {
			return rateLimiter.tryAcquire();// 如果限流配置已经存在，则直接进行锁许可证申请
		}
		
		boolean isGetPermit = dynamicAddRouter(routerName, limitCount, time);
		return isGetPermit;
	}
	
	/**
	 * <b>dynamicAddRouter</b> <br/>
	 * <br/>
	 * 当限流配置不存在的时候，需要进行动态限流配置。<br/>
	 * 当多个线程同时进行动态配置时会发生并发问题，所以需要利用常量池特性[ routerName.intern() ]进行仅同一路由加锁。
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param routerName
	 *            路由名称
	 * @param limitCount
	 *            限流数量
	 * @param time
	 *            限流时间，单位是秒
	 * @return boolean
	 *
	 */
	public boolean dynamicAddRouter(String routerName, int limitCount, int time) {
		synchronized (routerName.intern()) {
			RateLimiter rateLimiter = rateLimiterMap.get(routerName);
			if (rateLimiter == null) {
				rateLimiter = RateLimiter.create(limitCount * 1.0 / time);
				rateLimiterMap.put(routerName, rateLimiter);
				logger.info("单机限流-动态限流配置>>>router = [{}],time = [{}],count = [{}]", routerName, time, limitCount);
			}
			else {
				logger.warn("(重复添加限流配置)>>>router = [{}],time = [{}],count = [{}]", routerName, time, limitCount);
			}
			return rateLimiter.tryAcquire();
		}
	}
	
}
