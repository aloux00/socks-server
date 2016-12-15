package xl.share.config.utils.parser;

import java.io.File;
import java.io.InputStream;

/**
 * 
 * 〈文件解析通用接口〉<br />
 * 〈功能详细描述〉
 * 
 * @param <T>
 * @author: yinzhangniu
 * @since 2015-4-1 下午1:43:34
 * @version: ptt1.0.0
 */
public interface IParser<T> {

	public T parse(String fileName,Class<T> c);

	public T parse(File file,Class<T> c);

	public T parse(InputStream is,Class<T> c);

}
