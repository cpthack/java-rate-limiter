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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private final static Logger		 logger	= LoggerFactory.getLogger(SingleLimiter.class);
	
	private Map<String, RateLimiter> rateLimiterMap;
	
	public SingleLimiter() {
		this(null);
	}
	
	public SingleLimiter(RateLimiterConfig rateLimiterConfig) {
		if (null == rateLimiterConfig) {
			rateLimiterConfig = new RateLimiterConfig();
		}
		initRateLimiterMap(rateLimiterConfig);
	}
	
	public void initRateLimiterMap(RateLimiterConfig rateLimiterConfig) {
		if (null != rateLimiterMap)
			return;
		List<LimiterBean> limiterList = rateLimiterConfig.getLimiterList();
		rateLimiterMap = new HashMap<String, RateLimiter>();
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
		if (null == rateLimiter) {
			rateLimiter = RateLimiter.create(limitCount * 1.0 / time);
			rateLimiterMap.put(routerName, rateLimiter);
			logger.debug("单机限流-动态限流配置>>>router = [{}],time = [{}],count = [{}]", routerName, time, limitCount);
		}
		return rateLimiter.tryAcquire();
	}
	
}
