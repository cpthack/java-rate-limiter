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
package com.github.cpthack.commons.ratelimiter.lock;

/**
 * <b>Lock.java</b></br>
 * 
 * <pre>
 * 并发锁接口类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 21, 2017 9:59:03 AM
 * @since JDK 1.7
 */
public interface Lock {
	
	/**
	 * 
	 * <b>lock</b> <br/>
	 * <br/>
	 * 
	 * 加锁<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param uniqueKey
	 *            加锁唯一键标识
	 * @return 加锁/获取锁成功则返回true，否则返回false
	 *
	 */
	boolean lock(String uniqueKey);
	
	/**
	 * 
	 * <b>lock</b> <br/>
	 * <br/>
	 * 
	 * 加锁<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param uniqueKey
	 *            加锁唯一键标识
	 * @param expireTime
	 *            过期时间，单位秒
	 * @return 加锁/获取锁成功则返回true，否则返回false
	 *
	 */
	boolean lock(String uniqueKey, int expireTime);
	
	/**
	 * 
	 * <b>lock</b> <br/>
	 * <br/>
	 * 
	 * 加锁<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param uniqueKey
	 *            加锁唯一键标识
	 * @param expireTime
	 *            过期时间，单位秒
	 * @param permits
	 *            许可数量(最大并发量，默认为1)
	 * @return 加锁/获取锁成功则返回true，否则返回false
	 *
	 */
	boolean lock(String uniqueKey, int expireTime, int permits);
	
	/**
	 * 
	 * <b>releaseLock</b> <br/>
	 * <br/>
	 * 
	 * 释放锁<br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param uniqueKey
	 *            加锁唯一键标识
	 * @return boolean
	 *
	 */
	boolean releaseLock(String uniqueKey);
	
}
