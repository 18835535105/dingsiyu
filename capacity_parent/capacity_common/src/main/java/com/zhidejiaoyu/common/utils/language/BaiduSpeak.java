package com.zhidejiaoyu.common.utils.language;

import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

	@Autowired
	private VocabularyMapper vocabularyMapper;

	/**
	 * 获取语音合成地址
	 * 
	 * @param text
	 *            需要合成的文字内容
	 * @return
	 */
	public String getLanguagePath(String text) {

		Vocabulary vocabulary = vocabularyMapper.selectByWord(text);
		if (StringUtils.isEmpty(vocabulary.getReadUrl())) {
			return vocabulary.getReadUrl();
		} else {
			log.error("单词=[{}]在单词表中没有读音！", text);
			try {
				text = URLEncoder.encode(URLEncoder.encode(text, "utf-8"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				log.error("单词[{}]进行urlencode时出错！", text, e);
			}
			return baidu + text;
		}
	}
}
