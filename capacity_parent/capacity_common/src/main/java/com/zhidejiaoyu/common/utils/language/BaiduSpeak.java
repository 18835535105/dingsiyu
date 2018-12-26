package com.zhidejiaoyu.common.utils.language;
import com.zhidejiaoyu.common.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * 百度语音合成web api
 * 
 * @author wuchenxi
 * @date 2018年4月23日 下午6:39:36
 *
 */
@Slf4j
@Component
public class BaiduSpeak {

	@Value("${ftp.prefix}")
	private String prefix;

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
		if (Objects.equals(text, "up")) {
			return prefix + FileConstant.WORD_AUDIO + "up.mp3";
		}
		try {
			text = URLEncoder.encode(URLEncoder.encode(text, "utf-8"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error("单词[{}]进行urlencode时出错！", text, e);
		}
		return baidu + text;
	}
}
