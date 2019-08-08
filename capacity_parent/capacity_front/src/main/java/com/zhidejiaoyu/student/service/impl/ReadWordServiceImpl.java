package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.Vo.read.NewWordsBookVo;
import com.zhidejiaoyu.common.Vo.read.WordInfoVo;
import com.zhidejiaoyu.common.Vo.study.MemoryStudyVo;
import com.zhidejiaoyu.common.constant.read.ReadContentConstant;
import com.zhidejiaoyu.common.dto.read.SaveStrengthenDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.ReadContentMapper;
import com.zhidejiaoyu.common.mapper.ReadWordMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.MemoryStrengthUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.math.MathUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.language.SimpleYouDaoTranslate;
import com.zhidejiaoyu.student.common.PerceiveEngine;
import com.zhidejiaoyu.student.service.ReadWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2019-07-23
 */
@Service
public class ReadWordServiceImpl extends BaseServiceImpl<ReadWordMapper, ReadWord> implements ReadWordService {

    /**
     * 是否以单词或者数字或者 ' 结尾
     */
    private static final String END_MATCH = ".*[a-zA-z0-9'\\u4e00-\\u9fa5]$";

    /**
     * 是否以单词或者数字或者 ' 开头
     */
    private static final String START_MATCH = "^[a-zA-z0-9'\\u4e00-\\u9fa5].*";

    @Autowired
    private ReadWordMapper readWordMapper;

    @Autowired
    private SimpleYouDaoTranslate simpleYouDaoTranslate;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private ReadContentMapper readContentMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private MemoryStrengthUtil memoryStrengthUtil;

    @Override
    public ServerResponse<Object> getWordInfo(HttpSession session, Long courseId, String word) {
        Vocabulary vocabulary = vocabularyMapper.selectByWord(word);
        WordInfoVo wordInfoVo = simpleYouDaoTranslate.getWordInfoVo(word);
        if (vocabulary == null) {
            wordInfoVo.setWordId(null);
            wordInfoVo.setCanAddNewWordsBook(false);
            return ServerResponse.createBySuccess(wordInfoVo);
        }

        wordInfoVo.setWordId(vocabulary.getId());
        Student student = super.getStudent(session);
        int count = readWordMapper.countByCourseIdAndWordIdAndNotKnow(student.getId(), courseId, vocabulary.getId());
        // 如果当前选中的单词存在于当前课程的生词手册中，隐藏“添加到生词手册”按钮
        if (count > 0) {
            wordInfoVo.setCanAddNewWordsBook(false);
        } else {
            wordInfoVo.setCanAddNewWordsBook(true);
        }
        return ServerResponse.createBySuccess(wordInfoVo);


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> addNewWordsBook(HttpSession session, ReadWord readWord) {
        Vocabulary vocabulary = vocabularyMapper.selectById(readWord.getWordId());
        if (vocabulary == null) {
            throw new ServiceException(500, "未查询到 id=[" + readWord.getWordId() + "] 的单词信息！");
        }
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        int count = readWordMapper.countByCourseIdAndWordIdAndNotKnow(studentId, readWord.getCourseId(), readWord.getWordId());
        // 如果当前单词在当前课程的生词手册中已经存在，不再进行保存，否则正常保存
        if (count > 0) {
            return ServerResponse.createBySuccess();
        }

        readWord.setStudentId(studentId);
        this.saveReadWord(readWord);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getNewWordsBook(HttpSession session, Long courseId) {
        Student student = super.getStudent(session);
        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<ReadWord> readWords = readWordMapper.selectByStudentIdCourseId(student.getId(), courseId);
        if (readWords.size() == 0) {
            return ServerResponse.createBySuccess(new ArrayList<>());
        }
        PageInfo<ReadWord> readWordsPageInfo = new PageInfo<>(readWords);

        List<Long> wordIds = readWords.stream().map(ReadWord::getWordId).collect(Collectors.toList());
        List<Vocabulary> vocabularies = vocabularyMapper.selectByWordIds(wordIds);
        List<NewWordsBookVo> vos = vocabularies.stream().map(vocabulary -> {
            NewWordsBookVo newWordsBookVo = new NewWordsBookVo();
            newWordsBookVo.setChinese(vocabulary.getWordChinese());
            newWordsBookVo.setPhonetic(vocabulary.getSoundMark());
            newWordsBookVo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
            newWordsBookVo.setWord(vocabulary.getWord());
            return newWordsBookVo;
        }).collect(Collectors.toList());

        PageInfo<NewWordsBookVo> newWordsBookVoPageInfo = new PageInfo<>();
        newWordsBookVoPageInfo.setList(vos);
        newWordsBookVoPageInfo.setPages(readWordsPageInfo.getPages());
        newWordsBookVoPageInfo.setTotal(readWordsPageInfo.getTotal());

        return ServerResponse.createBySuccess(PageUtil.packagePage(newWordsBookVoPageInfo));
    }

    @Override
    public ServerResponse markWordRed(HttpSession session, Long courseId, Long readTypeId) {
        List<ReadContent> readContents = readContentMapper.selectByReadTypeId(readTypeId);
        if (readContents.size() == 0) {
            return ServerResponse.createByError(ResponseCode.NO_DATA);
        }

        StringBuilder sb = new StringBuilder();

        Map<String, String> translateMap = new HashMap<>(16);
        readContents.forEach(readContent -> {
            sb.append(readContent.getSentence().replace(ReadContentConstant.BLANK, "。")).append(" ");
            translateMap.put(readContent.getSentence(), readContent.getTranslate());
        });
        // 整篇文章
        List<String> allWords = getAllWords(sb.toString().trim());

        Student student = super.getStudent(session);
        // 需要标红的单词
        List<String> needMarkRedWords = readWordMapper.selectNeedMarkRedWords(student.getId(), courseId, allWords, readTypeId);

        if (needMarkRedWords == null || needMarkRedWords.size() == 0) {
            return ServerResponse.createBySuccess(301, "未查询到生词信息！");
        }
        if (needMarkRedWords.size() > 50) {
            return ServerResponse.createBySuccess(300, "生词大于50个单词!");
        }

        Map<String, String> newWordsMap = new HashMap<>(16);
        for (String word : needMarkRedWords) {
            newWordsMap.put(word.trim(), word.trim());
        }

        List<Object> returnList = this.getMarkWordRedList(translateMap, allWords, newWordsMap);

        if (returnList.size() == 0) {
            return ServerResponse.createBySuccess(301, "未查询到生词信息！");
        }

        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public ServerResponse startStrengthen(HttpSession session, Long courseId, Integer type) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();
        MemoryStudyVo memoryStudyVo = readWordMapper.selectNeedReview(studentId, courseId, type);
        if (memoryStudyVo == null) {
            return ServerResponse.createByError(300, "当前模块没有需要强化的单词！");
        }
        Long totalNeedReviewCount = readWordMapper.countNeedReview(studentId, courseId, type);
        memoryStudyVo.setImgUrl(GetOssFile.getPublicObjectUrl(memoryStudyVo.getImgUrl()));
        memoryStudyVo.setReadUrl(baiduSpeak.getLanguagePath(memoryStudyVo.getWord()));
        memoryStudyVo.setWordCount(totalNeedReviewCount);
        memoryStudyVo.setStudyNew(false);

        ReadWord readWord = readWordMapper.selectByStudentIdAndCourseIdAndWordId(studentId, courseId, memoryStudyVo.getWordId(), type);
        memoryStudyVo.setMemoryDifficulty(memoryDifficultyUtil.getReadWordDifficulty(readWord));
        memoryStudyVo.setEngine(PerceiveEngine.getPerceiveEngine(memoryStudyVo.getMemoryDifficulty(), memoryStudyVo.getMemoryStrength()));

        if (type == 2) {
            // 封装单词图鉴
            int random = MathUtil.getRandom(1, 1000);
            List<Vocabulary> vocabularies = vocabularyMapper.selectPictureRandom(random, 3);

            List<Map<String, Boolean>> mapList = new ArrayList<>(4);
            Map<String, Boolean> map = new HashMap<>(16);
            map.put(memoryStudyVo.getWord(), true);
            mapList.add(map);
            for (Vocabulary vocabulary : vocabularies) {
                map = new HashMap<>(16);
                map.put(vocabulary.getWord(), false);
                mapList.add(map);
            }
            Collections.shuffle(mapList);
            memoryStudyVo.setWordChineseList(mapList);
        }

        return ServerResponse.createBySuccess(memoryStudyVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveStrengthen(HttpSession session, SaveStrengthenDto dto) {

        Student student = super.getStudent(session);

        ReadWord readWord = readWordMapper.selectByStudentIdAndCourseIdAndWordId(student.getId(), dto.getCourseId(), dto.getWordId(), dto.getType());
        Double memoryStrength = memoryStrengthUtil.getStudyMemoryStrength(readWord.getMemoryStrength(), dto.getIsKnown());
        Learn learn = this.getLearnInfo(dto, student);
        if (memoryStrength == 1) {
            readWordMapper.deleteById(readWord.getId());
            if (learn != null) {
                learnMapper.deleteById(learn.getId());
            }
            return ServerResponse.createBySuccess();
        }

        readWord.setMemoryStrength(memoryStrength);
        readWord.setPush(GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date()));
        if (!dto.getIsKnown()) {
            readWord.setErrorCount(readWord.getErrorCount() + 1);
        }
        readWord.setUpdateTime(new Date());
        readWordMapper.updateById(readWord);

        if (learn != null) {
            learn.setStudyCount(learn.getStudyCount() + 1);
            learnMapper.updateById(learn);
        }

        return ServerResponse.createBySuccess();
    }

    private Learn getLearnInfo(SaveStrengthenDto dto, Student student) {
        ReadWord readWord = new ReadWord();
        readWord.setCourseId(dto.getCourseId());
        readWord.setStudentId(student.getId());
        readWord.setWordId(dto.getWordId());
        return learnMapper.selectReadWord(readWord, memoryDifficultyUtil.getReadWordStudyModel(dto.getType()));
    }


    /**
     * 获取生词标红响应数据
     *
     * @param translateMap 每句话的翻译
     * @param allWords     文章中所有的单词和字符
     * @param newWordsMap  需要标红的单词
     * @return
     */
    private List<Object> getMarkWordRedList(Map<String, String> translateMap, List<String> allWords, Map<String, String> newWordsMap) {
        List<Object> returnList = new ArrayList<>();
        // 存放一句话中的各个单词及各个单词是不是生词信息
        List<Map<String, Object>> wordInfoList = new ArrayList<>();
        // 当前整句话的信息
        List<Map<String, Object>> sentenceInfoList = new ArrayList<>();
        // 存放当前单词、该单词是不是生词
        Map<String, Object> wordInfoMap;
        // 将单词和字符拼接成句子
        StringBuilder sentence = new StringBuilder();
        int i = 0;
        for (String word : allWords) {
            wordInfoMap = new HashMap<>(16);
            if (ReadContentConstant.PARAGRAPH_SPLIT.equals(word)) {
                sentence.append(" ").append(word);
                if (i == 0) {
                    // 第一段开始还没有段落内容，不保存，接着获取当前段落的内容
                    continue;
                }
                // 上一个段落结束，保存上一个段落的内容
                returnList.add(sentenceInfoList);
                sentenceInfoList = new ArrayList<>();
            } else if (!Pattern.matches(END_MATCH, word)) {
                if ("。".equals(word)) {
                    // 说明该处是挖出的空格，让学生选择或者填写
                    packageWordInfoList(wordInfoList, wordInfoMap, null, false);
                    sentence.append(" ").append(ReadContentConstant.BLANK);
                    wordInfoList = packageSentenceInfoList(translateMap, wordInfoList, sentenceInfoList, sentence);
                } else {
                    packageWordInfoList(wordInfoList, wordInfoMap, word, false);
                    sentence.append(word);
                    wordInfoList = packageSentenceInfoList(translateMap, wordInfoList, sentenceInfoList, sentence);
                }
            } else {
                // 当前元素是单词，正常拼接
                packageWordInfoList(wordInfoList, wordInfoMap, word, newWordsMap.containsKey(word));
                if (ReadContentConstant.PARAGRAPH_SPLIT.equals(sentence.toString().trim())) {
                    // 段落标识符后面不加空格
                    sentence.append(word);
                } else {
                    // 正常内容但此后面都加上空格
                    sentence.append(" ").append(word);
                }
            }
            i++;
        }
        return returnList;
    }

    /**
     * 封装每句话的数据
     *
     * @param translateMap     每句话的翻译
     * @param wordInfoList     单词状态
     * @param sentenceInfoList 每句话翻译和单词的组合
     * @param sentence         当前整句话
     * @return
     */
    private List<Map<String, Object>> packageSentenceInfoList(Map<String, String> translateMap, List<Map<String, Object>> wordInfoList, List<Map<String, Object>> sentenceInfoList, StringBuilder sentence) {
        String sentenceTrim = sentence.toString().trim();
        if (translateMap.containsKey(sentenceTrim)) {
            // 如果当前元素不是单词，有可能是整句话，查找该句话的翻译
            Map<String, Object> sentenceMap = new HashMap<>(16);
            sentenceMap.put("translate", translateMap.get(sentenceTrim));
            sentenceMap.put("words", wordInfoList);
            sentenceInfoList.add(sentenceMap);
            wordInfoList = new ArrayList<>();
            sentence.setLength(0);
        }
        return wordInfoList;
    }

    /**
     * 封装每句话中各个单词的标红状态
     *
     * @param wordInfoList 单词状态
     * @param wordInfoMap  当前句子中所有单词状态
     * @param word         单词
     * @param red          是否标红
     */
    private void packageWordInfoList(List<Map<String, Object>> wordInfoList, Map<String, Object> wordInfoMap, String word, boolean red) {
        wordInfoMap.put("word", word);
        wordInfoMap.put("red", red);
        wordInfoList.add(wordInfoMap);
    }

    static List<String> getAllWords(String text) {

        // 把各个段落分开
        String[] split = text.split(ReadContentConstant.PARAGRAPH_SPLIT);
        // 存放所有单词和
        List<String> rightList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (String sentence : split) {
            String[] words = sentence.split(" ");
            splitPoint(rightList, sb, words);
            rightList.add(ReadContentConstant.PARAGRAPH_SPLIT);
        }
        return rightList;
    }

    /**
     * 将标点符号和单词拆开放在集合中
     *
     * @param rightList  最终返回的单词和符号拆开后的集合
     * @param sb         用于拼接单词
     * @param words      需要处理的单词数据（其中包含符号）
     */
    static void splitPoint(List<String> rightList, StringBuilder sb, String[] words) {
        for (String s : words) {
            if (Pattern.matches(END_MATCH, s) && Pattern.matches(START_MATCH, s)) {
                rightList.add(s);
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
                            rightList.add(sb.toString());
                            sb.setLength(0);
                        }
                        rightList.add(s1);
                    }
                    // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                    if (sb.length() > 0 && i == length - 1) {
                        rightList.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        }
    }

    private void saveReadWord(ReadWord readWord) {
        Date now = new Date();
        Date pushTime = GoldMemoryTime.getGoldMemoryTime(0.12, now);

        readWord.setType(1);
        readWord.setMemoryStrength(0.12);
        readWord.setPush(pushTime);
        readWord.setCreateTime(now);
        readWord.setUpdateTime(now);
        readWord.setErrorCount(1);
        this.insert(readWord);

        readWord.setType(2);
        this.insert(readWord);

        readWord.setType(3);
        this.insert(readWord);

        readWord.setType(4);
        this.insert(readWord);
    }
}
