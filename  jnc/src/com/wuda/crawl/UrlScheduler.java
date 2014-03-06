package com.wuda.crawl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.wuda.util.UrlBean;

/**
 * <p>
 * URL调度器.针对固定host下的URL调度,其余的不属于这个host下的URL都只获取<i>scheme</i>和<i>host</i>,然后添加到
 * {@link #GLOBAL_NEW_HOSTS}中,当一个host下的所有URL都抓取完成后,调用{@link #swapHost()}
 * 方法将重新切换一个host
 * </p>
 * 
 * @author wuda
 * 
 */
public class UrlScheduler {

	/**
	 * 新收集的，还未抓取过的HOST
	 */
	private final static LinkedList<UrlBean> GLOBAL_NEW_HOSTS = new LinkedList<UrlBean>();

	/**
	 * 抓取过的HOST
	 */
	private final static Set<UrlBean> GLOBAL_CRALED_HOSTS = new HashSet<UrlBean>();

	/**
	 * 对当前这个host的URL做调度
	 */
	private UrlBean host = null;

	/**
	 * 当前这个host下已经抓取过的url
	 */
	protected Set<UrlBean> CRAWLED_URLS = new HashSet<UrlBean>();

	/**
	 * 当前这个host下待抓取的url
	 */
	protected LinkedList<UrlBean> READY_FOR_CRAWL_URLS = new LinkedList<UrlBean>();

	private static Object lock = new Object();

	/**
	 * <p>
	 * 获取URL让{@link Spider}去抓取
	 * </p>
	 * 
	 * @return url,如果没有url则返回null
	 */
	public UrlBean getUrl() {
		UrlBean url = READY_FOR_CRAWL_URLS.pollFirst();
		if (url != null) {
			CRAWLED_URLS.add(url);
		}
		return url;
	}

	/**
	 * <p>
	 * 添加新收集的url.如果此url是当前正在抓取的host下的url,则添加到{@link #READY_FOR_CRAWL_URLS}
	 * 中,如果是其他host下的url,则会抽取新的host, 然后添加到{@link UrlScheduler#GLOBAL_NEW_HOSTS}中.
	 * </p>
	 * 
	 * @param urls
	 *            urls
	 */
	public void addUrls(Set<UrlBean> urls) {
		if (urls == null || urls.isEmpty()) {
			return;
		}
		for (UrlBean url : urls) {
			if (isInHost(url)) { // 是当前正在抓取的host下的url
				if (CRAWLED_URLS.contains(url) == false
						&& READY_FOR_CRAWL_URLS.contains(url) == false) {
					READY_FOR_CRAWL_URLS.addLast(url);
				}
			} else { // 其他host下的url
				addNewHost(url);
			}
		}
	}

	/**
	 * 切换调度的主机,此方法是线程安全的
	 * 
	 * @return 切换后的主机,如果所有主机都已经抓取完成则返回null
	 */
	public UrlBean swapHost() {
		synchronized (lock) {
			host = GLOBAL_NEW_HOSTS.pollFirst();
			if (host != null) {
				GLOBAL_CRALED_HOSTS.add(host);
			}
		}
		CRAWLED_URLS.clear();
		READY_FOR_CRAWL_URLS.clear();
		if (host != null) {
			READY_FOR_CRAWL_URLS.add(host);
		}
		return host;
	}

	/**
	 * 添加新收集的，还未抓取过的HOST,如果之前已经添加过则不会重复添加
	 * 
	 * @param host
	 *            新的host
	 */
	public static void addNewHost(UrlBean host) {
		host.onlyLeftSchemeAndHost();
		synchronized (lock) {
			if (GLOBAL_CRALED_HOSTS.contains(host) == false
					&& GLOBAL_NEW_HOSTS.contains(host) == false) {
				GLOBAL_NEW_HOSTS.addLast(host);
			}
		}
	}

	/**
	 * 获得当前正在调度的主机
	 * 
	 * @return 当前正在调度的主机
	 */
	public UrlBean getHost() {
		return host;
	}

	/**
	 * 判断指定的<i>url</i>是否在当前正在调度的host中
	 * 
	 * @param url
	 *            对比的{@link UrlBean}
	 * @return true-如果是在当前正在调度的host中
	 */
	protected boolean isInHost(UrlBean url) {
		if (url.isRelative()) { // 相对路径
			return true;
		} else {
			if (host.getHost().equalsIgnoreCase(url.getHost())) { // host值相等
				return true;
			}
		}
		return false;
	}

}
