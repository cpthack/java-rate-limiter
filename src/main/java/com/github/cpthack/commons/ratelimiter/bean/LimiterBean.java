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
 * <b>LimiterBean.java</b></br>
 * 
 * <pre>
 * Limiter限流的JavaBean对象
 * 表示：在${time} 秒内允许通过${count}次访问
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 9:40:08 PM
 * @since JDK 1.7
 */
public class LimiterBean {
	
	private String router;
	
	private int	   time;
	
	private int	   count;
	
	/**
	 * 限流的路由地址
	 */
	public String getRouter() {
		return router;
	}
	
	public void setRouter(String router) {
		this.router = router;
	}
	
	/**
	 * 限流时间，单位为 秒(s)
	 */
	public long getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	/**
	 * 限流数量
	 */
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
}
