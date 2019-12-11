package com.zhidejiaoyu.common.utils.language;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度语音合成web api
 *
 * @author wuchenxi
 * @date 2018年4月23日 下午6:39:36
 */
@Slf4j
@Component
public class BaiduSpeak {

    @Value("${baidu}")
    private String baidu;

    @Value("${youdao.create}")
    private String youdao;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    /**
     * 需要特殊处理的单词/例句集合
     */
    private static Map<String, String> wordMap;

    static {
        wordMap = new HashMap<>(16);
        wordMap.put("Mr.", "Mr.");
        wordMap.put("car", "car");
        wordMap.put("before", "before");
        wordMap.put("fast", "fast");
        wordMap.put("hang", "hang");
        wordMap.put("fritter", "fritter");
    }

    /**
     * 获取语音合成地址
     *
     * @param text 需要合成的文字内容
     * @return
     */
    public String getLanguagePath(String text) {
        Vocabulary vocabulary = vocabularyMapper.selectByWord(text);
        if (vocabulary != null && StringUtils.isNotEmpty(vocabulary.getReadUrl())) {
            return GetOssFile.getPublicObjectUrl(vocabulary.getReadUrl());
        }
        if (text != null && text.contains("a/an")) {
            text = text.replace("/", ",");
        }
        return youdao + text;
    }

    public String getSentencePath(String centreExample) {
        if (StringUtils.isEmpty(centreExample)) {
            return "";
        }
        return youdao +  centreExample.replace("#", " ").replace("$", "") + "@&@" + baidu + centreExample.replace("#", " ").replace("$", "");
    }

    public String getLetterPath(String centreExample) {
        if (StringUtils.isEmpty(centreExample)) {
            return "";
        }
        return  baidu + centreExample.replace("#", " ").replace("$", "");
    }
}
