package com.zhidejiaoyu.common.utils.language;

import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 百度语音合成web api
 *
 * @author wuchenxi
 * @date 2018年4月23日 下午6:39:36
 */
@Slf4j
@Component
public class BaiduSpeak {

    @Value("${ftp.prefix}")
    private String prefix;

    @Value("${baidu}")
    private String baidu;

    @Value("${youdao}")
    private String youdao;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    /**
     * 获取语音合成地址
     *
     * @param text 需要合成的文字内容
     * @return
     */
    public String getLanguagePath(String text) {
        if (Objects.equals("Mr.", text)) {
            return youdao + text + "&type=1";
        }
        return youdao + text;

        /*Vocabulary vocabulary = vocabularyMapper.selectByWord(text);
        if (vocabulary != null && StringUtils.isNotEmpty(vocabulary.getReadUrl())) {
            return prefix + vocabulary.getReadUrl();
        } else {
            log.error("单词=[{}]在单词表中没有读音！", text);
            try {
                text = URLEncoder.encode(URLEncoder.encode(text, "utf-8"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("单词[{}]进行urlencode时出错！", text, e);
            }
            return baidu + text;
        }*/
    }

    public String getSentencePaht(String text) {
        return youdao + text + "@&@" + baidu + text;
    }
}
