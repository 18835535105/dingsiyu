package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.CalculateTimeUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.CourseService;
import com.zhidejiaoyu.student.vo.CoursePlanVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.ResponseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CourseServiceImpl extends BaseServiceImpl<CourseMapper, Course> implements CourseService {

    /**
     * 注入课程mapper
     */
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private CapacityMemoryMapper capacityMemoryMapper;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Autowired
    private CapacityWriteMapper capacityWriteMapper;

    @Autowired
    private SentenceListenMapper sentenceListenMapper;

    @Autowired
    private SentenceWriteMapper sentenceWriteMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private SentenceTranslateMapper sentenceTranslateMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private UnitSentenceMapper unitSentenceMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private CapacityReviewMapper capacityReviewMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Override
    public List chooseGrade(HttpSession session) {

        long id = StudentIdBySession(session);

        // 去student_unit查询分配的所有年级
        List<String> list = courseMapper.chooseGrade(id);

        Course cou = new Course();
        cou.setId(Long.valueOf(id));

        List<Object> liResult = new ArrayList<Object>();

        // 根据年级, 学生id查询标签
        for (String grade : list) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("grade", grade);

            cou.setGrade(grade);
            List<Map<String, Object>> listMap = courseMapper.chooseGradeToLabel(cou);

            result.put("listLabel", listMap);
            result.put("msg", false);
            liResult.add(result);
        }
        return liResult;
    }

    @Override
    public List<Course> retGrade() {
        return courseMapper.retGrade();
    }

    @Override
    public List<Course> retVersion(String grade) {
        return courseMapper.retVersion(grade);
    }

    @Override
    public List<Course> retLabel(String grade, String version) {
        return courseMapper.retLabel(grade, version);
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
            List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, courseId,1);
            List<Map<String,Object>> returnCourse = null;
            if(studentStudyPlans.size() > 0) {
                returnCourse = new ArrayList<>();
                for (StudentStudyPlan studentStudyPlan : studentStudyPlans) {
                    List<Map<String, Object>> maps = unitVocabularyMapper.selUnitIdAndNameByCourseIdsAndStartUnitIdAndEndUnitId(courseId,
                            studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
                    for (int j = 0; j < maps.size(); j++) {
                        boolean contains = returnCourse.contains(maps.get(j));
                        if (contains) {
                            maps.remove(maps.get(j));
                        }
                    }
                    returnCourse.addAll(maps);
                }
            }
            if (returnCourse == null) {
                log.error("学生[{}]->[{}]没有课程[{}]的学习计划！", studentId, student.getStudentName(), courseId);
                return null;
            }
            List<Long> unitIds = new ArrayList<>(returnCourse.size());
            returnCourse.parallelStream().forEach(map -> unitIds.add((Long)map.get("id")));
            // 获取当前课程的单词总量
            int wordCount = vocabularyMapper.countByCourseId(courseId, 2);

            // 获取慧记忆模块本课程已学单词量和达到黄金记忆点的待复习单词量
            studyModel = "慧记忆";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = capacityMemoryMapper.countNeedReviewByStudentIdAndCourseId(studentId, courseId);
            this.packageMemoryVo(learnedCount, pushCount, wordCount, studyModel, vos);

            studyModel = "单词图鉴";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = capacityListenMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            int pictureWordCount = vocabularyMapper.countByCourseId(courseId, 1);
            this.packageMemoryVo(learnedCount, pushCount, pictureWordCount, studyModel, vos);

            // 获取慧听写模块本课程已学单词量和达到黄金记忆点的待复习单词量
            studyModel = "慧听写";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = capacityListenMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, wordCount, studyModel, vos);

            // 获取慧默写模块本课程已学单词量和达到黄金记忆点的待复习单词量
            studyModel = "慧默写";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = capacityWriteMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, wordCount, studyModel, vos);

        } else if(type==2){
            List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, courseId,2);
            List<Map<String,Object>> returnCourse=new ArrayList<>();
            if(studentStudyPlans.size()>0) {
                for (StudentStudyPlan studentStudyPlan : studentStudyPlans) {
                    List<Map<String, Object>> maps = unitSentenceMapper.selUnitIdAndNameByCourseIdsAndStartUnitIdAndEndUnitId(courseId,
                            studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId());
                    for (int j = 0; j < maps.size(); j++) {
                        boolean contains = returnCourse.contains(maps.get(j));
                        if (contains) {
                            maps.remove(maps.get(j));
                        }
                    }
                    returnCourse.addAll(maps);
                }
            }
            List<Long> unitIds = new ArrayList<>(returnCourse.size());
            returnCourse.parallelStream().forEach(map -> unitIds.add((Long)map.get("id")));
            // 当前课程下例句总量
            if(unitIds.size()==0){
                return ServerResponse.createByErrorMessage("无数据");
            }
            int sentenceCount = sentenceMapper.countByCourseId(unitIds);
            // 获取例句翻译模块本课程已学例句量和达到黄金记忆点的待复习例句量
            studyModel = "例句翻译";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = sentenceTranslateMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, sentenceCount, studyModel, vos);

            // 获取例句听力模块本课程已学例句量和达到黄金记忆点的待复习例句量
            studyModel = "例句听力";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = sentenceListenMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, sentenceCount, studyModel, vos);

            // 获取例句翻译模块本课程已学例句量和达到黄金记忆点的待复习例句量
            studyModel = "例句默写";
            learnedCount = learnMapper.countByCourseId(studentId, unitIds, studyModel);
            pushCount = sentenceWriteMapper.countNeedReviewByStudentIdAndCourseId(courseId, studentId);
            this.packageMemoryVo(learnedCount, pushCount, sentenceCount, studyModel, vos);
        }else{
            //获取但前单元课程包含单元数量
            List<Map<String, Object>> maps =new ArrayList<>();
            List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, courseId,3);
            if (studentStudyPlans.size() > 0) {
                for (StudentStudyPlan studentStudyPlan : studentStudyPlans) {
                    List<Map<String, Object>> returnMap = unitMapper.selectByStudentIdAndCourseIdAndStartUnitIdAndEndUnitId(courseId,
                            studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId(), studentId);
                    for (int j = 0; j < returnMap.size(); j++) {
                        boolean contains = maps.contains(returnMap.get(j));
                        if (contains) {
                            returnMap.remove(returnMap.get(j));
                        }
                    }
                    maps.addAll(returnMap);
                }
            }
            List<Long> unitIds = new ArrayList<>(maps.size());
            maps.parallelStream().forEach(map -> unitIds.add((Long)map.get("id")));
            if(unitIds.size()==0){
                return ServerResponse.createByErrorMessage("无数据");
            }
            //当前课程下单元数量
            int size = maps.size();
            studyModel="课文试听";
            Integer countAudition = 0;
            Integer teksGoodVoice=0;
            Integer teksTest=0;
            if(unitIds.size()>0){
                countAudition=learnMapper.selAllTeksLearn(student.getId(), unitIds, studyModel);
            }
            this.packageMemoryVo(countAudition, 0, size, studyModel, vos);
            studyModel="课文好声音";
            if(unitIds.size()>0){
                teksGoodVoice=learnMapper.selAllTeksLearn(student.getId(), unitIds,studyModel);
            }
            this.packageMemoryVo(teksGoodVoice, 0, size, "课文跟读", vos);
            studyModel="课文训练";
            if(unitIds.size()>0){
                teksTest=learnMapper.selAllTeksLearn(student.getId(), unitIds,"课文默写测试");
            }
            this.packageMemoryVo(teksTest, 0, size, studyModel, vos);
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
    public ServerResponse<Object> taskCourse(HttpSession session, Integer model) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        Map result = new HashMap();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        // 当前时间
        String time = DateUtil.DateTime();

        if (model == 1) {

            // 慧记忆
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer capacityMemory = 0;
            // 查出慧记忆追踪中存在的课程id 条件是学生id
            List<Integer> course_ids = capacityMemoryMapper.selectStatusBig(studentId, 1);
            // 遍历这些课程-查询课程下是否有20个需要复习的单词
            for (Integer course_id : course_ids) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 1, time);
                for (Map<String, Object> map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 20) {
                        map2.put("state", true);
                        capacityMemory += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    //capacityMemory += fxl;
                }
            }

            // 单词图鉴
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer capacityPicture = 0;
            // 查出慧记忆追踪中存在的课程id 条件是学生id
            List<Integer> course_ids0 = capacityMemoryMapper.selectStatusBig(studentId, 0);
            // 遍历这些课程-查询课程下是否有20个需要复习的单词 - 改为所有的
            for (Integer course_id : course_ids0) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 0, time);
                for (Map map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 10) {
                        map2.put("state", true);
                        capacityPicture += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    //capacityPicture += fxl;
                }
            }

            // 慧听写
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer capacityListen = 0;
            List<Integer> course_ids2 = capacityMemoryMapper.selectStatusBig(studentId, 2);
            for (Integer course_id : course_ids2) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 2, time);
                for (Map<String, Object> map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 10) {
                        map2.put("state", true);
                        capacityListen += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    //capacityListen += fxl;
                }
            }
            // 慧默写
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer capacityWrite = 0;
            List<Integer> course_ids3 = capacityMemoryMapper.selectStatusBig(studentId, 3);
            for (Integer course_id : course_ids3) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 3, time);
                for (Map<String, Object> map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 10) {
                        map2.put("state", true);
                        capacityWrite += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    // capacityWrite += fxl;
                }
            }
            // 一键复习复习量
            result.put("capacityPicture", capacityPicture);
            result.put("capacityMemory", capacityMemory);
            result.put("capacityListen", capacityListen);
            result.put("capacityWrite", capacityWrite);
        } else if (model == 2) {
            // 例句听力
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer sentenceListen = 0;
            List<Integer> course_ids4 = capacityMemoryMapper.selectStatusBig(studentId, 4);
            for (Integer course_id : course_ids4) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 4, time);
                for (Map<String, Object> map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 10) {
                        map2.put("state", true);
                        sentenceListen += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    //sentenceListen += fxl;
                }
            }
            // 例句翻译
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer sentenceTranslate = 0;
            List<Integer> course_ids5 = capacityMemoryMapper.selectStatusBig(studentId, 5);
            for (Integer course_id : course_ids5) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 5, time);
                for (Map<String, Object> map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 10) {
                        map2.put("state", true);
                        sentenceTranslate += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    // sentenceTranslate += fxl;
                }
            }
            // 例句默写
            // 1.查询记忆追踪1中的课程id 根据学生id,模块
            Integer sentenceWrite = 0;
            List<Integer> course_ids6 = capacityMemoryMapper.selectStatusBig(studentId, 6);
            for (Integer course_id : course_ids6) {
                List<Map<String, Object>> map = capacityMemoryMapper.selectStatusBigTenNine(studentId, course_id, 6, time);
                for (Map<String, Object> map2 : map) {
                    int fxl = Integer.parseInt(map2.get("fxl").toString());
                    if (fxl >= 10) {
                        map2.put("state", true);
                        sentenceWrite += fxl;
                    } else {
                        map2.put("state", false);
                    }
                    list.add(map2);
                    // sentenceWrite += fxl;
                }
            }
            // 一键复习每个模块的总复习量
            result.put("sentenceListen", sentenceListen);
            result.put("sentenceTranslate", sentenceTranslate);
            result.put("sentenceWrite", sentenceWrite);
        }

        result.put("list", list);

        // 2.把记忆追踪中需要复习数量大于10 / 20 的课程id查出来
        //select a.id,b.course_name, (SELECT count(id) AS fxl from capacity_memory  where student_id = 1 and course_id = 1 and push < '2018-02-03') AS fxl from capacity_memory a INNER JOIN course b ON a.course_id = b.id AND a.student_id = 1 AND a.course_id = 1 AND push < '2018-02-03'
        return ServerResponse.createBySuccess(result);
    }

    /**
     * 一键复习每个模块数量
     */
    @Override
    public ServerResponse<Object> buildReview(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        Map<String, Object> result = new HashMap<>(16);
        String now = DateUtil.DateTime();

        int count;
        for (int i = 0; i < 6; i++) {
            count = capacityReviewMapper.countByPushByCourseId(studentId, now, i);
        }

        Integer capacityMemory = capacityMemoryMapper.countByPushByCourseid(studentId, now);
        Integer capacityListen = capacityListenMapper.countByPushByCourseid(studentId, now);
        Integer capacityWrite = capacityWriteMapper.countByPushByCourseid(studentId, now);
        Integer sentenceListen = sentenceListenMapper.countByPushByCourseid(studentId, now);
        Integer sentenceTranslate = sentenceTranslateMapper.countByPushByCourseid(studentId, now);
        Integer sentenceWrite = sentenceWriteMapper.countByPushByCourseid(studentId, now);
        result.put("capacityMemory", capacityMemory);
        result.put("capacityListen", capacityListen);
        result.put("capacityWrite", capacityWrite);
        result.put("sentenceListen", sentenceListen);
        result.put("sentenceTranslate", sentenceTranslate);
        result.put("sentenceWrite", sentenceWrite);

        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> myCourse(HttpSession session, Integer model, Integer ifSort) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long student_id = student.getId();

        List<Map<String, String>> result = new ArrayList<>();

        // 查询学过的课程id, 课程名
        List<StudentCourse> courseId = studentCourseMapper.selectCourse(student_id, model);
        for (StudentCourse StudentCourse : courseId) {
            Map<String, String> map = new HashMap<>();
            map.put("course_id", StudentCourse.getCourseId() + "");
            map.put("course_name", StudentCourse.getCourseName() + "");
            // 通过课程名查找learn表中最大的学习时间
            String learn_time = learnMapper.selectBylLearn_time(StudentCourse.getCourseId(), student_id);
            if (learn_time == null) {
                learn_time = StudentCourse.getUpdateTime();
            }
            map.put("learn_time", CalculateTimeUtil.CalculateTime(learn_time) + "");

            // sort用于时间排序
            learn_time = learn_time.replaceAll("[\\pP\\pS\\pZ]", "");
            map.put("sort", learn_time + "");
            result.add(map);
        }

        // 排序-时间从大到小
        if (ifSort == null || ifSort == 1) {
            result.sort((b, a) -> (Long.valueOf(a.get("sort").toString()).compareTo(Long.valueOf(b.get("sort").toString()))));
        } else {
            result.sort((a, b) -> (Long.valueOf(a.get("sort").toString()).compareTo(Long.valueOf(b.get("sort").toString()))));
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
        Map<String, Object> map = courseMapper.postStudentByCourse(courseId);

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
            student.setSentenceUnitId(Integer.parseInt(map.get("id").toString()));
            student.setSentenceCourseName(map.get("course_name").toString());
        }
        student.setId(id);
        studentMapper.updateByPrimaryKeySelective(student);

        // 保存到我的课程
        // 1.查询我的课程中是否存在该课程id-学生id
        Integer scId = studentCourseMapper.selectCourseisExist(courseId, id);
        if (scId == null) {
            // 2.根据课程id查询课程名
            String courseName = courseMapper.selectByCourseName(courseId.toString());
            // 3.保存
            StudentCourse sc = new StudentCourse();
            sc.setCourseId(Long.valueOf(courseId));
            sc.setCourseName(courseName);
            sc.setStudentId(id);
            sc.setUpdateTime(DateUtil.DateTime());
            sc.setType(model);
            studentCourseMapper.insertSelective(sc);
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
        studentMapper.updateByPrimaryKeySelective(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess();
    }

    /**
     * 点击单元 - 单元闯关展示(单词/例句)
     */
    @Override
    public ServerResponse<Object> breakthrough(HttpSession session, Integer model) {
        // 学生id
        long studentId = StudentIdBySession(session);

        List<Map<String, Object>> result = new ArrayList<>();

        // 1.当前单词/例句所学课程id,单元id
        Map<String, Object> studentInfo = studentMapper.getCourseIdAndUnitId(studentId);
        // 单词 所学课程id
        Long courseIdw = (Long) studentInfo.get("course_id");
        // 单词 所学单元id
        Long unitIdw = (Long) studentInfo.get("unit_id");
        // 例句 所学课程id
        Long courseIds = (Long) studentInfo.get("sentence_course_id");
        // 例句 所学单元id
        Integer unitIds = (Integer) studentInfo.get("sentence_unit_id");

        // 单词
        if (model == 1) {
            //int now = 0; // 当前所学单元在listUnit中的索引所在位置
            int now = studentUnitMapper.getCountUnit(courseIdw, studentId) - 1;
            // 2.遍历当前所学课程下所有单元 ->单元id,单元名
            List<Map<String, Object>> listUnit = unitMapper.selectByUnitIdAndUnitName(courseIdw.intValue());

            for (int i = 0; i < listUnit.size(); i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                // 循环的单元id
                Long unitId = (Long) listUnit.get(i).get("id");

                // 单元名
                map.put("unitName", listUnit.get(i).get("unit_name"));
                // 单元id
                map.put("unitId", unitId);

                // 已做过的单元闯关测试
                if (i < now) {
                    Integer hjy = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 1);
                    Integer htx = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 2);
                    Integer hmx = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 3);
                    Integer dctj = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 0);
                    map.put("hjy", hjy + "分"); // 慧记忆分数
                    map.put("dctj", dctj + "分"); // 单词图鉴分数
                    map.put("htx", htx + "分"); // 慧听写分数
                    map.put("hmx", hmx + "分"); // 慧默写分数
                    map.put("state", 1); // 已测单元
                    result.add(map);

                    // 正在学的单元
                } else if (i == now) {
                    // 获取单元单词总数量
                    Integer count = unitVocabularyMapper.allCountWord(unitId);
                    // 获取单元闯关最高分数
                    Integer hjy = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 1);
                    Integer htx = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 2);
                    Integer hmx = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 3);
                    Integer dctj = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 0);
                    // 慧记忆
                    if (dctj != null) {
                        map.put("dctj", dctj + "分"); // 慧记忆
                    } else {
                        //-- 3.某学生某单元某模块单词 学了多少 ./
                        Integer countWord = learnMapper.selectNumberByStudentId(studentId, unitId.intValue(), 0);
                        if (countWord >= count && count != 0) {
                            map.put("dctj", "闯关"); // 闯关
                        } else {
                            map.put("dctj", "单词图鉴"); // 慧记忆
                            if (countWord > 0) {
                                map.put("dctj", countWord + "/" + count);// 学习进度
                            }
                        }
                    }
                    // 慧记忆
                    if (hjy != null) {
                        map.put("hjy", hjy + "分"); // 慧记忆
                    } else {
                        // 查询慧记忆模块单词是否学完
                        //Long countWord = learnMapper.countLearnWord(studentId, unitId, "慧记忆", 1);
                        //-- 3.某学生某单元某模块单词 学了多少 ./
                        Integer countWord = learnMapper.selectNumberByStudentId(studentId, unitId.intValue(), 1);
                        if (countWord >= count && count != 0) {
                            map.put("hjy", "闯关"); // 闯关
                        } else {
                            map.put("hjy", "慧记忆"); // 慧记忆
                            if (countWord > 0) {
                                map.put("hjy", countWord + "/" + count);// 学习进度
                            }
                        }
                    }

                    // 慧听写
                    if (htx != null) {
                        map.put("htx", htx + "分"); // 慧听写
                    } else {
                        // 查询慧听写模块单词是否学完
                        //Long countWord = learnMapper.countLearnWord(studentId, unitId, "慧听写", 1);
                        Integer countWord = learnMapper.selectNumberByStudentId(studentId, unitId.intValue(), 2);
                        if (countWord >= count && count != 0) {
                            map.put("htx", "闯关"); // 闯关
                        } else {
                            map.put("htx", "慧听写"); // 慧听写
                            if (countWord > 0) {
                                map.put("htx", countWord + "/" + count);// 学习进度
                            }
                        }
                    }

                    // 慧默写
                    if (hmx != null) {
                        map.put("hmx", hmx + "分"); // 慧默写
                    } else {
                        // 查询慧默写模块单词是否学完
                        //Long countWord = learnMapper.countLearnWord(studentId, unitId, "慧默写", 1);
                        Integer countWord = learnMapper.selectNumberByStudentId(studentId, unitId.intValue(), 3);
                        if (countWord >= count && count != 0) {
                            map.put("hmx", "闯关"); // 闯关
                        } else {
                            map.put("hmx", "慧默写"); // 慧默写
                            if (countWord > 0) {
                                map.put("hmx", countWord + "/" + count);// 学习进度
                            }
                        }
                    }
                    map.put("state", 2); // 正在学的单元
                    result.add(map);

                    // 未学的单元
                } else {
                    map.put("hjy", "慧记忆"); // 慧记忆
                    map.put("htx", "慧听写"); // 慧听写
                    map.put("hmx", "慧默写"); // 慧默写
                    map.put("dctj", "单词图鉴"); // 单词图鉴
                    //map.put("state", i == 0 ? 2 : 3); // 未测单元-上锁, 默认第一个单元解锁
                    map.put("state", 3); // 未测单元-上锁
                    result.add(map);
                }
            }

            // 例句
        } else if (model == 2) {
            //int now = 0; // 当前所学单元在listUnit中的索引所在位置
            int now = studentUnitMapper.getCountUnitSentenceStatus(courseIds, studentId) - 1;
            // 2.遍历当前所学课程下所有单元 ->单元id,单元名
            List<Map<String, Object>> listUnit = unitMapper.selectByUnitIdAndUnitName(courseIds.intValue());

            for (int i = 0; i < listUnit.size(); i++) {//listUnit.get(i).get("id"); listUnit.get(i).get("unit_name");
                Map<String, Object> map = new HashMap<String, Object>();
                // 循环的单元id
                long unitId = (long) listUnit.get(i).get("id");

                map.put("unitId", unitId);
                map.put("unitName", listUnit.get(i).get("unit_name"));

                // 已做过的单元闯关测试
                if (i < now) {
                    Integer ljtl = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 4);
                    Integer ljfy = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 5);
                    Integer ljmx = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 6);
                    map.put("ljtl", ljtl + "分");
                    map.put("ljfy", ljfy + "分");
                    map.put("ljmx", ljmx + "分");
                    map.put("state", 1);
                    result.add(map);

                    // 正在学的单元
                } else if (i == now) {
                    Integer ljtl = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 4);
                    Integer ljfy = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 5);
                    Integer ljmx = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, unitId, 6);

                    // 获取单元例句总数量
                    Integer count = unitSentenceMapper.countByUnitId(unitId);

                    // 例句听力
                    if (ljtl != null && ljtl.toString().length() > 0) {
                        map.put("ljtl", ljtl + "分");
                    } else {
                        // 查询例句听力模块单词是否学完
                        Long countWord = learnMapper.countLearnWord(studentId, unitId, "例句听力", 1);
                        if (countWord >= count && count != 0) {
                            map.put("ljtl", "闯关");
                        } else {
                            map.put("ljtl", "例句听力");
                        }
                    }

                    // 例句翻译
                    if (ljfy != null || ljfy.toString().length() > 0) {
                        map.put("ljfy", ljtl + "分");
                    } else {
                        // 查询慧听写模块单词是否学完
                        Long countWord = learnMapper.countLearnWord(studentId, unitId, "例句翻译", 1);
                        if (countWord >= count && count != 0) {
                            map.put("ljfy", "闯关");
                        } else {
                            map.put("ljfy", "例句翻译");
                        }
                    }

                    // 例句默写
                    if (ljmx != null || ljmx.toString().length() > 0) {
                        map.put("ljmx", ljtl + "分");
                    } else {
                        // 查询慧默写模块单词是否学完
                        Long countWord = learnMapper.countLearnWord(studentId, unitId, "例句默写", 1);
                        if (countWord >= count && count != 0) {
                            map.put("ljmx", "闯关");
                        } else {
                            map.put("ljmx", "例句默写");
                        }
                    }

                    map.put("state", 2); // 正在学的单元
                    result.add(map);

                    // 未学的单元
                } else {
                    map.put("ljtl", "例句听力");
                    map.put("ljfy", "例句翻译");
                    map.put("ljmx", "例句默写");
                    map.put("state", 3);
                    result.add(map);
                }
            }
        }

        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getAllUnit(Long courseId, Boolean showAll, Integer type) {
        List<Map<String, Object>> unitsInfo = courseMapper.getAllUnitInfos(courseId, type);
        if (unitsInfo.size() > 0 && showAll) {
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
            studentMapper.updateUnitId(studentId, unitId);
            // 例句模块
            return ServerResponse.createBySuccess();
        } else {
            studentMapper.updatesentenceUnitId(studentId, unitId);
            return ServerResponse.createBySuccess();
        }
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getAllCourses(HttpSession session, Integer type, Boolean flag) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        List<Map<String, Object>> courseInfo = courseMapper.getAllCourse(student, type);

        if (courseInfo.size() > 0) {
            int count = 0;
            Map<String, Object> map = new HashMap<>(16);
            for (Map<String, Object> stringObjectMap : courseInfo) {
                count += Integer.valueOf(stringObjectMap.get("count").toString());
            }
            if (flag) {
                map.put("courseName", "全部课程");
                map.put("count", count);
                map.put("courseId", 0);
                courseInfo.add(0, map);
            }
        }
        return ServerResponse.createBySuccess(courseInfo);
    }

    @Override
    public ServerResponse<PageInfo<Map<String, Object>>> getUnitPage(HttpSession session, Long courseId, Integer pageNum, Integer pageSize) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> map = unitMapper.selectUnitIdAndUnitNameByCourseIdAndStudentId(courseId, student.getId());
        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(map);
        return ServerResponse.createBySuccess(mapPageInfo);
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getAllCoursesInfo(HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        List<Map<String, Object>> courseMaps = studentUnitMapper.selectCourseInfo(studentId);

        Map<String, Object> map = new HashMap<>(16);
        map.put("courseName", "全部课程");
        map.put("id", 0);

        courseMaps.add(0, map);

        return ServerResponse.createBySuccess(courseMaps);
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getVersion(HttpSession session) {
        Student student = getStudent(session);
        List<Map<String, Object>> versionList = courseMapper.selectVersionByStudent(student);
        return ServerResponse.createBySuccess(versionList);
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getCourseByVersion(HttpSession session, String versionName) {
        Student student = getStudent(session);
        List<Map<String, Object>> courses = courseMapper.selectCourseByVersion(student, versionName);
        return ServerResponse.createBySuccess(courses);
    }

}
