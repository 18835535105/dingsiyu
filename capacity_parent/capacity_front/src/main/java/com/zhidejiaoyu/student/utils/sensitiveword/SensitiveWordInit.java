package com.zhidejiaoyu.student.utils.sensitiveword;

import com.zhidejiaoyu.student.config.SensitiveWordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author wuchenxi
 */
@Component
public class SensitiveWordInit {
    private HashMap sensitiveWordMap;

	@Autowired
	private SensitiveWordConfig sensitiveWordConfig;
	
	SensitiveWordInit(){
		super();
	}
	
	Map initKeyWord(){
		try {
			Set<String> keyWordSet = getSensitiveWord();
			addSensitiveWordToHashMap(keyWordSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensitiveWordMap;
	}

    private Set<String> getSensitiveWord() {
        String word = sensitiveWordConfig.getWord();
        if (word.contains("-")) {
            String[] split = word.split("-");
            Set<String> set = new HashSet<>(split.length);
            set.addAll(Arrays.asList(split));
            return set;
        }
        return new HashSet<>(0);
    }


	private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
		sensitiveWordMap = new HashMap(keyWordSet.size());
		String key;
		Map nowMap;
		Map<String, String> newWorMap;
        for (String aKeyWordSet : keyWordSet) {
            key = aKeyWordSet;
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);
                Object wordMap = nowMap.get(keyChar);

                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<>(16);
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
	}
}
