package bugcloud.junit.core.util;

import java.util.Collection;

import org.springframework.util.StringUtils;

/**
 * Url资源拼接工具
 * @author yuzhantao
 *
 */
public class UrlUtils {
	public static String getUrl(Collection<String> cs) {
		StringBuffer sb = new StringBuffer();
		for (String s : cs) {
			if (StringUtils.hasLength(s) == false) {
				continue;
			}
			int len = s.indexOf("/");
			if (len == 0) {
				s = s.substring(1);
			}
			len = s.lastIndexOf("/");
			if (len != s.length() - 1) {
				s += "/";
			}
			sb.append(s);
		}
		return sb.toString();
	}
}
