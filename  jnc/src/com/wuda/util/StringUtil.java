package com.wuda.util;

/**
 * <p>
 * 字符串工具类
 * </p>
 * 
 * @author wuda
 * @since 1.6
 * 
 */
public class StringUtil {

	/**
	 * <p>
	 * 是否null或者空
	 * </p>
	 * 
	 * @param str
	 *            str
	 * @return true if is null or empty
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}

}
