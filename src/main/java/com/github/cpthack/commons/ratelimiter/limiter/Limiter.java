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

/**
 * <b>Limiter.java</b></br>
 * 
 * <pre>
 * 限流接口类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 5:54:57 PM
 * @since JDK 1.7
 */
public interface Limiter {
	
	/**
	 * 
	 * <b>execute</b> <br/>
	 * <br/>
	 * 
	 * 执行限流控制，如果通过则返回true，如果不通过则返回false。<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param routerName
	 *            路由名称
	 * @return boolean
	 *
	 */
	boolean execute(String routerName);
	
	/**
	 * 
	 * <b>execute</b> <br/>
	 * <br/>
	 * 
	 * 执行限流控制，如果通过则返回true，如果不通过则返回false。 如果限流规则不存在，则往规则集合中添加当前限流规则 <br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param routerName
	 *            路由名称
	 * @param limitCount
	 *            限流数量
	 * @return boolean
	 *
	 */
	boolean execute(String routerName, int limitCount);
	
	/**
	 * 
	 * <b>execute</b> <br/>
	 * <br/>
	 * 
	 * 执行限流控制，如果通过则返回true，如果不通过则返回false。 如果限流规则不存在，则往规则集合中添加当前限流规则<br/>
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
	boolean execute(String routerName, int limitCount, int time);
	
}
