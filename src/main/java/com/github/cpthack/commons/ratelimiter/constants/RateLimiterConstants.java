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
package com.github.cpthack.commons.ratelimiter.constants;

/**
 * <b>RateLimiterConstants.java</b></br>
 * 
 * <pre>
 * 常量配置类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 4:39:24 PM
 * @since JDK 1.7
 */
public class RateLimiterConstants {
	
	/**
	 * 默认的配置文件名称
	 */
	public final static String RATE_LIMITER_CONFIG_FILE		  = "rate-limiter.properties";
	
	/**
	 * 默认的限流任务总数
	 */
	public final static int	   MAX_RATE_LIMITER_LIMITER_LIMIT = 10;
	
	/**
	 * 默认的并发控制任务总数
	 */
	public final static int	   MAX_RATE_LIMITER_LOCK_LIMIT	  = 10;
	
}
