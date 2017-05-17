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
import java.util.Map;

import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.constants.RateLimiterConstants;

/**
 * <b>LimiterFactory.java</b></br>
 * 
 * <pre>
 * 限流工厂类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 17, 2017 12:18:47 AM
 * @since JDK 1.7
 */
public class LimiterFactory {
	/**
	 * 单机限流实现 实例集合
	 */
	private static Map<String, Limiter>	singleLimiterMap	  = new HashMap<String, Limiter>();
	/**
	 * 分布式限流实现 实例集合
	 */
//	private static Map<String, Limiter>	distributedLimiterMap = new HashMap<String, Limiter>();
	
	
	public Limiter single(){
		return single(null);
	}
	
	public Limiter single(RateLimiterConfig rateLimiterConfig) {
		Limiter limiter = null;
		if (null == rateLimiterConfig) {
			String rateLimiterDefaultConfigName = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;
			limiter = singleLimiterMap.get(rateLimiterDefaultConfigName);
			if (null != limiter) {
				return limiter;
			}
			limiter = new SingleLimiter();
			singleLimiterMap.put(rateLimiterDefaultConfigName, limiter);
			return limiter;
		}
		
		limiter = singleLimiterMap.get(rateLimiterConfig.getConfigFile());
		if (null != limiter) {
			return limiter;
		}
		limiter = new SingleLimiter(rateLimiterConfig);
		singleLimiterMap.put(rateLimiterConfig.getConfigFile(), limiter);
		return limiter;
	}
	
}
