package com.kedacom.share.config.utils.parser.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.kedacom.share.config.utils.parser.IParser;

public abstract class AbstractYamlParser<T> implements IParser<T> {

	@Override
	public T parse(String fileName,Class<T> c) {
		File file = new File(fileName);
		return parse(file,c);
	}

	@Override
	public T parse(File file,Class<T> c) {
		try {
			return parse(new FileInputStream(file),c);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("configuration file "
					+ file.getName() + " not found");
		}
	}

	@Override
	public abstract T parse(InputStream is,Class<T> c);
}
