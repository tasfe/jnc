package com.wuda.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.wuda.util.HtmlParser;
import com.wuda.util.StringUtil;

/**
 * <p>
 * 页面下载类
 * </p>
 * 
 * @author wuda
 * 
 */
public class PageDownload {

	public static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * <p>
	 * 根据url,执行http的get请求,下载页面内容,此方法会自动判断网页的字符集,如果已经知道了网页的字符集,可以使用
	 * {@link #getContent(String, String)}方法,效率会更高
	 * </p>
	 * 
	 * @param url
	 *            需要下载的页面的url
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String getContent(String url) throws ParseException,
			IOException {
		if (StringUtil.isNullOrEmpty(url)) {
			return null;
		}
		HttpGet httpGet = null;
		byte[] bytes = null;
		HttpClient client = new DefaultHttpClient();
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = client.execute(httpGet);
			return getContent(httpResponse);
		} finally {
			if (bytes != null) {
				bytes = null;
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}

	}

	/**
	 * <p>
	 * 下载页面内容,此方法会自动判断网页的字符集
	 * </p>
	 * 
	 * @param response
	 *            HttpResponse
	 * @throws IOException
	 */
	public static String getContent(HttpResponse response) throws IOException {
		byte[] bytes = null;
		try {
			HttpEntity entity = response.getEntity();
			String charset = getCharsetFromHeader(entity.getContentType()); // 从相应头中获取字符集
			if (!StringUtil.isNullOrEmpty(charset)) { // 响应头中有字符集
				return EntityUtils.toString(entity, charset);
			} else {
				bytes = EntityUtils.toByteArray(entity);// 获得网页内容的字节数组
				charset = getCharsetFromHtmlMeta(bytes); // 从页面内容中获取字符集
				if (charset == null) {
					charset = DEFAULT_CHARSET;
				}
				return new String(bytes, charset);
			}
		} finally {
			if (bytes != null) {
				bytes = null;
			}
		}

	}

	/**
	 * <p>
	 * 根据url,执行http的get请求,下载页面内容
	 * </p>
	 * 
	 * @param url
	 *            需要下载的页面的url
	 * @param charset
	 *            字符集
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String getContent(String url, String charset)
			throws ClientProtocolException, IOException {
		if (StringUtil.isNullOrEmpty(url)) {
			return null;
		}
		HttpGet httpGet = null;
		HttpClient client = new DefaultHttpClient();
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = client.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			return EntityUtils.toString(entity, charset);
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}

	}

	/**
	 * <p>
	 * 根据url,执行http的get请求,获得状态行信息
	 * </p>
	 * 
	 * @param url
	 *            需要下载的页面的url
	 */
	public static StatusLine getStatusLine(String url)
			throws ClientProtocolException, IOException {
		if (StringUtil.isNullOrEmpty(url)) {
			return null;
		}
		HttpGet httpGet = null;
		HttpClient client = new DefaultHttpClient();
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = client.execute(httpGet);
			return httpResponse.getStatusLine();
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}

	}

	/**
	 * <p>
	 * 获得网页的字符集
	 * </p>
	 * 
	 * @param url
	 *            url
	 * @return 字符集
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getCharset(String url) throws ClientProtocolException,
			IOException {
		if (StringUtil.isNullOrEmpty(url)) {
			return null;
		}
		HttpGet httpGet = null;
		byte[] bytes = null;
		HttpClient client = new DefaultHttpClient();
		try {
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = client.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			String charset = getCharsetFromHeader(entity.getContentType()); // 从相应头中获取字符集
			if (charset == null) {
				bytes = EntityUtils.toByteArray(entity);// 获得网页内容的字节数组
				charset = getCharsetFromHtmlMeta(bytes); // 从页面内容中获取字符集
			}
			return charset;
		} finally {
			if (bytes != null) {
				bytes = null;
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	/**
	 * <p>
	 * 从响应头content-type中获取字符集
	 * </p>
	 */
	private static String getCharsetFromHeader(Header contentType) {
		Pattern pattern = Pattern.compile("charset=(.+)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentType.getValue());
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}

	/**
	 * <p>
	 * 从网页内容的<meta>标签中获取字符集
	 * </p>
	 * 
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	private static String getCharsetFromHtmlMeta(byte[] bytes)
			throws IllegalStateException, IOException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		int length = bytes.length;
		int size = 1024;
		if (length < 1024) {
			size = length;
		}
		byte[] bytesFoGetCharset = new byte[size];
		System.arraycopy(bytes, 0, bytesFoGetCharset, 0, size);
		String content = new String(bytesFoGetCharset);
		return HtmlParser.getCharset(content);
	}

	/**
	 * <p>
	 * 判断响应头<i>Content-Type</i>是否为<i>text/html</i>
	 * </p>
	 * 
	 * @param response
	 *            HttpResponse
	 * @return true-如果是
	 */
	public static boolean isTextHtml(HttpResponse response) {
		Header header = response.getEntity().getContentType();
		if (header == null) {
			return false;
		}
		String contentType = header.getValue();
		if (StringUtil.isNullOrEmpty(contentType)) {
			return false;
		}
		return contentType.toLowerCase().contains("text/html");
	}

}
