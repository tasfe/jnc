package com.wuda.crawl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuda.util.HtmlParser;
import com.wuda.util.PageDownload;
import com.wuda.util.UrlBean;

/**
 * <p>
 * 针对一个主机的抓取.在一个主机下可以有多个{@link Spider}.当一个host下的所有URL都抓取完成后,程序会自动切换另一个host来继续抓取
 * </p>
 * 
 * @author wuda
 * 
 */
public class HostTarget {

	private Logger logger = LoggerFactory.getLogger(HostTarget.class);

	private Config config = null;

	private AtomicInteger cralw_total = new AtomicInteger(0);// 抓取的网页总数(正在抓取+已经抓取完成的网页数)
	private AtomicInteger finished_url_count = new AtomicInteger(0);// 已经抓取完成的网页数

	private Object lock = new Object();

	private boolean isCrawl = true;

	private HttpHost httpHost = null;

	private ExecutorService THREAD_POLL = null;

	/**
	 * <p>
	 * 针对一个主机的抓取实例
	 * </p>
	 * 
	 * @param spider
	 *            在此主机下抓取的蜘蛛数
	 */
	public HostTarget(Config config) {
		this.config = config;
		if (config.getThreadCount() > 0) {
			THREAD_POLL = Executors.newFixedThreadPool(config.getThreadCount());
		} else {
			THREAD_POLL = Executors.newCachedThreadPool();
		}
	}

	/**
	 * <p>
	 * 获得当前正在抓取的主机
	 * </p>
	 * 
	 * @return domain
	 */
	public String getHost() {
		return config.getUrlScheduler().getHost().getHost();
	}

	public void start() {
		UrlBean host = null;
		if ((host = swapHost()) == null) {
			logger.info("没有可用的主机来抓取");
			return;
		}
		logger.info("当前在host：" + host.getHost() + "下抓取");
		UrlBean url = null;
		Worker worker = null;
		while (isCrawl) {
			/**
			 * 在一个host下暂时没有可用的url抓取，但是由于还有未完成的抓取工作，
			 * 而在这些未完成的抓取页面里面很有可能再收集的新的url来抓取
			 */
			synchronized (lock) {
				while (isCrawl
						&& (url = config.getUrlScheduler().getUrl()) == null
						&& cralw_total.get() != finished_url_count.get()) {
					try {
						lock.wait(config.getTimeOut());
					} catch (InterruptedException e) {
						e.printStackTrace();
						logger.warn(e.getMessage(), e);
					}
				}
			}
			if (url != null) {
				worker = new Worker(url);// 抓取这个页面
				cralw_total.incrementAndGet();
				THREAD_POLL.execute(worker);
			} else { // 在一个host下已经抓取完成,现在切换到另外一个host下去抓取
				logger.info("host:" + getHost() + "已经抓取完成,这个host下共有："
						+ cralw_total.get() + "个网页,\t完成抓取："
						+ finished_url_count.get());
				logger.info("现在切换到另外一个host下去抓取");
				host = swapHost();
				if (host == null) { // 切换以后发现已经没有可用抓取的host了
					isCrawl = false;
					logger.info("切换以后发现已经没有可用抓取的host了");
				} else {
					isCrawl = true;
					logger.info("当前在host：" + host.getHost() + "下抓取");
				}
			}
		}
		logger.info("抓取退出!");
		System.exit(0);
	}

	/**
	 * 切换到另外一个host下去抓取
	 * 
	 * @return 新的host,如果已经没有可用的host需要抓取了则返回null
	 */
	public UrlBean swapHost() {
		UrlBean host = config.getUrlScheduler().swapHost();
		if (host != null) {
			/**
			 * 为抓取一个新的host而重置各个指标
			 */
			httpHost = new HttpHost(host.getHost());
			cralw_total.set(0);
			finished_url_count.set(0);
		}
		return host;
	}

	/**
	 * 已经抓取的url数加一
	 */
	private void incFinCount() {
		synchronized (lock) {
			finished_url_count.incrementAndGet();
		}
	}

	/*
	 * 抓取线程
	 */
	class Worker implements Runnable {

		private UrlBean url = null;

		Worker(UrlBean url) {
			this.url = url;
		}

		@Override
		public void run() {
			HttpGet httpGet = null;
			HttpResponse response = null;
			String htmlContent = null;
			AnalysisResult result = null;
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						new Integer(config.getTimeOut() + "")); // 连接超时
				HttpConnectionParams.setSoTimeout(client.getParams(),
						new Integer(config.getTimeOut() + "")); // 读取数据超时
				httpGet = new HttpGet(url.getUrl().trim());
				response = client.execute(httpHost, httpGet);
				if (response.getStatusLine().getStatusCode() == 200
						&& PageDownload.isTextHtml(response)) {
					htmlContent = PageDownload.getContent(response);
					Set<UrlBean> urls = HtmlParser.getUrls(htmlContent,
							config.getUrlUnContains());
					config.getUrlScheduler().addUrls(urls);
					result = config.getAnalyzer().getResult(htmlContent);
					config.getStorage().save(result);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				incFinCount();
				synchronized (lock) {
					lock.notify();
				}
				if (httpGet != null) {
					httpGet.releaseConnection();
				}
			}

		}
	}

}
