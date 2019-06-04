package com.zhidejiaoyu.common.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 获取单词音节信息
 *
 * @author wuchenxi
 * @date 2018/5/23 16:38
 */
@Slf4j
@Component
public class SyllableUtil {

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
            log.error("单词 {} 的音节获取失败！", word);
        }
        return null;
    }

    public static void main(String[] args) {
        SyllableUtil syllableUtil = new SyllableUtil();
        System.out.println(syllableUtil.getSyllable("Singapore"));
        System.out.println(syllableUtil.getSyllable("netherlands"));
    }
}
