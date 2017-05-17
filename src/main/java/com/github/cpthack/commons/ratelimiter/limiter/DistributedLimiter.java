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
 * <b>DistributedLimiter.java</b></br>
 * 
 * <pre>
 * 限流接口 - 分布式实现
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 6:03:13 PM
 * @since JDK 1.7
 */
public class DistributedLimiter implements Limiter {
	
	@Override
	public boolean execute(String routerName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean execute(String routerName, int limitCount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean execute(String routerName, int limitCount, long time) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
