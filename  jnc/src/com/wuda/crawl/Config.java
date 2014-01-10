package com.wuda.crawl;

import java.util.Set;

/**
 * 抓取所需的配置信息实体
 * 
 * @author wuda
 * 
 */
public class Config {

	private int threadCount = 0; // 在主机下抓取的线程数
	private UrlScheduler urlScheduler = null;
	private Analyzer analyzer = null;
	private Storage storage = null;
	private Set<String> url_uncontains=null;
	private long timeOut=5000; //连接超时时间

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public Set<String> getUrlUnContains() {
		return url_uncontains;
	}

	public void setUrlUnContains(Set<String> url_uncontains) {
		this.url_uncontains = url_uncontains;
	}

	/**
	 * <p>
	 * 获得在主机下抓取的线程数
	 * </p>
	 * 
	 * @return 在主机下抓取的线程数
	 */
	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public UrlScheduler getUrlScheduler() {
		return urlScheduler;
	}

	public void setUrlScheduler(UrlScheduler urlScheduler) {
		this.urlScheduler = urlScheduler;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

}
