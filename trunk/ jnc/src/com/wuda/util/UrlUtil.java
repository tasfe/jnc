package com.wuda.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 对url操作的工具类
 * </p>
 * 
 * @author wuda
 * 
 */
public class UrlUtil {

	/**
	 * <p>
	 * 域名后缀
	 * </p>
	 */
	public final static String DOMAIN_SUFFIX = "(\\.com\\.cn|\\.org\\.cn|\\.com\\.hk|\\.net\\.cn|\\.com|\\.cn|\\.net|\\.org|\\.hk|\\.pw|\\.cc|\\.name|\\.in|\\.mobi|\\.asia|\\.cm|\\.中国|\\.公司|\\.网络|\\.香港)";

	/**
	 * <p>
	 * 图片格式后缀
	 * </p>
	 */
	public final static String PHOTO_SUFFIX = "(\\.jpg|\\.jpeg|\\.gif|\\.png|\\.bmp)";

	/**
	 * <p>
	 * 标准URL模式
	 * </p>
	 */
	public final static Pattern URL_PATTERN = Pattern
			.compile("(?i)((http|https)://)?(www\\.)?([^/]+?" + DOMAIN_SUFFIX
					+ ")?(/[^\\?]*)?(\\?(.*))?");

	/**
	 * <p>
	 * 根据url生成{@link UrlBean}
	 * </p>
	 * 
	 * @param url
	 *            url,可以是相对或者绝对路径
	 * @return {@link UrlBean},如果为null则表示不是标准的url
	 */
	public static UrlBean getUrlBean(String url) {
		if (StringUtil.isNullOrEmpty(url)) {
			return null;
		}
		url = url.toLowerCase();
		Matcher matcher = URL_PATTERN.matcher(url);
		UrlBean bean = null;
		if (matcher.find()) {
			bean = new UrlBean();
			bean.setUrl(url);
			bean.setScheme(matcher.group(2));
			bean.setHost(matcher.group(4)); //不含"www."
			bean.setPath(matcher.group(6));
			bean.setQueryString(matcher.group(8));
		}
		return bean;
	}

}
