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
package com.github.cpthack.commons.ratelimiter.base;

import java.util.HashMap;
import java.util.Map;

import com.cpthack.commons.rdclient.config.RedisConfig;
import com.github.cpthack.commons.ratelimiter.config.RateLimiterConfig;
import com.github.cpthack.commons.ratelimiter.constants.RateLimiterConstants;
import com.github.cpthack.commons.ratelimiter.utils.ProxyHelper;

/**
 * <b>AbstractBaseFactory.java</b></br>
 * 
 * <pre>
 * 抽象基础工厂类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 24, 2017 12:32:49 PM
 * @since JDK 1.7
 */
public abstract class AbstractBaseFactory<T> {
	/**
	 * 单机实现 实例集合
	 */
	private static Map<String, Object> singleMap		 = new HashMap<String, Object>();
	
	/**
	 * 分布式实现 实例集合
	 */
	private static Map<String, Object> distributedMap = new HashMap<String, Object>();
	
	private Class<T>				   singleClass;
	private Class<T>				   distributedClass;
	
	@SuppressWarnings("unchecked")
	public AbstractBaseFactory(Class<?> singleClass, Class<?> distributedClass) {
		this.singleClass = (Class<T>) singleClass;
		this.distributedClass = (Class<T>) distributedClass;
	}
	
	@SuppressWarnings("hiding")
	public <T> T single() {
		return single(null);
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T> T single(RateLimiterConfig rateLimiterConfig) {
		T limiter = null;
		if (null == rateLimiterConfig) {// 如果配置变量为空，则启用默认配置，默认配置需要依赖:rate-limiter.properties文件
			String rateLimiterDefaultConfigName = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;
			limiter = (T) singleMap.get(rateLimiterDefaultConfigName);
			if (null != limiter) {
				return limiter;
			}
			limiter = (T) ProxyHelper.getInstance(singleClass);
			
			singleMap.put(rateLimiterDefaultConfigName, limiter);
			return limiter;
		}
		
		limiter = (T) singleMap.get(rateLimiterConfig.getConfigFile());
		if (null != limiter) {
			return limiter;
		}
		limiter = (T) ProxyHelper.getInstance(singleClass, new Class[] { RateLimiterConfig.class }, new Object[] { rateLimiterConfig });
		singleMap.put(rateLimiterConfig.getConfigFile(), limiter);
		return limiter;
	}
	
	@SuppressWarnings("hiding")
	public <T> T distributed() {
		return distributed(null);
	}
	
	@SuppressWarnings("hiding")
	public <T> T distributed(RateLimiterConfig rateLimiterConfig) {
		return distributed(rateLimiterConfig, null);
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T> T distributed(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
		T limiter = null;
		if (null == rateLimiterConfig) {// 如果配置变量为空，则启用默认配置，默认配置需要依赖:rate-limiter.properties文件
			String rateLimiterDefaultConfigName = RateLimiterConstants.RATE_LIMITER_CONFIG_FILE;
			limiter = (T) distributedMap.get(rateLimiterDefaultConfigName);
			if (null != limiter) {
				return limiter;
			}
			limiter = (T) ProxyHelper.getInstance(distributedClass);
			distributedMap.put(rateLimiterDefaultConfigName, limiter);
			return limiter;
		}
		
		limiter = (T) distributedMap.get(rateLimiterConfig.getConfigFile());
		if (null != limiter)
			return limiter;
		limiter = (T) ProxyHelper.getInstance(distributedClass, new Class[] { RateLimiterConfig.class, RedisConfig.class }, new Object[] { rateLimiterConfig, redisConfig });
		distributedMap.put(rateLimiterConfig.getConfigFile(), limiter);
		return limiter;
	}
}
