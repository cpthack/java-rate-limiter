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
package com.github.cpthack.commons.ratelimiter.utils;

/**
 * <b>ProxyHelper.java</b></br>
 * 
 * <pre>
 * 代理工具类，动态生成类实例
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 24, 2017 12:24:17 PM
 * @since JDK 1.7
 */
public class ProxyHelper {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getInstance(Class<T> target, Class[] args, Object[] argsValue) {
		try {
			Class c = Class.forName(target.getName());
			java.lang.reflect.Constructor constructor = c.getConstructor(args);
			return (T) constructor.newInstance(argsValue);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getInstance(Class<T> target) {
		try {
			Class c = Class.forName(target.getClass().getName());
			return (T) c.newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
