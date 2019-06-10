package com.zhidejiaoyu.common.study.simple;

import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 单词图鉴测试题,随机选择题
 */
@Component
public class WordPictureUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaiduSpeak baiduSpeak;

    /**
     * 分配题型‘看词选图’、‘听音选图’、‘看图选词’3:3:4  listSelect的长度要是list的三倍
     *
     * @param list 题目,正确的list题
     * @param listSelect 用于选择题
     * @return
     */
    public Map<String, Object> allocationWord(List<Vocabulary> list, List<Vocabulary> listSelect, Map<Long, Map<Long, Long>> longMapMap){
        // 打乱
        Collections.shuffle(list);

        // 题的比例
        int size = list.size();
        int a = (int) Math.round(size*0.3);
        int b = (int) Math.round(size*0.3);
        int c = size - (int) Math.round(size*0.3) * 2;
        if(size <= 5){
            if(size == 5){
                a = 1;
                b = 2;
                c = 2;
            }else if(size == 4){
                a = 1;
                b = 1;
                c = 2;
            }else if(size == 3){
                a = 1;
                b = 1;
                c = 1;
            }else if(size == 2){
                a = 1;
                b = 1;
                c = 0;
            }else if(size == 1){
                a = 1;
                b = 0;
                c = 0;
            }
        }

        Map result = new HashMap();

        int max = listSelect.size();

        if(a != 0){
            List<Map<String, Object>> listMapa = new ArrayList<>();
            List<Vocabulary> lista = list.subList(0, a);
            // 遍历每一道题
            Map m = null;
            Map t = null;
            for (Vocabulary vo : lista){
                m = new HashMap();
                t = new HashMap();

                // 封装单元id
                if(longMapMap != null && longMapMap.containsKey(vo.getId())){
                    m.put("unitId", (longMapMap.get(vo.getId()).get("unitId")));
                }
                m.put("type", "看词选图");
                m.put("id", vo.getId());
                m.put("chinese", vo.getWordChinese());
                m.put("title", vo.getWord());
                m.put("recordpicurl", vo.getRecordpicurl());
                m.put("word", vo.getWord());
                for (int i = 0; i < 4; i++) {
                    if(i==0){
                        t.put(vo.getRecordpicurl(), true);
                    }else{
                        max -- ;
                        if(!listSelect.get(max).getRecordpicurl().equals(vo.getRecordpicurl())){
                            t.put(listSelect.get(max).getRecordpicurl(), false);
                        }else{
                            i--;
                        }
                    }
                }
                m.put("subject",t);
                listMapa.add(m);
            }
            result.put("one", listMapa);
        }

        if(b > 0){
            List<Map<String, Object>> listMapb = new ArrayList<>();
            List<Vocabulary> listb = list.subList(a, a+b);

            Map m = null;
            Map t = null;
            for (Vocabulary vo : listb){
                m = new HashMap();
                t = new HashMap();

                // 封装单元id
                if(longMapMap != null && longMapMap.containsKey(vo.getId())){
                    m.put("unitId", (longMapMap.get(vo.getId()).get("unitId")));
                }
                m.put("type", "听音选图");
                m.put("id", vo.getId());
                m.put("chinese", vo.getWordChinese());
                m.put("recordpicurl", vo.getRecordpicurl());
                m.put("word", vo.getWord());
                // 读音url
                m.put("title", baiduSpeak.getLanguagePath(vo.getWord()));
                for (int i = 0; i < 4; i++) {
                    if(i==0){
                        t.put(vo.getRecordpicurl(), true);
                    }else{
                        max -- ;
                        if(!listSelect.get(max).getRecordpicurl().equals(vo.getRecordpicurl())){
                            t.put(listSelect.get(max).getRecordpicurl(), false);
                        }else{
                            i--;
                        }
                    }
                }
                m.put("subject",t);
                listMapb.add(m);
            }
            result.put("two", listMapb);
        }

        if(c > 0){
            List<Map<String, Object>> listMapc = new ArrayList<>();
            List<Vocabulary> listc = list.subList(a+b, a+b+c);
            Map m = null;
            Map t = null;
            for (Vocabulary vo : listc){
                m = new HashMap();
                t = new HashMap();

                // 封装单元id
                if(longMapMap != null && longMapMap.containsKey(vo.getId())){
                    m.put("unitId", (longMapMap.get(vo.getId()).get("unitId")));
                }
                m.put("type", "看图选词");
                m.put("id", vo.getId());
                m.put("chinese", vo.getWordChinese());
                m.put("title", vo.getRecordpicurl());
                m.put("word", vo.getWord());
                m.put("recordpicurl", vo.getRecordpicurl());
                for (int i = 0; i < 4; i++) {
                    if(i==0){
                        t.put(vo.getWord(), true);
                    }else{
                        max -- ;
                        if(!listSelect.get(max).getWord().equals(vo.getWord())){
                            t.put(listSelect.get(max).getWord(), false);
                        }else{
                            i--;
                        }
                    }
                }
                m.put("subject",t);
                listMapc.add(m);
            }
            result.put("three", listMapc);
        }
        return result;
    }

}
