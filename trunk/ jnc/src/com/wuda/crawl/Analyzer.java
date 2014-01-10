package com.wuda.crawl;

import com.wuda.util.HtmlParser;

/**
 * <p>
 * Html内容分析器
 * </p>
 * 
 * @author wuda
 * 
 */
public class Analyzer {

	/**
	 * 分析网页内容,获取对应的结果
	 * 
	 * @param htmlContent
	 *            网页内容
	 * @return 分析结果
	 */
	public AnalysisResult getResult(String htmlContent) {
		String title=HtmlParser.getTitle(htmlContent);
		String keywords=HtmlParser.getKeywords(htmlContent);
		String description=HtmlParser.getDescription(htmlContent);
		System.out.println("title:"+title);
		System.out.println("keywords:"+keywords);
		System.out.println("description:"+description);
		return null;
	}

}
