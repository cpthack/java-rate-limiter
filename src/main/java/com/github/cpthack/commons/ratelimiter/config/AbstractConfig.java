package com.github.cpthack.commons.ratelimiter.config;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <b>AbstractConfig.java</b></br>
 * 
 * <pre>
 * 基础配置加载抽象类
 * </pre>
 *
 * @author cpthack cpt@jianzhimao.com
 * @date May 16, 2017 5:00:31 PM
 * @since JDK 1.7
 */
public abstract class AbstractConfig {
	private static Logger logger		   = LoggerFactory.getLogger(AbstractConfig.class);
	private Properties	  configProperties = null;
	
	public synchronized boolean reloadConfig() {
		logger.debug("Base config reloadConfig().");
		if (configProperties != null) {
			configProperties.clear();
		}
		String file = getConfigFile();
		configProperties = FileUtils.loadStaticProperties(file);
		if (configProperties == null) {
			return false;
		}
		return true;
	}
	
	public String getProperty(String key) {
		if (configProperties == null) {
			return null;
		}
		return configProperties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultV) {
		String strValue = defaultV;
		String temp = getProperty(key);
		if (StringUtils.isNotBlank(temp)) {
			strValue = temp;
		}
		return strValue;
	}
	
	public int getPropertyToInt(String key, int defaultV) {
		int intValue = defaultV;
		String temp = getProperty(key);
		try {
			intValue = Integer.parseInt(temp);
		}
		catch (NumberFormatException e) {
		}
		return intValue;
	}
	
	// 子类需指定配置文件名
	public abstract String getConfigFile();
}