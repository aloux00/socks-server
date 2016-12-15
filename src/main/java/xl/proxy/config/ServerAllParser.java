package xl.proxy.config;

import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import xl.share.config.utils.parser.impl.AbstractYamlParser;


public class ServerAllParser<T> extends AbstractYamlParser<T> {

	@Override
	public T parse(InputStream is, Class<T> c) {
		try {
			Yaml yaml = new Yaml();
			T configuration = yaml.loadAs(is, c);
			return configuration;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
