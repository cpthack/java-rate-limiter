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
package com.github.cpthack.commons.ratelimiter.bean;

/**
 * <b>LockBean.java</b></br>
 * 
 * <pre>
 * Limiter并发锁的JavaBean对象
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 22, 2017 11:09:31 AM
 * @since JDK 1.7
 */
public class LockBean {
	
	private String uniqueKey;
	
	private int	   expireTime;
	
	private int	   permits;
	
	/**
	 * 加锁唯一键标识
	 */
	public String getUniqueKey() {
		return uniqueKey;
	}
	
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	
	/**
	 * 过期时间，单位秒
	 */
	public int getExpireTime() {
		return expireTime;
	}
	
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	
	/**
	 * 许可数量(最大并发量，默认为1)
	 */
	public int getPermits() {
		return permits;
	}
	
	public void setPermits(int permits) {
		this.permits = permits;
	}
	
}
