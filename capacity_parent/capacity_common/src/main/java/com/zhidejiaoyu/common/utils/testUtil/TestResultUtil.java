package com.zhidejiaoyu.common.utils.testUtil;

import com.zhidejiaoyu.common.mapper.UnitVocabularyMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.vo.testVo.TestResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

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
     * 以字母或数字结尾
     */
    final String END_MATCH = ".*[a-zA-Z0-9$# ']$";
    /**
     * 以字母或数据开头
     */
    final String START_MATCH = "^[a-zA-Z0-9$# '].*";
    /**
     * 二次判断以字母或数字结尾
     */
    final String END_MATCH2 = ".*[a-zA-Z0-9$# ':.@-]$";
    /**
     * 二次判断以字母或数据开头
     */
    final String START_MATCH2 = "^[a-zA-Z0-9$# ':@-].*";
    /**
     * 以字母或数字结尾
     */
    final String END_MATCH_CHAINESE = ".*[a-zA-z0-9\\u4e00-\\u9fa5@-]$";
    /**
     * 以字母或数据开头
     */
    final String START_MATCH_CHAINESE = "^[a-zA-z0-9\\u4e00-\\u9fa5@-].*";

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
    public List<TestResultVO> getWordTestes(String[] type, Integer subjectNum, List<Vocabulary> target, String version, String phase) {
        if (type == null) {
            throw new RuntimeException("测试题类型不能为空！");
        }
        if (target == null) {
            throw new RuntimeException("需要封装的单词集合不能为空！");
        }
        if (subjectNum > target.size()) {
            throw new RuntimeException("需要封装的测试题数量不能大于当前单词数量!");
        }
        List<TestResultVO> results = new ArrayList<>();
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
                TestResultVO testResult = new TestResultVO();
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
                    for (int k = 0; k < (Math.min(target.size() - 1, 3)); ) {
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
                    for (int k = 0; k < (Math.min(target.size() - 1, 3)); ) {
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
    public List<TestResultVO> getWordTestesForUnit(String[] type, Integer subjectNum, List<Vocabulary> target, Long unitId) {
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
        List<TestResultVO> results = new ArrayList<>();
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
    public List<TestResultVO> getWordTestesForCourse(String[] type, Integer subjectNum, List<Vocabulary> target, Long courseId) {
        if (type == null) {
            throw new RuntimeException("测试题类型不能为空！");
        }
        if (target == null) {
            throw new RuntimeException("需要封装的单词集合不能为空！");
        }
        if (subjectNum > target.size()) {
            throw new RuntimeException("需要封装的测试题数量不能大于当前单词数量!");
        }
        if (subjectNum == 0) {
            log.error("封装测试题时，subjectNum=0");
        }
        if (target.size() == 0) {
            log.error("封装测试题时，target.size=0");
        }
        List<TestResultVO> results = new ArrayList<>();
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
    private void toGetSubject(String[] type, Integer subjectNum, List<Vocabulary> target, List<TestResultVO> results,
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
                TestResultVO testResult = new TestResultVO();
                testResult.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
                testResult.setType(type[i]);
                if ("英译汉".equals(type[i]) || "听力理解".equals(type[i])) {
                    testResult.setTitle(vocabulary.getWord());
                    testResult.setId(vocabulary.getId());
                    map2.put(wordChinese, true);
                    /*if ("听力理解".equals(type[i])) {
                        testResult.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
                    }*/
                    // 从其余题目中随机获取三道题目
                    for (int k = 0; k < (Math.min(target.size() - 1, 3)); ) {
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
                    for (int k = 0; k < (Math.min(target.size() - 1, 3)); ) {
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
    public List<SentenceTranslateVo> getSentenceTestResults(List<Sentence> sentences, Integer classify, Integer type) {
        if (sentences.size() > 0) {
            List<SentenceTranslateVo> vos = new ArrayList<>(sentences.size());
            if (classify == 6) {
                getSentenceWriteVo(sentences, vos, type);
            } else {
                getSentenceTranslateVo(sentences, vos, type, classify);
            }
            return vos;
        }
        return null;
    }


    private void getSentenceWriteVo(List<Sentence> sentences, List<SentenceTranslateVo> vos, Integer type) {
        SentenceTranslateVo sentenceTranslateVo;
        for (Sentence sentence : sentences) {
            sentenceTranslateVo = new SentenceTranslateVo();
            sentenceTranslateVo.setId(sentence.getId());
            sentenceTranslateVo.setMemoryStrength(0.00);
            sentenceTranslateVo.setEnglish(sentence.getCentreExample().replace("#", " ").replace("$", ""));
            sentenceTranslateVo.setChinese(sentence.getCentreTranslate().replace("*", ""));
            sentenceTranslateVo.setReadUrl(baiduSpeak.getSentencePath(sentence.getCentreExample()));
            if (type == 2) {
                sentenceTranslateVo.setOrder(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
            } else {
                sentenceTranslateVo.setOrder(commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
            }
            sentenceTranslateVo.setRateList(commonMethod.getEnglishList(sentence.getCentreExample()));
            vos.add(sentenceTranslateVo);
        }
    }

    private void getSentenceTranslateVo(List<Sentence> sentences, List<SentenceTranslateVo> vos, int type, int classify) {
        SentenceTranslateVo sentenceTranslateVo;
        for (Sentence sentence : sentences) {
            sentenceTranslateVo = new SentenceTranslateVo();
            sentenceTranslateVo.setChinese(sentence.getCentreTranslate().replace("*", ""));
            sentenceTranslateVo.setEnglish(sentence.getCentreExample().replace("#", " ").replace("*", " ").replace("$", ""));
            sentenceTranslateVo.setId(sentence.getId());
            sentenceTranslateVo.setReadUrl(baiduSpeak.getSentencePath(sentence.getCentreExample()));

            int nextInt = new Random().nextInt();
            if (classify == 4) {
                nextInt = 2;
            }
            if (nextInt % 2 == 0) {
                getOrderEnglishList(sentenceTranslateVo, sentence.getCentreExample(), sentence.getExampleDisturb(), type);
            } else {
                getOrderChineseList(sentenceTranslateVo, sentence.getCentreTranslate(), sentence.getTranslateDisturb(), type);
            }
            vos.add(sentenceTranslateVo);
        }
    }

    public void getOrderChineseList(Map<String, Object> sentenceTranslateVo, String centreTranslates, String translateDisturb, Integer type) {
        // 将例句按照空格拆分
        String[] centreTranslate = centreTranslates.split(" ");
        List<String> centreTranslatelist = new ArrayList<>();
        for (String s : centreTranslate) {
            String[] split = s.split("\\*");
            centreTranslatelist.addAll(Arrays.asList(split));
        }
        // 正确顺序
        List<String> rightList = new ArrayList<>();
        // 乱序
        List<String> orderList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (String s : centreTranslatelist) {
            if (Pattern.matches(END_MATCH_CHAINESE, s) && Pattern.matches(START_MATCH_CHAINESE, s)) {
                rightList.add(s);
                orderList.add(s);
            } else {
                char[] chars = s.toCharArray();
                sb.setLength(0);
                int length = chars.length;
                for (int i = 0; i < length; i++) {
                    char aChar = chars[i];
                    // 当前下标的数据
                    String s1 = new String(new char[]{aChar});
                    // 是字母或者数字，拼接字符串
                    if (Pattern.matches(END_MATCH_CHAINESE, s1)) {
                        sb.append(s1);
                    } else {
                        if (sb.length() > 0) {
                            rightList.add(sb.toString());
                            orderList.add(sb.toString());
                            sb.setLength(0);
                        }
                        // 如果符号前面是字母需要在符号列表中加 null
                        rightList.add(s1);
                    }
                    // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                    if (sb.length() > 0 && i == length - 1) {
                        rightList.add(sb.toString());
                        orderList.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        }
        if (type.equals(2)) {
            if (StringUtils.isNotEmpty(translateDisturb)) {
                orderList.add(translateDisturb.replace("*", " ").replace("$", ""));
            }
        }
        Collections.shuffle(orderList);
        sentenceTranslateVo.put("order", orderList);
        sentenceTranslateVo.put("rateList", rightList);
    }

    public void getOrderChineseList(SentenceTranslateVo sentenceTranslateVo, String centreTranslates, String translateDisturb, Integer type) {
        // 将例句按照空格拆分
        String[] centreTranslate = centreTranslates.split(" ");
        List<String> centreTranslatelist = new ArrayList<>();
        for (String s : centreTranslate) {
            String[] split = s.split("\\*");
            centreTranslatelist.addAll(Arrays.asList(split));
        }
        // 正确顺序
        List<String> rightList = new ArrayList<>();
        // 乱序
        List<String> orderList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String s : centreTranslatelist) {
            char[] chars = s.toCharArray();
            sb.setLength(0);
            int length = chars.length;
            for (int i = 0; i < length; i++) {
                char aChar = chars[i];
                // 当前下标的数据
                String s1 = new String(new char[]{aChar});
                // 是字母或者数字，拼接字符串
                if (Pattern.matches(END_MATCH_CHAINESE, s1)) {
                    sb.append(s1);
                } else {
                    if (sb.length() > 0) {
                        rightList.add(sb.toString());
                        orderList.add(sb.toString());
                        sb.setLength(0);
                    }
                    // 如果符号前面是字母需要在符号列表中加 null
                    rightList.add(s1);
                }
                // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                if (sb.length() > 0 && i == length - 1) {
                    rightList.add(sb.toString());
                    orderList.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        if (type.equals(2)) {
            if (StringUtils.isNotEmpty(translateDisturb)) {
                orderList.add(translateDisturb.replace("*", " ").replace("$", ""));
            }
        }
        Collections.shuffle(orderList);
        sentenceTranslateVo.setOrder(orderList);
        sentenceTranslateVo.setRateList(rightList);
    }

    /**
     * 将例句单词顺序打乱
     *
     * @param sentence
     * @param exampleDisturb 例句英文干扰项  为空时无干扰项
     * @return
     */
    public void getOrderEnglishList(Map<String, Object> sentenceTranslateVo, String sentence, String exampleDisturb, Integer type) {
        // 将例句按照空格拆分
        String[] words = sentence.split(" ");
        // 正确顺序
        List<String> rightList = new ArrayList<>();
        // 乱序
        List<String> orderList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String s : words) {
            s = s.replace("#", " ").replace("$", "");
            if (Pattern.matches(END_MATCH, s) && Pattern.matches(START_MATCH, s)) {
                rightList.add(s.replace("#", " ").replace("$", ""));
                orderList.add(s.replace("#", " ").replace("$", ""));
            } else {
                char[] chars = s.toCharArray();
                sb.setLength(0);
                int length = chars.length;
                for (int i = 0; i < length; i++) {
                    char aChar = chars[i];
                    // 当前下标的数据
                    String s1 = new String(new char[]{aChar});
                    // 是字母或者数字，拼接字符串
                    if (Pattern.matches(END_MATCH, s1)) {
                        sb.append(s1);
                    } else {
                        if (sb.length() > 0) {
                            rightList.add(sb.toString().replace("#", " ").replace("$", ""));
                            orderList.add(sb.toString().replace("#", " ").replace("$", ""));
                            sb.setLength(0);
                        }
                        // 如果符号前面是字母需要在符号列表中加 null
                        rightList.add(s1);
                    }
                    // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                    if (sb.length() > 0 && i == length - 1) {
                        rightList.add(sb.toString().replace("#", " ").replace("$", ""));
                        orderList.add(sb.toString().replace("#", " ").replace("$", ""));
                        sb.setLength(0);
                    }
                }
            }
        }
        if (type.equals(2)) {
            if (StringUtils.isNotEmpty(exampleDisturb)) {
                orderList.add(exampleDisturb.replace("#", " ").replace("$", ""));
            }
        }
        Collections.shuffle(orderList);
        sentenceTranslateVo.put("order", orderList);
        sentenceTranslateVo.put("rateList", rightList);
    }

    public static void main(String[] args) {
        SentenceTranslateVo sentenceTranslateVo = new SentenceTranslateVo();
        TestResultUtil testResultUtil = new TestResultUtil();
        testResultUtil.getOrderEnglishList(sentenceTranslateVo,
                "I felt lucky, as all my teachers gave me much encouragement and I enjoyed all my subjects:English, History, English#Literature, Computer#Science, Maths, Science, PE, Art, Cooking and French.", null, 1);
        System.out.println(sentenceTranslateVo);
    }

    /**
     * 将例句单词顺序打乱
     *
     * @param sentence
     * @param exampleDisturb 例句英文干扰项  为空时无干扰项
     * @param type           是否是暴走模式<ul>
     *                       <li>1：非暴走模式</li>
     *                       <li>2：暴走模式</li>
     *                       </ul>
     * @return
     */
    public void getOrderEnglishList(SentenceTranslateVo sentenceTranslateVo, String sentence, String exampleDisturb, Integer type) {
        // 将例句按照空格拆分
        String[] words = sentence.split(" ");
        // 正确顺序
        List<String> rightList = new ArrayList<>();
        // 乱序
        List<String> orderList = new ArrayList<>();
        // 以字母或数字结尾
        StringBuilder sb = new StringBuilder();
        for (String s : words) {
            s = s.replace("#", " ").replace("$", "");
            if (Pattern.matches(END_MATCH, s) && Pattern.matches(START_MATCH, s)) {
                rightList.add(s.replace("#", " ").replace("$", ""));
                orderList.add(s.replace("#", " ").replace("$", ""));
            } else {
                char[] chars = s.toCharArray();
                sb.setLength(0);
                int length = chars.length;
                for (int i = 0; i < length; i++) {
                    char aChar = chars[i];
                    // 当前下标的数据
                    String s1 = new String(new char[]{aChar});
                    // 是字母或者数字，拼接字符串
                    if (Pattern.matches(END_MATCH, s1)) {
                        sb.append(s1);
                    } else {
                        if (i == 0) {
                            rightList.add(s1);
                        } else {
                            if (i != (length - 1)) {
                                char longChar = chars[i + 1];
                                if (Pattern.matches(END_MATCH2, s1)) {
                                    sb.append(s1);
                                } else {
                                    if (sb.length() > 0) {
                                        rightList.add(sb.toString().replace("#", " ").replace("$", ""));
                                        orderList.add(sb.toString().replace("#", " ").replace("$", ""));
                                        sb.setLength(0);
                                    }
                                    rightList.add(s1);
                                }
                            } else {
                                if (sb.length() > 0) {
                                    rightList.add(sb.toString().replace("#", " ").replace("$", ""));
                                    orderList.add(sb.toString().replace("#", " ").replace("$", ""));
                                    sb.setLength(0);
                                }
                                // 如果符号前面是字母需要在符号列表中加 null
                                rightList.add(s1);
                            }
                        }

                    }
                    // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                    if (sb.length() > 0 && i == length - 1) {
                        rightList.add(sb.toString().replace("#", " ").replace("$", ""));
                        orderList.add(sb.toString().replace("#", " ").replace("$", ""));
                        sb.setLength(0);
                    }
                }
            }
        }
        if (type.equals(2)) {
            if (StringUtils.isNotEmpty(exampleDisturb)) {
                orderList.add(exampleDisturb.replace("#", " ").replace("$", ""));
            }
        }
        Collections.shuffle(orderList);
        sentenceTranslateVo.setOrder(orderList);
        sentenceTranslateVo.setRateList(rightList);
        sentenceTranslateVo.setEnglishList(rightList);
        sentenceTranslateVo.setOrderEnglish(orderList);
    }


    /**
     * 获取指定数量的随机题目
     *
     * @param target     需要封装的测试题集合
     * @param subjectNum 需要展示的当前类型的试题数量
     * @return map key:单词id；value：单词对象
     */
    private Map<Long, Vocabulary> getTestVocabulary(Integer subjectNum, List<Vocabulary> target) {
        Collections.shuffle(target);

        Map<Long, Vocabulary> map = new LinkedHashMap<>();
        target.stream()
                // 过滤掉题目中含有答案信息的数据
                .filter(vocabulary -> StringUtil.isNotEmpty(vocabulary.getWordChinese()) && !vocabulary.getWordChinese().contains(vocabulary.getWord()))
                .limit(subjectNum).forEach(vocabulary -> map.put(vocabulary.getId(), vocabulary));
        return map;
    }

}
