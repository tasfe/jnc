package com.wuda.test;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.ParseException;

import com.wuda.util.PageDownload;

public class PageDownloadTest {

	public static void main(String[] args) {
		try {
			System.out.println(URLEncoder.encode("dW0ORamLmBC+aQQbc5WVmZ8RdDUjSW9iknApLEVGq6Oe/S3a4omhmIA0mK/KDdHXlmewV3eb8BvZ/qW5fJk4TqJ1Qg3WkUk5hwbcqbNv5uF9TDl781j6OUmhsL3Nx4bfR4XVJQ5Ho+NKM/kQsO7wdlpsS2xxkFCifEaf6fetFvI=govnet", "utf-8"));
			String url = "http://192.168.1.69:9988/client_verification?clientKey"
					+ URLEncoder
							.encode("dW0ORamLmBC+aQQbc5WVmZ8RdDUjSW9iknApLEVGq6Oe/S3a4omhmIA0mK/KDdHXlmewV3eb8BvZ/qW5fJk4TqJ1Qg3WkUk5hwbcqbNv5uF9TDl781j6OUmhsL3Nx4bfR4XVJQ5Ho+NKM/kQsO7wdlpsS2xxkFCifEaf6fetFvI=govnet",
									"utf-8");
			String content = PageDownload.getContent(url);
			System.out.println(content);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
