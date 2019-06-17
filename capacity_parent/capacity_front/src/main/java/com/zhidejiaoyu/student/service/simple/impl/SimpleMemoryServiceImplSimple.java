package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.Vo.simple.SimpleCapacityVo;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleLearnTimeUtil;
import com.zhidejiaoyu.common.utils.simple.language.SimpleBaiduSpeak;
import com.zhidejiaoyu.student.common.PerceiveEngine;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.common.SaveLearnAndCapacity;
import com.zhidejiaoyu.student.service.simple.SimpleMemoryServiceSimple;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;


@Service
public class SimpleMemoryServiceImplSimple extends SimpleBaseServiceImpl<SimpleVocabularyMapper, Vocabulary> implements SimpleMemoryServiceSimple {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /** 默写模块答错3次, 黄金记忆点时间延长一个小时*/
    private final int pushRise = 3;

    @Value("${ftp.prefix}")
    private String ftpPrefix;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private SimpleBaiduSpeak simpleBaiduSpeak;

    @Autowired
    private SaveLearnAndCapacity saveLearnAndCapacity;

    @Autowired
    private SimpleTestRecordMapper simpleTestRecordMapper;

    @Autowired
    private SimpleSimpleCapacityMapper simpleSimpleCapacityMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleTestServiceImplSimple testServiceImpl;

    @Autowired
    private SimpleUnitVocabularyMapper simpleUnitVocabularyMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private ExecutorService executorService;

    /**
     * 9大学习页面
     * 	1 2 3 4 6 8 9模块需要测试-
     */
    @Override
    public Object getMemoryWord(HttpSession session, int type, Long courseId, Long unitId, boolean flag, boolean anew) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // 是否需要课程前测,课后测试,单元前测,单元闯关
        String testId;

        // 判断当前模块是否需要走测试逻辑
        boolean testModel = testModel(type);

        // 判断是否需要重置当前学生该单元数据
        if(anew) {
        	// 课程所有单词 == 课程已学单词
            int allCount = redisOpt.wordCountInCourse(courseId);
            int allLearn = learnMapper.getAllCountWordByCourse(courseId, studentId);
        	if(allLearn >= allCount) {
	        	// 是否做了课后测试
                Long courseTest = simpleTestRecordMapper.getXHTest(courseId, studentId);
                if (courseTest == null && testModel) {
                    // 弹框强制测试
                    return super.toUnitTest(301, "学后测试");
	    		}
        	}

        	Long lastUnitId = unitMapper.selectMaxUnitIdInCourse(courseId);
        	if (Objects.equals(lastUnitId, unitId)) {
                // 获取单元新单词
                SimpleCapacityVo simpleCapacityNew = vocabularyMapper.showWordSimple(unitId, studentId, type);
                // 表示本课程已学完
                if(simpleCapacityNew == null) {
                    resetTheUnit(studentId, unitId, simpleCommonMethod.getTestType(type));
                    return ServerResponse.createBySuccess(302, "当前选择课程已学习完毕，可以重新学习，温故知新哦。");
                }
            }
    		// 清除数据
        	resetTheUnit(studentId, unitId, simpleCommonMethod.getTestType(type));
        }

        if(testModel) {
	        // 1.判断是否需要单元前测
            testId = simpleTestRecordMapper.getWhetherTest(studentId, courseId, unitId, type, "单元前测");
            if (testId == null) {
                // 需要进行单元前测
                return super.toUnitTest(300, "单元前测");
            }
        }

        // 获取当前单元下的所有单词的总个数
        long wordCount = redisOpt.getWordCountWithUnit(unitId);

        if (wordCount == 0) {
            log.error("单元 {} 下没有单词信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
        }

        // 查询学生当前单元当前模块下已学习单词的个数
        Long plan = learnMapper.getSimpleLearnByStudentIdByModel(studentId, unitId, type);

        // 判断是否需要单元闯关测试或学后测试
        if (plan >= wordCount && testModel) {
            testId = simpleTestRecordMapper.getWhetherTest(studentId, courseId, unitId, type, "单元闯关测试");
        	// 强制学生进行单元闯关测试
 	        if(testId == null) {
                // 弹框强制测试
                return super.toUnitTest(301, "单元闯关测试");
 	        }
        }

        // 获取单元新单词
        SimpleCapacityVo simpleCapacityNew = vocabularyMapper.showWordSimple(unitId, studentId, type);

        // 1.查询有没有需要复习的数据
        List<SimpleCapacityVo> simpleWhetherReviews = simpleSimpleCapacityMapper.getSimpleWhetherFeview(studentId, unitId, new Date(), type);

    	SimpleCapacityVo simpleCapacityVo = null;
    	if (simpleWhetherReviews.size() > 1) {
            simpleSimpleCapacityMapper.deleteById(simpleWhetherReviews.get(1).getCapacityId());
            simpleCapacityVo = simpleWhetherReviews.get(0);
         } else if (simpleWhetherReviews.size() == 1) {
    	    simpleCapacityVo = simpleWhetherReviews.get(0);
        }
        // 2. 如果记忆追踪中没有需要复习的, 去单词表中取出一个单词,条件是(learn表中单词id不存在的)
        if (simpleCapacityVo == null) {
        	simpleCapacityVo = simpleCapacityNew;

        	if (simpleCapacityVo == null && testModel) {
                testId = simpleTestRecordMapper.getWhetherTest(studentId, courseId, unitId, type, "单元闯关测试");
                // 强制学生进行单元闯关测试
                if(testId == null) {
                    // 弹框强制测试
                    return super.toUnitTest(301, "单元闯关测试");
                }
            }

            // 词汇考点或者语法辨析当前课程如果学习完毕进行提示
            if (simpleCapacityVo == null && !testModel) {
                // 清除数据
                resetTheUnit(studentId, unitId, simpleCommonMethod.getTestType(type));
                return ServerResponse.createBySuccess(302, "当前选择课程已学习完毕，可以重新学习，温故知新哦。");
            }

            // 是新单词
            simpleCapacityVo.setStudyNew(true);
            // 记忆强度
            simpleCapacityVo.setMemory_strength(0.00);
            // 答错次数
            simpleCapacityVo.setFault_time(0);
            // 学生id
            simpleCapacityVo.setStudent_id(studentId);

		}else {
			// 不是新词
			simpleCapacityVo.setStudyNew(false);
		}
        // 图片url
        if (StringUtils.isNotEmpty(simpleCapacityVo.getRecordpicurl())) {
            simpleCapacityVo.setRecordpicurl(ftpPrefix+simpleCapacityVo.getRecordpicurl());
        }
        // 已学单元单词
        simpleCapacityVo.setPlan(plan);
        // 总单元单词数量
        simpleCapacityVo.setWordCount((long) wordCount);

        // 记忆难度
        SimpleCapacity simpleCapacity = new SimpleCapacity();
        simpleCapacity.setStudentId(studentId);
        simpleCapacity.setVocabularyId(simpleCapacityVo.getId());
        simpleCapacity.setUnitId(simpleCapacityVo.getUnit_id());
        simpleCapacity.setFaultTime(simpleCapacityVo.getFault_time());
        simpleCapacity.setMemoryStrength(simpleCapacityVo.getMemory_strength());
        int hard = memoryDifficultyUtil.getMemoryDifficulty(simpleCapacity, type);
        simpleCapacityVo.setMemoryDifficulty(hard);

        simpleCapacityVo.setEngine(PerceiveEngine.getPerceiveEngine(hard, simpleCapacityVo.getMemory_strength()));

		// 单词读音url
		if(type == 1 || type == 2 || type == 3 || type == 4 || type==8 || type == 9) {
			simpleCapacityVo.setReadUrl(simpleBaiduSpeak.getLanguagePath(simpleCapacityVo.getWord()));
		}

		// 去掉音标
		if(type != 1 && type != 3 && type != 8) {
			simpleCapacityVo.setSoundMark(null);
		}

        // 说明学生是第一次在本系统学习,记录首次学习时间
        if (student.getFirstStudyTime() == null) {
			student.setFirstStudyTime(new Date());
			simpleStudentMapper.updateByPrimaryKeySelective(student);
			student= simpleStudentMapper.selectById(student.getId());
			session.setAttribute(UserConstant.CURRENT_STUDENT, student);
		}
        // 记录学生开始学习该单词/词组/例句的有效时长
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        return ServerResponse.createBySuccess(simpleCapacityVo);
    }

    /**
     * 重置学生该单元所有数据
     *  @param studentId
     * @param unitId
     * @param studyModel
     */
    private void resetTheUnit(Long studentId, Long unitId, String studyModel) {
        learnMapper.updateTypeByStudentIdAndUnitId(studentId, unitId, studyModel);
    	simpleSimpleCapacityMapper.deleteByStudenIdByUnitId(studentId, unitId);
        simpleTestRecordMapper.updateByStudentAndUnitId(studentId, unitId);
	}



	/**
     * 需要测试的模块
     *
     * @param type
     */
    private boolean testModel(int type) {
    	if(type == 1 || type == 2 || type == 3 || type == 4 || type == 6 || type == 8 || type == 9) {
    		return true;
    	}else {
    		return false;
    	}
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveMemoryWord(HttpSession session, Learn learn, Boolean known, Integer plan,
                                                 Integer total, Integer type) {
        if (type == null) {
            return ServerResponse.createByErrorMessage("type 不能为空！");
        }
        // 保存学习记录
        saveLearnAndCapacity.saveLearnCapacity(session, learn, known, type);
        Student student = super.getStudent(session);

        // 5,7没有测试的模块开启下一单元
        // 已学 >= 总单词
        if((type == 5 || type == 7) && plan + 1 >= total) {
        	String state = testServiceImpl.unlockUnit(student, learn.getCourseId(), learn.getUnitId(), 0);

        	// 5或7模块写入单元闯关测试记录, 用于遍历单元使用
        	TestRecord tr = new TestRecord();
        	tr.setStudentId(learn.getStudentId());
        	tr.setPoint(0);
        	tr.setCourseId(learn.getCourseId());
        	tr.setUnitId(learn.getUnitId());
        	tr.setGenre("单元闯关测试");
        	tr.setStudyModel(type == 5 ? "词汇考点" : "语法辨析");
        	tr.setTestStartTime(new Date());
        	tr.setTestEndTime(new Date());
        	simpleTestRecordMapper.insert(tr);

        	if(("1").equals(state)) {
        		return ServerResponse.createBySuccess(303, "当前单元已学完，已为你开启下一单元");
        	}
        }

        // 默写模块错过三次在记忆时间上再加长两小时
        if(type == 8 || type == 9) {
        	// 查询错误次数>=3
        	Integer faultTime;
            try{
                faultTime = simpleSimpleCapacityMapper.getFaultTime(student.getId(), learn.getVocabularyId(), learn.getUnitId(), type);
            } catch (Exception e) {
                log.error("学生[{}->{}]查询第[{}]单元类型为[{}]的单词[{}]错误次数时出错", student.getStudentName(), student.getId(), learn.getUnitId(), type, learn.getVocabularyId(), e);
                return ServerResponse.createByError();
            }
            if(faultTime != null && faultTime >= 3) {
        		// 如果错误次数>=3, 黄金记忆时间推迟3小时
        		simpleSimpleCapacityMapper.updatePush(student.getId(), learn.getVocabularyId(), pushRise, type);
        	}
        }

        // 统计初出茅庐勋章
        medalAwardAsync.inexperienced(student);

        return ServerResponse.createBySuccess("ok");
    }

    @Override
    public ServerResponse<String> clearFirst(HttpSession session, String studyModel) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        simpleCommonMethod.clearFirst(student.getId(), studyModel);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> todayTime(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long id = student.getId();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 有效时长  !
        Integer valid = super.getTodayValidTime(id);
        // 在线时长 !
        Integer online = super.getTodayOnlineTime(session);
        result.put("online", online);
        result.put("valid", valid);
        // 今日学习效率 !
        if (valid != null && online != null) {
            String efficiency = SimpleLearnTimeUtil.efficiency(valid, online);
            if ("100%".equals(efficiency) && !valid.equals(online)) {
                result.put("efficiency", "99%");
            } else {
                result.put("efficiency", efficiency);
            }
        } else {
            result.put("efficiency", "0%");
        }
        // todo:跟踪日志
        if (valid == null) {
            log.error("今日有效时长 valid = null;");
        }
        return ServerResponse.createBySuccess(result);
    }


}