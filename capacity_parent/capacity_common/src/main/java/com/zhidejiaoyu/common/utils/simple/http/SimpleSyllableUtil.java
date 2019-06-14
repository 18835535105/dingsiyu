package com.zhidejiaoyu.common.utils.simple.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wuchenxi
 * @date 2018/5/23 16:38
 */
@Component
public class SimpleSyllableUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取单词的音标
     * @param word
     * @return
     */
    public String getSyllable(String word) {
        Document doc;
        try {
            doc = Jsoup.connect("https://www.ldoceonline.com/dictionary/" + word).get();
            Elements rows = doc.select("span[class=HYPHENATION]");
            for (Element row : rows) {
                if (row.text().contains("‧")) {
                    return row.text();
                }
            }
        } catch (IOException e) {
            logger.error("单词 {} 的音标获取失败！", word);
        }
        return null;
    }
}
