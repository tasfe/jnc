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
		UrlBean host = UrlUtil.getUrlBean("http://www.sina.com.cn/"); 
		UrlScheduler.addNewHost(host);//添加一个主机

		Config config = new Config(); //抓取的各种配置
		config.setAnalyzer(new Analyzer()); //分析器
		config.setStorage(new Storage()); //存储器
		config.setThreadCount(4); //每个主机下的线程数
		config.setTimeOut(3000); //超时时间
		config.setUrlScheduler(new UrlScheduler());
		Set<String> url_uncontains = new HashSet<String>(); //URL中不能包含的字符
		url_uncontains.add("javascript");
		url_uncontains.add("\\?");
		config.setUrlUnContains(url_uncontains);

		HostTarget target = new HostTarget(config);
		target.start(); //开始抓取

	}

}
