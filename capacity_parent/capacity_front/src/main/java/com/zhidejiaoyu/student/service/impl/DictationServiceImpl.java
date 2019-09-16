package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.DictationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DictationServiceImpl extends BaseServiceImpl<VocabularyMapper, Vocabulary> implements DictationService {

	/** 单词 */
	@Autowired
	private VocabularyMapper vocabularyMapper;

	/** 单元 */
	@Autowired
	private UnitMapper unitMapper;

	/** 记忆追踪,听写*/
	@Autowired
	private CapacityListenMapper capacityListenMapper;

	/** 学习信息 */
	@Autowired
	private LearnMapper learnMapper;

	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	private BaiduSpeak baiduSpeak;

	@Override
	public Object dictationShow(String unitId, HttpSession session, Long[] ignoreWordId) {
		Map<String,Object> map = new HashMap<>(16);

		// 获取当前学生信息
		Student student = getStudent(session);
		Long id = student.getId();

		if (student.getFirstStudyTime() == null) {
			// 说明学生是第一次在本系统学习，记录首次学习时间
			student.setFirstStudyTime(new Date());
			studentMapper.updateByPrimaryKeySelective(student);
			session.setAttribute(UserConstant.CURRENT_STUDENT, student);
		}

		// 记录学生开始学习该单词/例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

		// 1. 查询智能听写记忆追踪中是否有需要复习的单词
		Vocabulary vocabulary = capacityListenMapper.selectCapacityListen(unitId, id);

		// 2. 如果记忆追踪中没有需要复习的, 去单词表中取出一个单词,条件是(learn表中单词id不存在的)
        if (vocabulary == null) {
			vocabulary = vocabularyMapper.showWord(unitId, id);
            // 是新单词
            map.put("studyNew", true);
            // 记忆强度
            map.put("memoryStrength", 0.00);
		} else {
			// 不是新词
			map.put("studyNew", false);
			// 记忆强度
			map.put("memoryStrength", vocabulary.getMemory_strength());
		}

		// 如果该单元单词都已经学习完毕并且没有达到黄金记忆点的单词，获取生词
        if (vocabulary == null) {
			Long wordId = capacityListenMapper.selectUnknownWordByUnitId(student, unitId, ignoreWordId);
			if (wordId != null) {
				vocabulary = vocabularyMapper.selectById(wordId);
			}
		}

		// 单元单词已学完,去单元测试
        if (vocabulary == null) {
			return super.toUnitTest();
		}

        // id
        map.put("id", vocabulary.getId());
        // 单词
        map.put("word", vocabulary.getWord());
        // 中文意思
        map.put("wordChinese", vocabulary.getWordChinese());

        // 如果单词没音节,把音节字段设置为单词
        if (StringUtil.isEmpty(vocabulary.getSyllable())) {
            map.put("wordyj", vocabulary.getWord());
        } else {
            // 音节
            map.put("wordyj", vocabulary.getSyllable());
		}
		// 音标,读音url,词性
		map.put("soundmark", vocabulary.getSoundMark());
		// 读音url
		map.put("readUrl", baiduSpeak.getLanguagePath(vocabulary.getWord()));

		// 3. count单元表单词有多少个    /.
		Integer count = unitMapper.countWordByUnitid(unitId);
		map.put("wordCount", count);

		// 4. 该单元已学单词  ./
		map.put("plan", learnMapper.learnCountWord(id, Integer.parseInt(unitId), "慧听写"));

		// 5. 是否是第一次学习慧听写，true:第一次学习，进入学习引导页；false：不是第一次学习
		Integer the = learnMapper.theFirstTime(id);
		if (the == null) {
			map.put("firstStudy", true);
			// 初始化一条数据，引导页进行完之后进入学习页面
			Learn learn = new Learn();
			learn.setStudentId(student.getId());
			learn.setStudyModel("慧听写");
			learnMapper.insert(learn);
		} else {
			map.put("firstStudy", false);
		}

		return ServerResponse.createBySuccess(map);
	}

}
