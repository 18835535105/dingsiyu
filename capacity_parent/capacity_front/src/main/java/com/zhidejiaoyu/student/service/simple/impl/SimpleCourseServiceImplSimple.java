package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentCourse;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleCalculateTimeUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.simple.SimpleCourseService;
import com.zhidejiaoyu.student.vo.CoursePlanVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 课程业务接口实现层
 *
 * @author qizhentao
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
public class SimpleCourseServiceImplSimple extends SimpleBaseServiceImpl<SimpleCourseMapper, Course> implements SimpleCourseService {

    /**
     * 注入课程mapper
     */
    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleCapacityMemoryMapper simpleCapacityMemoryMapper;

    @Autowired
    private SimpleCapacityListenMapper capacityListenMapper;

    @Autowired
    private SimpleCapacityWriteMapper simpleCapacityWriteMapper;

    @Autowired
    private SimpleSentenceListenMapper simpleSentenceListenMapper;

    @Autowired
    private SimpleSentenceWriteMapper simpleSentenceWriteMapper;

    @Autowired
    private SimpleSentenceMapper simpleSentenceMapper;

    @Autowired
    private SimpleSentenceTranslateMapper simpleSentenceTranslateMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleTestRecordMapper simpleTestRecordMapper;

    @Autowired
    private SimpleStudentCourseMapper simpleStudentCourseMapper;

    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

    @Autowired
    private SimpleSimpleStudentUnitMapper simpleSimpleStudentUnitMapper;

    @Autowired
    private SimpleSimpleCapacityMapper simpleSimpleCapacityMapper;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Resource
    private RedisOpt redisOpt;

    @Override
    public List chooseGrade(HttpSession session) {

        long id = StudentIdBySession(session);

        // 去student_unit查询分配的所有年级
        List<String> list = simpleCourseMapper.chooseGrade(id);

        Course cou = new Course();
        cou.setId(Long.valueOf(id));

        List<Object> liResult = new ArrayList<Object>();

        // 根据年级, 学生id查询标签
        for (String grade : list) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("grade", grade);

            cou.setGrade(grade);
            List<Map<String, Object>> listMap = simpleCourseMapper.chooseGradeToLabel(cou);

            result.put("listLabel", listMap);
            result.put("msg", false);
            liResult.add(result);
        }
        return liResult;
    }

    @Override
    public List<Course> retGrade() {
        return simpleCourseMapper.retGrade();
    }

    @Override
    public List<Course> retVersion(String grade) {
        return simpleCourseMapper.retVersion(grade);
    }

    @Override
    public List<Course> retLabel(String grade, String version) {
        return simpleCourseMapper.retLabel(grade, version);
    }

    @Override
    public ServerResponse<List<CoursePlanVo>> getCoursePlan(HttpSession session, Long courseId, Integer type) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        List<CoursePlanVo> vos = new ArrayList<>();
        String studyModel;
        int learnedCount;
        int pushCount;

        if (type == 1) {
            // 获取当前课程的单词总量
            int wordCount = vocabularyMapper.countByCourseId(courseId, 2);

            // 获取慧记忆模块本课程已学单词量和达到黄金记忆点的待复习单词量
            studyModel = "慧记忆";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = simpleCapacityMemoryMapper.countNeedReviewByStudentIdAndCourseId(studentId, courseId);
            this.packageMemoryVo(learnedCount, pushCount, wordCount, studyModel, vos);

            studyModel = "单词图鉴";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = capacityListenMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            int pictureWordCount = vocabularyMapper.countByCourseId(courseId, 1);
            this.packageMemoryVo(learnedCount, pushCount, pictureWordCount, studyModel, vos);

            // 获取慧听写模块本课程已学单词量和达到黄金记忆点的待复习单词量
            studyModel = "慧听写";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = capacityListenMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, wordCount, studyModel, vos);

            // 获取慧默写模块本课程已学单词量和达到黄金记忆点的待复习单词量
            studyModel = "慧默写";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = simpleCapacityWriteMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, wordCount, studyModel, vos);

        } else {
            // 当前课程下例句总量
            int sentenceCount = simpleSentenceMapper.countByCourseId(courseId);
            // 获取例句听力模块本课程已学例句量和达到黄金记忆点的待复习例句量
            studyModel = "例句听力";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = simpleSentenceListenMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, sentenceCount, studyModel, vos);

            // 获取例句翻译模块本课程已学例句量和达到黄金记忆点的待复习例句量
            studyModel = "例句翻译";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = simpleSentenceTranslateMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, sentenceCount, studyModel, vos);

            // 获取例句翻译模块本课程已学例句量和达到黄金记忆点的待复习例句量
            studyModel = "例句默写";
            learnedCount = learnMapper.countByCourseId(studentId, courseId, studyModel);
            pushCount = simpleSentenceWriteMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, sentenceCount, studyModel, vos);
        }
        return ServerResponse.createBySuccess(vos);
    }

    private void packageMemoryVo(int learnedCount, int pushCount, int wordCount, String studyModel, List<CoursePlanVo> vos) {
        CoursePlanVo vo = new CoursePlanVo();
        vo.setLearned(learnedCount);
        vo.setNeedReview(pushCount);
        vo.setPlan(BigDecimalUtil.div(learnedCount * 1.0, wordCount, 2));
        vo.setStudyModel(studyModel);
        vo.setTotal(wordCount);
        vos.add(vo);
    }

    @Override
    public ServerResponse<Object> taskCourse(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // 响应数据结果集
        Map result = new HashMap();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        String time = SimpleDateUtil.DateTime();

        int fxlA = 0;
        int fxlB = 0;
        int fxlC = 0;
        int fxlD = 0;
        int fxlE = 0;
        int fxlF = 0;
        int fxlG = 0;
        int fxlH = 0;
        int fxlI = 0;

        // 单词辨音; 词组辨音; 快速单词; 快速词组; 词汇考点; 快速句型; 语法辨析; 单词默写; 词组默写
        for (int a = 1; a <= 9; a++) {
            // 1.该模型所有需要复习的课程id
            List<Integer> course_ids0 = simpleSimpleCapacityMapper.getReviewCourseIdAllByModel(studentId, a, time);


            for (Integer course_id : course_ids0) {
                // 2.返回:课程id  课程名course_name  模块名classify  复习量fxl
                List<Map<String, Object>> map = simpleSimpleCapacityMapper.selectStatusBigTenNine(studentId, course_id, a, time);
                for (Map<String, Object> map2 : map) {
                    map2.put("state", Integer.parseInt(map2.get("fxl").toString()) >= 10);
                    if (map2.get("course_name") != null && map2.get("course_name").toString().contains("冲刺版")) {
                        String[] split = map2.get("course_name").toString().split("-");
                        map2.put("course_name", split[0] + "-" + split[2]);
                    }
                    list.add(map2);
                    // 一键复习量
                    if (a == 1) {
                        fxlA += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 2) {
                        fxlB += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 3) {
                        fxlC += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 4) {
                        fxlD += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 5) {
                        fxlE += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 6) {
                        fxlF += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 7) {
                        fxlG += Integer.parseInt(map2.get("fxl").toString());
                    } else if (a == 8) {
                        fxlH += Integer.parseInt(map2.get("fxl").toString());
                    } else {
                        fxlI += Integer.parseInt(map2.get("fxl").toString());
                    }
                }
            }
        }

        // 一键复习量
        result.put("list", list);
        result.put("fxlA", fxlA);
        result.put("fxlB", fxlB);
        result.put("fxlC", fxlC);
        result.put("fxlD", fxlD);
        result.put("fxlE", fxlE);
        result.put("fxlF", fxlF);
        result.put("fxlG", fxlG);
        result.put("fxlH", fxlH);
        result.put("fxlI", fxlI);

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 一键复习每个模块数量
     */
    @Override
    public ServerResponse<Object> buildReview(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long student_id = student.getId();

        Map<String, Object> result = new HashMap<String, Object>();

        Integer capacityMemory = simpleCapacityMemoryMapper.countByPushByCourseid(student_id, SimpleDateUtil.DateTime());
        Integer capacityListen = capacityListenMapper.countByPushByCourseid(student_id, SimpleDateUtil.DateTime());
        Integer capacityWrite = simpleCapacityWriteMapper.countByPushByCourseid(student_id, SimpleDateUtil.DateTime());
        Integer sentenceListen = simpleSentenceListenMapper.countByPushByCourseid(student_id, SimpleDateUtil.DateTime());
        Integer sentenceTranslate = simpleSentenceTranslateMapper.countByPushByCourseid(student_id, SimpleDateUtil.DateTime());
        Integer sentenceWrite = simpleSentenceWriteMapper.countByPushByCourseid(student_id, SimpleDateUtil.DateTime());
        result.put("capacityMemory", capacityMemory);
        result.put("capacityListen", capacityListen);
        result.put("capacityWrite", capacityWrite);
        result.put("sentenceListen", sentenceListen);
        result.put("sentenceTranslate", sentenceTranslate);
        result.put("sentenceWrite", sentenceWrite);

        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> myCourse(HttpSession session, Integer ifSort) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        List<Map<String, String>> result = new ArrayList<>();

        List<StudentCourse> courses = learnMapper.getSimpleCourseId(studentId);
        for (StudentCourse studentCourse : courses) {
            Map<String, String> map = new HashMap<>(16);
            map.put("course_id", studentCourse.getCourseId().toString());
            if (StringUtils.isNotEmpty(studentCourse.getCourseName()) && studentCourse.getCourseName().contains("冲刺版")) {
                String[] split = studentCourse.getCourseName().split("-");
                map.put("course_name", split[0] + "-" + split[2]);
            } else {
                map.put("course_name", studentCourse.getCourseName());
            }

            // 通过课程名查找learn表中最大的学习时间
            String learnTime = learnMapper.selectBylLearn_time(studentCourse.getCourseId(), studentId);
            if (StringUtils.isNotEmpty(learnTime)) {
                map.put("learn_time", SimpleCalculateTimeUtil.CalculateTime(learnTime));
            } else {
                continue;
            }

            // sort用于时间排序
            learnTime = learnTime.replaceAll("[\\pP\\pS\\pZ]", "");
            map.put("sort", learnTime);
            result.add(map);
        }

        // 排序-时间从大到小
        if (ifSort == null || ifSort == 1) {
            result.sort((b, a) -> (Long.valueOf(a.get("sort")).compareTo(Long.valueOf(b.get("sort")))));
        } else {
            result.sort(Comparator.comparing(a -> Long.valueOf(a.get("sort"))));
        }

        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> allUnit(Integer courseId) {
        List<Map<String, Object>> unit = unitMapper.allUnit(courseId);
        return ServerResponse.createBySuccess(unit);
    }

    /**
     * 保存学生所选课本
     */
    @Override
    public ServerResponse<Object> postStudentByCourse(Integer courseId, Integer model, HttpSession session) {
        long id = StudentIdBySession(session);
        // 根据unitId获取
        Map<String, Object> map = simpleCourseMapper.postStudentByCourse(courseId);

        Student student = new Student();
        Learn record = new Learn();
        if (model == 1) {
            // 保存到学习信息中/ 单词
            student.setCourseId((long) courseId);
            student.setUnitId((long) map.get("id"));
            student.setCourseName(map.get("course_name").toString());
            //student.setUnitName(map.get("unit_name").toString());

        } else if (model == 2) {
            // 保存到学习信息中/ 例句
            student.setSentenceCourseId((long) courseId);
            student.setSentenceUnitId((int) map.get("id"));
            student.setSentenceCourseName(map.get("course_name").toString());
            //student.setSentenceUnitName(map.get("unit_name").toString());
        }
        student.setId(id);
        simpleStudentMapper.updateByPrimaryKeySelective(student);

        // 保存到我的课程
        // 1.查询我的课程中是否存在该课程id-学生id
        Integer scId = simpleStudentCourseMapper.selectCourseisExist(courseId, id);
        if (scId == null) {
            // 2.根据课程id查询课程名
            String courseName = simpleCourseMapper.selectByCourseName(courseId.toString());
            // 3.保存
            StudentCourse sc = new StudentCourse();
            sc.setCourseId(Long.valueOf(courseId));
            sc.setCourseName(courseName);
            sc.setStudentId(id);
            sc.setUpdateTime(SimpleDateUtil.DateTime());
            sc.setType(model);
            simpleStudentCourseMapper.insertSelective(sc);
        }

        return ServerResponse.createBySuccess();

    }

    /**
     * 从session中获取学生id(本类方法)
     *
     * @param session
     * @return
     */
    private long StudentIdBySession(HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        return student.getId();
    }

    /**
     * 把learn表中的课程id, 单元id保存到student表中
     */
    @Override
    public ServerResponse<Object> clickLearn(Integer courseId, int model, HttpSession session) {
        // 学生id
        long studentId = StudentIdBySession(session);

        // 通过课程id,学生id查询learn中正在学的单元id
        Integer unitId = learnMapper.selectMaxUnitId(studentId, courseId);

        if (unitId == null) {
            // 去课程中取出最小单元
            unitId = unitMapper.getMinUnit(courseId);
        }

        // 通过单元id和课程id查询出课程名和单元名  course_name, unit_name
        Map<String, String> map = unitMapper.getCourseNameAndUnitName(courseId, unitId);

        Student student = new Student();
        // 保存到学习信息中
        if (model == 1) {
            // 单词
            student.setCourseId((long) courseId);
            student.setCourseName(map.get("course_name"));
            student.setUnitId((long) unitId);
            student.setUnitName(map.get("unit_name"));
        } else if (model == 2) {
            // 例句
            student.setSentenceCourseId((long) courseId);
            student.setSentenceUnitId(unitId);
            student.setSentenceCourseName(map.get("course_name"));
            student.setSentenceUnitName(map.get("unit_name"));
        }
        student.setId(studentId);
        simpleStudentMapper.updateByPrimaryKeySelective(student);
        student = simpleStudentMapper.selectById(student.getId());
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getAllUnit(Long courseId, Boolean showAll) {
        List<Map<String, Object>> unitsInfo = simpleCourseMapper.getAllUnitInfos(courseId);
        if (showAll && unitsInfo.size() > 0) {
            int totalWordCount = 0;
            Map<String, Object> map = new HashMap<>(16);
            for (Map<String, Object> stringObjectMap : unitsInfo) {
                totalWordCount += Integer.valueOf(stringObjectMap.get("wordCount").toString());
                map.put("unitName", "全部单元");
                map.put("wordCount", totalWordCount);
                map.put("unitId", 0);
            }
            unitsInfo.add(0, map);
        }
        return ServerResponse.createBySuccess(unitsInfo);
    }

    @Override
    public ServerResponse<Object> postUnit(HttpSession session, int unitId, int model) {
        // 学生id
        long studentId = StudentIdBySession(session);
        // 单词模块
        if (model == 1) {
            simpleStudentMapper.updateUnitId(studentId, unitId);
            // 例句模块
            return ServerResponse.createBySuccess();
        } else {
            simpleStudentMapper.updatesentenceUnitId(studentId, unitId);
            return ServerResponse.createBySuccess();
        }
    }

    /**
     * 学习页的课程列表 - 精简版
     * 展示的课程和分配的课程和模块相关
     * 默认正在学习的课程state:true
     *
     * @return
     */
    @Override
    public ServerResponse<Object> getSimpleCourseAll(long studentId, String typeStr, int type) {
        // 1.获取当前学生当前模块关联的所有课程, 返回id,version
        List<Map> courseList = redisOpt.getCourseListInType(studentId, typeStr);

        // 2.获取选择模块正在学习的课程id
        Long courseId = simpleSimpleStudentUnitMapper.getCourseIdByTypeToStudent(studentId, type);

        int a = 0;

        // 3.设置默认状态
        for (Map course : courseList) {
            if (course.get("id").equals(courseId)) {
                // 正在学习的课程
                course.put("state", true);
                a = 1;
            } else {
                course.put("state", false);
            }
        }

        // 如果没有true的数据设置第一个为true
        if (a == 0 && courseList.size() > 0) {
            courseList.get(0).put("state", true);
        }

        return ServerResponse.createBySuccess(courseList);
    }

    /**
     * 学习页的单元列表 - 精简版
     * 根据课程展示的单元
     * 默认正在学习的单元state:true ->默认正在学习的单元
     * 未开启的单元yesState:false ->不可点击
     * 已经学完的单元 anew:true ->点击单元重新学习
     *
     * @return
     */
    @Override
    public ServerResponse<Object> getSimpleUnitAll(HttpSession session, long courseId, int type, String typeStr) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();
        // 1.根据课程id获取所有单元(id, 单元名, 开启状态0未开启 1开启),	 返回 id, unit_name, word_status
        List<Map> unitList = simpleStudentUnitMapper.getSimpleUnitByStudentIdByCourseId(studentId, courseId);

        // 获取已经开启的单元id
        Map<Long, Map<Long, Object>> map = simpleStudentUnitMapper.getOpenUnitId(studentId, courseId);

        // 获取已经做了单元闯关测试的单元id
        Map<Long, Map<Long, Object>> unitTest = simpleTestRecordMapper.getUnitTestByCourseId(courseId, studentId);

        // 2.查询学生当前模块正在学习的单元id
        Long unitId = simpleSimpleStudentUnitMapper.getUnitIdByTypeToStudent(studentId, type);

        // 获取课程下学生每个单元已学单词数量
        Map<Long, Map<Long, Object>> learnWordSum = simpleStudentMapper.learnUnitsWordSum(studentId, courseId);

        // 获取课程下每个单元单词总量
        Map<Long, Map<Long, Object>> unitWordSum = redisOpt.getWordCountWithUnitInCourse(courseId);

        boolean state = true;
        try {
            for (Map unit : unitList) {

                if (unit.get("id").equals(unitId)) {
                    // 正在学习的单元
                    unit.put("state", true);
                    unit.put("yesState", true);
                    unit.put("anew", false);
                    state = false;
                    // 判断单元是否能够重新学习
                    judgeAnew(type, map, unitTest, learnWordSum, unitWordSum, unit);
                } else if (unit.get("word_status").equals(0)) {
                    // 未开启的单元
                    unit.put("state", false);
                    unit.put("yesState", false);
                    unit.put("anew", false);
                } else {
                    // 已学过的单元
                    unit.put("state", false);
                    unit.put("yesState", true);
                    unit.put("anew", false);
                    // 判断单元是否能够重新学习
                    judgeAnew(type, map, unitTest, learnWordSum, unitWordSum, unit);
                }
            }
        } catch (Exception e) {
            log.error("获取单元课程下单元信息出错：学生:[{}]->[{}], errorMsg:[{}]", studentId, student.getStudentName(), e);
        }

        if (state && unitList.size() > 0) {
            Map<String, Object> m = new HashMap<>(16);
            m.put("id", 0);
            m.put("state", true);
            m.put("unit_name", "选择章节");
            unitList.add(0, m);
        }

        return ServerResponse.createBySuccess(unitList);
    }

    /**
     * 判断当前单元是否可以重新学习
     *
     * @param type         1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @param map          已经开启的单元id；单元与其开启状态的对应关系
     * @param unitTest     已经做了单元闯关测试的单元id
     * @param learnWordSum 课程下学生每个单元已学单词数量
     * @param unitWordSum  课程下每个单元单词总量
     * @param unit         当前单元信息 b.id, b.unit_name, a.word_status
     */
    private void judgeAnew(int type, Map<Long, Map<Long, Object>> map, Map<Long, Map<Long, Object>> unitTest,
                           Map<Long, Map<Long, Object>> learnWordSum, Map<Long, Map<Long, Object>> unitWordSum,
                           Map<String, Object> unit) {
        long atUnitId = (long) unit.get("id");
        if (map.containsKey(atUnitId)) {
            // 已开启的单元
            if (map.get(atUnitId).get("isAll").toString().equals("1")) {
                // 除了没有测试的模块,单元做过单元闯关测试
                if (unitTest.containsKey(atUnitId)) {
                    // anew:true的时候代表可以进行再学一遍;
                    unit.put("anew", true);
                }
                // 5:词汇考点; 7:语法辨析;
                if (type == 5 || type == 7) {
                    // 单元已学 == 单元全部单词
                    if (learnWordSum != null && unitWordSum != null && unitWordSum.containsKey(atUnitId + "") && learnWordSum.containsKey(atUnitId)
                            && Long.valueOf(unitWordSum.get(atUnitId + "").get("sum").toString()) <= Long.valueOf(learnWordSum.get(atUnitId).get("sum").toString())) {
                        unit.put("anew", true);
                    }
                }
            }
        }
    }

    @Override
    public ServerResponse<Object> getSimpleTestWordCourseAll(long studentId) {
        // 1.获取当前学生当前模块关联的所有单词课程, 返回id,version
        List<Map> courseList = simpleCourseMapper.getSimpleCourseByStudentIdByType(studentId, "单词");
        return ServerResponse.createBySuccess(courseList);
    }

    /**
     * 获取学生关联的所有课程名
     */
    @Override
    public ServerResponse<Object> getStudentCourseAll(long studentId) {
        // 获取学生关联的所有课程名
        List<String> courseAll = simpleCourseMapper.getStudentCourseAllByStudentId(studentId);
        if (courseAll.size() > 0) {
            List<String> resultCourse = new ArrayList<>(courseAll.size());
            courseAll.forEach(course -> {
                if (course != null && course.contains("冲刺版")) {
                    String[] split = course.split("-");
                    resultCourse.add(split[0] + "-" + split[2]);
                } else {
                    resultCourse.add(course);
                }
            });
            return ServerResponse.createBySuccess(resultCourse);
        }
        return ServerResponse.createBySuccess(courseAll);
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getAllCourse(HttpSession session, Integer type, Boolean isAll) {
        Student student = super.getStudent(session);
        String typeStr = simpleCommonMethod.getTestType(type);
        List<Map<String, Object>> courseMap = simpleCourseMapper.selectAllCourseByStuIdAndType(student.getId(), typeStr);

        if (courseMap.size() > 0) {
            courseMap.forEach(map -> {
                if (map.get("version") != null && map.get("version").toString().contains("冲刺版")) {
                    String[] split = map.get("version").toString().split("-");
                    map.put("version", split[0] + "-" + split[2]);
                }
            });
        }

        if (isAll) {
            Map<String, Object> map = new HashMap<>(16);
            // 有当前模块的课程
            if (courseMap.size() > 0) {
                map.put("id", 0);
            } else { // 无当前模块的课程
                map.put("id", "null");
            }
            map.put("version", "全部");
            courseMap.add(0, map);
        }
        return ServerResponse.createBySuccess(courseMap);
    }

    @Override
    public ServerResponse<Object> postCourseIdAndUnitId(Long studentId, Long courseId, Long unitId, int model) {
        simpleSimpleStudentUnitMapper.updateCourseIdAndUnitIdByCourseIdByModel(courseId, unitId, studentId, model);
        return ServerResponse.createBySuccess();
    }


}
