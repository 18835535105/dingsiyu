package com.zhidejiaoyu.common.utils.simple;

import java.util.Random;
import java.util.UUID;

/**
 * uuid生成策略
 */
public class IDUtils {

	/**
	 * 生成token值
	 * @return	token值
	 */
	public static String createToken() {
		return UUID.randomUUID().toString() + ":" + System.currentTimeMillis();
	}

	/**
	 * 生成只包含中英文的uuid
	 *
	 * @return uuid
	 */
	public static String ID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 当前时间毫秒值+三位随机数
	 *
	 * return uuid
	 */
	public static String genImageName() {
		//取当前时间的长整形值包含毫秒
		long millis = System.currentTimeMillis();
		//long millis = System.nanoTime();
		//加上三位随机数
		Random random = new Random();
		int end3 = random.nextInt(999);
		//如果不足三位前面补0
		String str = millis + String.format("%03d", end3);

		return str;
	}

	/**
	 * 当前时间毫秒值+两位随机数
	 *
	 * @return uuid
	 */
	public static long genItemId() {
		//取当前时间的长整形值包含毫秒
		long millis = System.currentTimeMillis();
		//long millis = System.nanoTime();
		//加上两位随机数
		Random random = new Random();
		int end2 = random.nextInt(99);
		//如果不足两位前面补0
		String str = millis + String.format("%02d", end2);
		long id = new Long(str);
		return id;
	}
}
