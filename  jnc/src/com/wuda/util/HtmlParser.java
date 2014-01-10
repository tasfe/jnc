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
			.compile("(?i)<meta\\sname=\"keywords\"\\scontent=\"(.+?)\"\\s?/>");

	/**
	 * description的正则表达式模式
	 */
	public final static Pattern DESCRIPTION = Pattern
			.compile("(?i)<meta\\sname=\"description\"\\scontent=\"(.+?)\"\\s?/>");

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
	 * 从给定的html内容中获得所有的<a>标签中的url,但是url中不能出现<i>uncontains</i>中包含的值,如果<i>
	 * uncontains</i>中包含需要转义的字符,比如 ：问号?,则必须传人已经转义过的\\?,这个方法里面不会执行转义操作
	 * </p>
	 * 
	 * @param htmlContent
	 *            html内容
	 * @param uncontains
	 *            url中不能出现的内容
	 * @return urls 或者null-如果没有url
	 */
	public static Set<UrlBean> getUrls(String htmlContent, Set<String> uncontains) {
		if (StringUtil.isNullOrEmpty(htmlContent)) {
			return null;
		}
		String uncontains_regex = getOrRegex(uncontains);
		StringBuilder tag_a_regex = new StringBuilder();
		tag_a_regex.append("(?i)<a.*?href=(\"|')");
		if (StringUtil.isNullOrEmpty(uncontains_regex) == false) {
			tag_a_regex.append("(((?!" + uncontains_regex + ").)+?)"); // 任何字符，除了xxx
		} else {
			tag_a_regex.append("(.+?)");
		}
		tag_a_regex.append("(\"|').*?>(.*?)</a>");
		Set<UrlBean> urls = new HashSet<UrlBean>();
		Pattern pattern = Pattern.compile(tag_a_regex.toString());
		Matcher matcher = pattern.matcher(htmlContent);
		String url = null;
		while (matcher.find()) {
			url = matcher.group(2);
			if (!StringUtil.isNullOrEmpty(url)) {
				url = url.toLowerCase();
				urls.add(UrlUtil.getUrlBean(url));
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
	 * <p>
	 * 获得“或者”条件
	 * </p>
	 */
	private static String getOrRegex(Set<String> elements) {
		if (elements == null || elements.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String ele : elements) {
			sb.append(ele);
			sb.append("|"); // "或者"分隔符
		}
		return sb.substring(0, sb.length() - 1);
	}

}
