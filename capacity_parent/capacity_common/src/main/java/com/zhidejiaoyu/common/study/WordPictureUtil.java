package com.zhidejiaoyu.common.study;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.PictureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 单词图鉴测试题,随机选择题
 */
@Slf4j
@Component
public class WordPictureUtil {

    /**
     * 分配题型‘看词选图’、‘听音选图’、‘看图选词’3:3:4  listSelect的长度要是list的三倍
     *
     * @param list       题目,正确的list题
     * @param listSelect 用于选择题
     * @param unitId
     * @return
     */
    public Map<String, Object> allocationWord(List<Vocabulary> list, List<Vocabulary> listSelect, Map<Long, Map<Long, Long>> longMapMap, Long unitId) {
        // 打乱
        Collections.shuffle(list);
        int c = list.size();

        Map<String, Object> result = new HashMap<>(16);

        int max = listSelect.size();

        if (c > 0) {
            List<Map<String, Object>> listMapc = new ArrayList<>();
            Map<String, Object> m;
            Map<String, Object> t;
            for (Vocabulary vo : list) {
                m = new HashMap<>(16);
                t = new HashMap<>(16);

                // 封装单元id
                if (longMapMap != null && longMapMap.containsKey(vo.getId())) {
                    m.put("unitId", (longMapMap.get(vo.getId()).get("unitId")));
                }
                m.put("type", "看图选词");
                m.put("id", vo.getId());
                m.put("chinese", vo.getWordChinese());
                m.put("title", PictureUtil.getPictureByUnitId(vo, unitId));
                m.put("word", vo.getWord());
                m.put("recordpicurl", PictureUtil.getPictureByUnitId(vo, unitId));
                for (int i = 0; i < 4; i++) {
                    if (i == 0) {
                        t.put(vo.getWord(), true);
                    } else {
                        max--;
                        if (!listSelect.get(max).getWord().equals(vo.getWord())) {
                            t.put(listSelect.get(max).getWord(), false);
                        } else {
                            i--;
                        }
                    }
                }
                m.put("subject", t);
                listMapc.add(m);
            }
            result.put("three", listMapc);
        }
        return result;
    }

}
