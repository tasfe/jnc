package com.wuda.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * html解析工具类.
 * </p>
 * 
 * @author wuda
 * @since jdk 1.6
 * 
 */
public class HtmlParser {

	/**
	 * <p>
	 * A标签正则表达式模式
	 * </p>
	 */
	public final static Pattern TAG_A = Pattern
			.compile("(?i)<a.*?href=(\"|')(.+?)(\"|').*?>(.*?)</a>");

	/**
	 * 字符集的正则表达式模式
	 */
	public final static Pattern CHARSET = Pattern
			.compile("(?i)<META(.*?)charset=\"?(.+?)\"?\\s?/?>");

	/**
	 * 标题title的正则表达式模式
	 */
	public final static Pattern TITLE = Pattern
			.compile("(?i)<title>(.+?)</title>");

	/**
	 * keywords的正则表达式模式
	 */
	public final static Pattern KEYWORDS = Pattern
			.compile("(?s)(?i)<meta\\sname=\"keywords\".*?content=\"(.+?)\".*?>");

	/**
	 * description的正则表达式模式
	 */
	public final static Pattern DESCRIPTION = Pattern
			.compile("(?s)(?i)<meta\\sname=\"description\".*?content=\"(.+?)\".*?>");

	/**
	 * script的正则表达式模式
	 */
	public final static Pattern SCRIPT = Pattern
			.compile("(?s)(?i)<script.+?</script>");

	/**
	 * html注释的正则表达式模式
	 */
	public final static Pattern HTML_NOTE = Pattern
			.compile("(?s)(?i)<!--.*?-->");

	/**
	 * html标签的正则表达式模式
	 */
	public final static Pattern HTML_TAG = Pattern
			.compile("(?s)(?i)<(/?|!?)[a-z]+.*?>");

	/**
	 * 一些特殊字符的正则表达式模式
	 */
	public final static Pattern SPECIAL_CHAR = Pattern
			.compile("\\t|\\n|\\r|\\s|&nbsp;|&copy;");

	/**
	 * <p>
	 * 从给定的html内容中获得所有的<a>标签中的url
	 * </p>
	 * 
	 * @param htmlContent
	 *            html内容
	 * @return urls 或者null-如果没有url
	 */
	public static Set<UrlBean> getUrls(String htmlContent) {
		return getUrls(htmlContent, null);
	}

	/**
	 * <p>
	 * 从给定的html内容中获得所有的<a>标签中的url,但是url中不能出现<i>uncontains</i>中包含的值,比如
	 * 不想收集有"?"的url
	 * </p>
	 * 
	 * @param htmlContent
	 *            html内容
	 * @param uncontains
	 *            url中不能出现的内容
	 * @return urls 或者null-如果没有url
	 */
	public static Set<UrlBean> getUrls(String htmlContent,
			Set<String> uncontains) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		Set<UrlBean> urls = new HashSet<UrlBean>();
		Matcher matcher = TAG_A.matcher(htmlContent);
		String url = null;
		while (matcher.find()) {
			url = matcher.group(2);
			if (!StringUtil.isNullOrEmpty(url)) {
				url = url.toLowerCase();
				boolean contains = false;
				if (uncontains != null && !uncontains.isEmpty()) {
					for (String uc : uncontains) {
						if (url.indexOf(uc.toLowerCase()) != -1) {
							contains = true;
							break;
						}
					}
				}
				if (!contains) {
					urls.add(UrlUtil.getUrlBean(url));
				}
			}
		}
		return urls;
	}

	/**
	 * <p>
	 * 获得页面中<i>charset</i>标签的值
	 * </p>
	 * 
	 * @param htmlContent
	 *            网页内容
	 * @return <i>charset</i>标签的值
	 */
	public static String getCharset(String htmlContent) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		Matcher matcher = CHARSET.matcher(htmlContent);
		if (matcher.find()) {
			return matcher.group(2).trim();
		}
		return null;
	}

	/**
	 * <p>
	 * 获得页面中<i>title</i>标签的值
	 * </p>
	 * 
	 * @param htmlContent
	 *            网页内容
	 * @return <i>title</i>标签的值
	 */
	public static String getTitle(String htmlContent) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		Matcher matcher = TITLE.matcher(htmlContent);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}

	/**
	 * <p>
	 * 获得页面中<i>keywords</i>标签的值
	 * </p>
	 * 
	 * @param htmlContent
	 *            网页内容
	 * @return <i>keywords</i>标签的值
	 */
	public static String getKeywords(String htmlContent) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		Matcher matcher = KEYWORDS.matcher(htmlContent);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}

	/**
	 * <p>
	 * 获得页面中<i>description</i>标签的值
	 * </p>
	 * 
	 * @param htmlContent
	 *            网页内容
	 * @return <i>description</i>标签的值
	 */
	public static String getDescription(String htmlContent) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		Matcher matcher = DESCRIPTION.matcher(htmlContent);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}

	/**
	 * 去除html标签,获取纯文本内容
	 * 
	 * @return 去除html标签后的文本内容
	 */
	public static String getTextContent(String htmlContent) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		Matcher matcher = SCRIPT.matcher(htmlContent);
		htmlContent = matcher.replaceAll("");
		matcher = HTML_NOTE.matcher(htmlContent);
		htmlContent = matcher.replaceAll("");
		matcher = HTML_TAG.matcher(htmlContent);
		htmlContent = matcher.replaceAll("");
		matcher = SPECIAL_CHAR.matcher(htmlContent);
		htmlContent = matcher.replaceAll("");
		return htmlContent;
	}
}
