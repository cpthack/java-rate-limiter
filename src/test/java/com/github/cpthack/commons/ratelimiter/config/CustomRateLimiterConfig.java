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

/**
 * <b>CustomRateLimiterConfig.java</b></br>
 * 
 * <pre>
 * 自定义限流配置类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 18, 2017 11:26:46 PM
 * @since JDK 1.7
 */
public class CustomRateLimiterConfig extends RateLimiterConfig {
	
	private final String FILE_NAME = "test-rate-limiter.properties";
	
	@Override
	public String getConfigFile() {
		return FILE_NAME;
	}
	
}
