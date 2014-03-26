package com.wuda.util;

/**
 * <p>
 * url bean
 * </p>
 * 
 * @author wuda
 * 
 */
public class UrlBean {

	private String url = ""; // 完整的url字符串
	private String scheme = "http"; // 协议
	private String host = ""; // host
	private String path = "/"; // 路径
	private String queryString = ""; // 查询字符串

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url == null) {
			return;
		}
		this.url = url;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		if (scheme == null) {
			return;
		}
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (host == null) {
			return;
		}
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (path==null || path.isEmpty()) {
			return;
		}
		this.path = path;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		if (queryString == null) {
			return;
		}
		this.queryString = queryString;
	}

	/**
	 * 是否相对路径
	 * 
	 * @return 如果host值为空则true,反正false
	 */
	public boolean isRelative() {
		return StringUtil.isNullOrEmpty(host);
	}

	/**
	 * 就如名字一样,只保留这个url的scheme和host
	 */
	public void onlyLeftSchemeAndHost() {
		url = "";
		path = "/";
		queryString = "";
	}

	@Override
	public boolean equals(Object url) {
		if (url instanceof UrlBean) {
			if (!this.getScheme().equalsIgnoreCase(((UrlBean) url).getScheme())) {
				return false;
			}
			if (!this.getHost().equalsIgnoreCase(((UrlBean) url).getHost())) {
				return false;
			}
			if (!this.getPath().equalsIgnoreCase(((UrlBean) url).getPath())) {
				return false;
			}
			if (!this.getQueryString().equalsIgnoreCase(
					((UrlBean) url).getQueryString())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (scheme + host + path + queryString).toLowerCase().hashCode();
	}

}
