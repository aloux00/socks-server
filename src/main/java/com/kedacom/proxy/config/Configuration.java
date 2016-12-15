package com.kedacom.proxy.config;

import com.kedacom.share.config.utils.parser.IParser;

/**
 * 
 * 〈所有的配置〉<br />
 * 〈功能详细描述〉
 * 
 * @author: yinzhangniu
 * @since 2015-4-1 下午1:19:30
 * @version: ptt1.0.0
 */
public class Configuration<T> {
	private static final String DEFAULT_FILENAME = "config.yml";
	private T config = null;

	public Configuration(Class<T> c) {
		this(DEFAULT_FILENAME, c);
	}

	public Configuration(String filePath, Class<T> c) {
		IParser<T> connectorParser = new ServerAllParser<T>();
		config = connectorParser.parse(filePath, c);
	}

	public T get() {
		return this.config;
	}
}
