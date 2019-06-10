package com.zhidejiaoyu.common.utils.simple.testUtil;

import com.zhidejiaoyu.common.mapper.simple.UnitVocabularyMapper;
import com.zhidejiaoyu.common.mapper.simple.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.study.simple.CommonMethod;
import com.zhidejiaoyu.common.utils.simple.language.BaiduSpeak;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * 获取学生测试题 util
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
@Slf4j
@Component
public class TestResultUtil implements Serializable {

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 封装指定类型的单词测试题，适用于 "英译汉"："汉译英"："听力理解" = 3 ：3 ：4 的题型
     * <p>
     * 用于学前测试：游戏测试，等级测试
     *
     * @param type       测试题类型数组 ["英译汉","汉译英","听力理解"]
     * @param subjectNum 测试题总个数
     * @param target     需要封装的测试题集合
     * @param version    学习版本
     * @param phase      学段
     * @return
     */
    public List<TestResult> getWordTestes(String[] type, Integer subjectNum, List<Vocabulary> target, String version, String phase) {
        if (type == null) {
            throw new RuntimeException("测试题类型不能为空！");
        }
        if (target == null) {
            throw new RuntimeException("需要封装的单词集合不能为空！");
        }
        if (subjectNum > target.size()) {
            throw new RuntimeException("需要封装的测试题数量不能大于当前单词数量!");
        }
        List<TestResult> results = new ArrayList<>();
        // 随机找出指定数量单词
        Map<Long, Vocabulary> map = this.getTestVocabulary(subjectNum, target);
        Set<Long> idSet = map.keySet();
        Iterator<Long> iterator = idSet.iterator();
        // 根据题型对测试题进行平分,有几个题型就将测试题平分成几份
        for (int i = 0; i < type.length; i++) {
            // 控制每类题型的数量
            int j = 0;
            while (iterator.hasNext()) {
                j++;
                Map<Object, Boolean> map2 = new HashMap<>(16);
                Vocabulary vocabulary = map.get(iterator.next());
                TestResult testResult = new TestResult();
                // 从当前课程下随机取一个当前单词对应的版本的单词释义
                String wordChinese = getWordChinese(version, phase, vocabulary);
                testResult.setType(type[i]);
                if ("英译汉".equals(type[i]) || "听力理解".equals(type[i])) {
                    testResult.setTitle(vocabulary.getWord());
                    testResult.setId(vocabulary.getId());
                    map2.put(wordChinese, true);
                    if ("听力理解".equals(type[i])) {
                        testResult.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
                    }
                    // 从其余题目中随机获取三道题目
                    for (int k = 0; k < (target.size() - 1 > 3 ? 3 : target.size() - 1); ) {
                        int random = (int) (Math.random() * target.size());
                        if (map2.containsKey(target.get(random).getWordChinese())) {
                            continue;
                        } else {
                            map2.put(target.get(random).getWordChinese(), false);
                        }
                        k++;
                    }
                } else if ("汉译英".equals(type[i])) {
                    testResult.setTitle(wordChinese);
                    testResult.setId(vocabulary.getId());
                    map2.put(vocabulary.getWord(), true);
                    // 从其余题目中随机获取三道题目数
                    for (int k = 0; k < (target.size() - 1 > 3 ? 3 : target.size() - 1); ) {
                        int random = (int) (Math.random() * target.size());
                        if (map2.containsKey(target.get(random).getWord())) {
                            continue;
                        } else {
                            map2.put(target.get(random).getWord(), false);
                        }
                        k++;
                    }
                }

                testResult.setSubject(map2);
                results.add(testResult);
                if (type.length == 3) {
                    boolean canBreak = (i < 2 && j == (Math.round(subjectNum * 0.3))
                            || (i == 2 && j == (subjectNum - Math.round(subjectNum * 0.3) - Math.round(subjectNum * 0.3))));
                    if (canBreak) {
                        break;
                    }
                }

            }
        }
        return results;
    }

    private String getWordChinese(String version, String phase, Vocabulary vocabulary) {
        String wordChinese = vocabularyMapper.selectWordChineseByVersionAndWordId(version, vocabulary.getId(), phase);
        if (StringUtils.isEmpty(wordChinese)) {
            wordChinese = vocabulary.getWordChinese();
        }
        return wordChinese;
    }

    /**
     * 封装指定类型的单词测试题，适用于 "英译汉"："汉译英"："听力理解" = 3 ：3 ：4 的题型
     * <p>
     * 用于测试题从单元中抽取的测试类型
     *
     * @param type       测试题类型数组 ["英译汉","汉译英","听力理解"]
     * @param subjectNum 测试题总个数
     * @param target     需要封装的测试题集合
     * @param unitId     当前单元id
     * @return
     */
    public List<TestResult> getWordTestesForUnit(String[] type, Integer subjectNum, List<Vocabulary> target, Long unitId) {
        if (type == null || type.length == 0) {
            throw new RuntimeException("测试题类型不能为空！");
        }
        if (target == null || target.size() == 0) {
            throw new RuntimeException("需要封装的单词集合不能为空！");
        }
        if (subjectNum > target.size()) {
            throw new RuntimeException("需要封装的测试题数量不能大于当前单词数量!");
        }
        if (unitId == null) {
            throw new RuntimeException("单元id不能为空");
        }
        List<TestResult> results = new ArrayList<>();
        // 随机找出指定数量单词
        Map<Long, Vocabulary> map = this.getTestVocabulary(subjectNum, target);
        Set<Long> idSet = map.keySet();

        Map<Long, Map<Long, String>> chineseMap = unitVocabularyMapper.selectWordChineseMapByUnitIdAndWordIds(unitId, idSet);

        Iterator<Long> iterator = idSet.iterator();
        // 根据题型对测试题进行平分,有几个题型就将测试题评分成几份
        toGetSubject(type, subjectNum, target, results, map, chineseMap, iterator);
        return results;
    }

    /**
     * 封装指定类型的单词测试题，适用于 "英译汉"："汉译英"："听力理解" = 3 ：3 ：4 的题型
     * <p>
     * 用于测试题从课程中抽取的测试类型
     *
     * @param type       测试题类型数组 ["英译汉","汉译英","听力理解"]
     * @param subjectNum 测试题总个数
     * @param target     需要封装的测试题集合
     * @param courseId   当前课程id
     * @return
     */
    public List<TestResult> getWordTestesForCourse(String[] type, Integer subjectNum, List<Vocabulary> target, Long courseId) {
        if (type == null) {
            throw new RuntimeException("测试题类型不能为空！");
        }
        if (target == null || target.size() == 0) {
            throw new RuntimeException("需要封装的单词集合不能为空！");
        }
        if (subjectNum > target.size()) {
            throw new RuntimeException("需要封装的测试题数量不能大于当前单词数量!");
        }
        List<TestResult> results = new ArrayList<>();
        // 随机找出指定数量单词
        Map<Long, Vocabulary> map = this.getTestVocabulary(subjectNum, target);
        Set<Long> idSet = map.keySet();
        Map<Long, Map<Long, String>> chineseMap = unitVocabularyMapper.selectWordChineseMapByCourseIdIdAndWordIds(courseId, idSet);


        Iterator<Long> iterator = idSet.iterator();
        // 根据题型对测试题进行平分,有几个题型就将测试题评分成几份
        toGetSubject(type, chineseMap.size(), target, results, map, chineseMap, iterator);
        return results;
    }

    /**
     * 获取试题
     *
     * @param type       测试题类型数组 ["英译汉","汉译英","听力理解"]
     * @param subjectNum 测试题总个数
     * @param target     需要封装的测试题集合
     * @param results    封装的测试题结果
     * @param map        需要封装的单词map  key：单词id；value：单词对象
     * @param chineseMap 单词释义map key：单词id；value：map key：单词id；value：单词释义
     * @param iterator   单词id迭代器
     */
    private void toGetSubject(String[] type, Integer subjectNum, List<Vocabulary> target, List<TestResult> results,
                              Map<Long, Vocabulary> map, Map<Long, Map<Long, String>> chineseMap, Iterator<Long> iterator) {
        String wordChinese;
        String chinese;
        for (int i = 0; i < type.length; i++) {
            // 控制每类题型的数量
            int j = 0;
            while (iterator.hasNext()) {
                j++;
                Map<Object, Boolean> map2 = new HashMap<>(16);
                Vocabulary vocabulary = map.get(iterator.next());
                wordChinese = chineseMap.get(vocabulary.getId()).get("wordChinese");
                TestResult testResult = new TestResult();
                testResult.setType(type[i]);
                if ("英译汉".equals(type[i]) || "听力理解".equals(type[i])) {
                    testResult.setTitle(vocabulary.getWord());
                    testResult.setId(vocabulary.getId());
                    map2.put(wordChinese, true);
                    if ("听力理解".equals(type[i])) {
                        testResult.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
                    }
                    // 从其余题目中随机获取三道题目
                    for (int k = 0; k < (target.size() - 1 > 3 ? 3 : target.size() - 1); ) {
                        int random = (int) (Math.random() * target.size());
                        if (chineseMap.get(target.get(random).getId()) == null) {
                            continue;
                        }
                        chinese = chineseMap.get(target.get(random).getId()).get("wordChinese");
                        if (map2.containsKey(chinese)) {
                            continue;
                        } else {
                            map2.put(chinese, false);
                        }
                        k++;
                    }

                    // 选项凑够4个单词翻译
                    int record = 0;
                    int nextInt = new Random().nextInt(100);
                    if (target.size() == 1) {
                        // 需要三道 - 查四道题防止和正确答案重复
                        List<String> chinese_ = vocabularyMapper.getThreeChinese(nextInt, 4);
                        for (String chine : chinese_) {
                            if (!map2.containsKey(chine) && record < 3) {
                                map2.put(chine, false);
                                record++;
                            }
                        }
                    } else if (target.size() == 2) {
                        // 需要两道
                        List<String> chinese_ = vocabularyMapper.getThreeChinese(nextInt, 3);
                        for (String chine : chinese_) {
                            if (!map2.containsKey(chine) && record < 2) {
                                map2.put(chine, false);
                                record++;
                            }
                        }
                    } else if (target.size() == 3) {
                        // 需要三道
                        List<String> chinese_ = vocabularyMapper.getThreeChinese(nextInt, 2);
                        for (String chine : chinese_) {
                            if (!map2.containsKey(chine) && record < 1) {
                                map2.put(chine, false);
                                record++;
                            }
                        }
                    }

                } else if ("汉译英".equals(type[i])) {
                    chinese = chineseMap.get(vocabulary.getId()).get("wordChinese");
                    testResult.setTitle(chinese);
                    testResult.setId(vocabulary.getId());
                    map2.put(vocabulary.getWord(), true);
                    // 从其余题目中随机获取三道题目数
                    for (int k = 0; k < (target.size() - 1 > 3 ? 3 : target.size() - 1); ) {
                        int random = (int) (Math.random() * target.size());
                        if (map2.containsKey(target.get(random).getWord())) {
                            continue;
                        } else {
                            map2.put(target.get(random).getWord(), false);
                        }
                        k++;
                    }

                    // 选项凑够4个单词
                    int record = 0;
                    int nextInt = new Random().nextInt(100);
                    if (target.size() == 1) {
                        // 需要三道
                        List<String> word = vocabularyMapper.getThreeWord(nextInt, 4);
                        for (String word_ : word) {
                            if (!map2.containsKey(word_) && record < 3) {
                                map2.put(word_, false);
                                record++;
                            }
                        }
                    } else if (target.size() == 2) {
                        // 需要两道
                        List<String> word = vocabularyMapper.getThreeWord(nextInt, 3);
                        for (String word_ : word) {
                            if (!map2.containsKey(word_) && record < 2) {
                                map2.put(word_, false);
                                record++;
                            }
                        }
                    } else if (target.size() == 3) {
                        // 需要三道
                        List<String> word = vocabularyMapper.getThreeWord(nextInt, 2);
                        for (String word_ : word) {
                            if (!map2.containsKey(word_) && record < 1) {
                                map2.put(word_, false);
                                record++;
                            }
                        }
                    }
                }

                testResult.setSubject(map2);
                results.add(testResult);
                if (type.length == 3) {
                    // 只有三种类型的题目时，判断是否需要进行题目类型切换
                    boolean canBreak = (i < 2 && j == Math.round(subjectNum * 0.3)
                            || (i == 2 && j == (subjectNum - Math.round(subjectNum * 0.3) - Math.round(subjectNum * 0.3))));
                    if (canBreak) {
                        break;
                    }
                } else if (type.length == 2) {
                    // 只有两种类型的题目时，判断是否需要进行题目类型切换
                    boolean canBreak = (i < 1 && j == Math.round(subjectNum * 0.5)
                            || (i == 1 && j == (subjectNum - Math.round(subjectNum * 0.5))));
                    if (canBreak) {
                        break;
                    }
                }

            }
        }
    }

    /**
     * 封装例句测试题目
     *
     * @param sentences 需要测试的例句集合
     * @return
     */
    public List<SentenceTestResult> getSentenceTestResults(List<Sentence> sentences) {
        if (sentences.size() == 0) {
            return null;
        }
        List<SentenceTestResult> sentenceTestResults = new ArrayList<>();
        SentenceTestResult result;
        for (Sentence sentence : sentences) {
            result = new SentenceTestResult();
            result.setId(sentence.getId());
            result.setSentence(sentence.getCentreExample().replace("#", " "));
            result.setChaosSentence(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
            result.setSentenctChinese(sentence.getCentreTranslate().replace("*", ""));
            result.setReadUrl(baiduSpeak.getLanguagePath(sentence.getCentreExample().replace("#", " ")));
            sentenceTestResults.add(result);
        }
        return sentenceTestResults;
    }

    /**
     * 获取指定数量的随机题目
     *
     * @param target     需要封装的测试题集合
     * @param subjectNum 需要展示的当前类型的试题数量
     * @return map key:单词id；value：单词对象
     */
    private Map<Long, Vocabulary> getTestVocabulary(Integer subjectNum, List<Vocabulary> target) {
        Map<Long, Vocabulary> map = new LinkedHashMap<>();

        Collections.shuffle(target);
        for (int i = 0; i < subjectNum; i++) {
            map.put(target.get(i).getId(), target.get(i));
        }
        return map;
    }

    /**
     * 注意：target 中含有相同的单词时生成的试题可能比预期少
     *
     * @param type
     * @param subjectNum
     * @param target
     * @param start
     * @param end
     * @return
     */
    public List<TestResult> getWordTestesForCourse5DTest(String[] type, Integer subjectNum, List<Vocabulary> target, String start, String end) {
        List<TestResult> results = new ArrayList<>();
        // 随机找出指定数量单词
        Map<Long, Vocabulary> map = this.getTestVocabulary(subjectNum, target);
        Set<Long> idSet = map.keySet();
        Map<Long, Map<Long, String>> chineseMap = unitVocabularyMapper.selectWordChineseMapByCourseIdIdAndWordIds5DTest(idSet, start, end);

        Iterator<Long> iterator = idSet.iterator();
        // 根据题型对测试题进行平分,有几个题型就将测试题评分成几份
        toGetSubject(type, chineseMap.size(), target, results, map, chineseMap, iterator);
        return results;
    }

}
