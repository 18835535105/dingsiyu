package com.zhidejiaoyu.common.utils.language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 百度语音合成web api
 * 
 * @author wuchenxi
 * @date 2018年4月23日 下午6:39:36
 *
 */
@Component
public class BaiduSpeak {

	@Value("${baidu}")
	private String baidu;

	/**
	 * 获取语音合成地址
	 * 
	 * @param text
	 *            需要合成的文字内容
	 * @return
	 */
	public String getLanguagePath(String text) {
		return baidu + text;
	}
}
