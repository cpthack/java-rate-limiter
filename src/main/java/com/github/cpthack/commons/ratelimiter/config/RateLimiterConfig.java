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
package com.github.cpthack.commons.ratelimiter.config;

import java.util.ArrayList;
import java.util.List;

import com.github.cpthack.commons.ratelimiter.bean.LimiterBean;
import com.github.cpthack.commons.ratelimiter.constants.RateLimiterConstants;

/**
 * <b>RateLimiterConfig.java</b></br>
 * 
 * <pre>
 * 默认的配置类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 5:01:26 PM
 * @since JDK 1.7
 */
public class RateLimiterConfig extends AbstractConfig {
	
	private final String FILE_NAME					  = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;
	
	private final String MAX_LOCK_TASK_COUNT_NAME	  = "rate.limiter.lock.task.count";
	private final String LOCK_TASK_NAME_PRE			  = "rate.limiter.lock.router.";
	
	private final String MAX_LIMITER_TASK_COUNT_NAME  = "rate.limiter.limiter.task.count";
	private final String LIMITER_TASK_ROUTER_NAME_PRE = "rate.limiter.limiter.router.";
	private final String LIMITER_TASK_TIME_NAME_PRE	  = "rate.limiter.limiter.time.";
	private final String LIMITER_TASK_COUNT_NAME_PRE  = "rate.limiter.limiter.count.";
	
	@Override
	public String getConfigFile() {
		return FILE_NAME;
	}
	
	/**
	 * 
	 * <b>getLockList</b> <br/>
	 * <br/>
	 * 
	 * 从本地配置文件中加载并发配置列表，并组装成List<String>对象<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @return List<String>
	 *
	 */
	public List<String> getLockList() {
		
		int defaultLockListSize = getPropertyToInt(MAX_LOCK_TASK_COUNT_NAME, RateLimiterConstants.MAX_RATE_LIMITER_LOCK_LIMIT);
		List<String> lockList = null;
		String lockValue = null;
		for (int i = 0; i < defaultLockListSize; i++) {
			lockValue = getProperty(LOCK_TASK_NAME_PRE + (i + 1));
			if (null == lockValue)
				continue;
			if (null == lockList)
				lockList = new ArrayList<String>(defaultLockListSize);
			lockList.add(lockValue);
		}
		return lockList;
	}
	
	/**
	 * 
	 * <b>getLimiterList</b> <br/>
	 * <br/>
	 * 
	 * 从本地配置文件中加载限流配置列表，并组装成List<LimiterBean>对象返回<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @return List<LimiterBean>
	 *
	 */
	public List<LimiterBean> getLimiterList() {
		
		int defaultLimiterListSize = getPropertyToInt(MAX_LIMITER_TASK_COUNT_NAME, RateLimiterConstants.MAX_RATE_LIMITER_LIMITER_LIMIT);
		List<LimiterBean> limiterList = null;
		LimiterBean limiterBean = null;
		String limiterRouter = null;
		int limiterTime;
		int limiterCount;
		for (int i = 0; i < defaultLimiterListSize; i++) {
			limiterRouter = getProperty(LIMITER_TASK_ROUTER_NAME_PRE + (i + 1));
			if (null == limiterRouter)
				continue;
			
			limiterBean = new LimiterBean();
			limiterBean.setRouter(limiterRouter);
			
			limiterTime = getPropertyToInt(LIMITER_TASK_TIME_NAME_PRE + (i + 1), Integer.MAX_VALUE);
			limiterBean.setTime(limiterTime);
			
			limiterCount = getPropertyToInt(LIMITER_TASK_COUNT_NAME_PRE + (i + 1), 1);
			limiterBean.setCount(limiterCount);
			
			if (null == limiterList)
				limiterList = new ArrayList<LimiterBean>(defaultLimiterListSize);
			limiterList.add(limiterBean);
		}
		return limiterList;
	}
	
}
