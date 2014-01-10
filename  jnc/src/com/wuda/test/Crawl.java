package com.wuda.test;

import java.util.HashSet;
import java.util.Set;

import com.wuda.crawl.Analyzer;
import com.wuda.crawl.Config;
import com.wuda.crawl.HostTarget;
import com.wuda.crawl.Storage;
import com.wuda.crawl.UrlScheduler;
import com.wuda.util.UrlBean;
import com.wuda.util.UrlUtil;

public class Crawl {

	public static void main(String[] args) {
		UrlBean host = UrlUtil.getUrlBean("http://www.lizhuping.com/");
		UrlScheduler.addNewHost(host);

		Config config = new Config();
		config.setAnalyzer(new Analyzer());
		config.setStorage(new Storage());
		config.setThreadCount(4);
		config.setTimeOut(3000);
		config.setUrlScheduler(new UrlScheduler());
		Set<String> url_uncontains = new HashSet<String>();
		url_uncontains.add("javascript");
		url_uncontains.add("\\?");
		config.setUrlUnContains(url_uncontains);

		HostTarget target = new HostTarget(config);
		target.start();

	}

}
