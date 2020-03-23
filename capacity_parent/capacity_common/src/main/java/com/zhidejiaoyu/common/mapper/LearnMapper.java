package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface LearnMapper extends BaseMapper<Learn> {
    int countByExample(LearnExample example);

    List<Learn> selectByExample(LearnExample example);

    int updateByPrimaryKeySelective(Learn record);

    /**
     * 查询学生当前单元当前模块下已学习单词/例句的个数，即学习进度
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel
     * @return
     */
    Long countLearnWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                        @Param("studyModel") String studyModel);

    /**
     * 查询学生当前单元当前模块下已学习单词/例句的个数，即学习进度
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel
     * @param count      当前课程的学习遍数
     * @return
     */
    Long learnWordBySentence(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                             @Param("studyModel") String studyModel, @Param("count") Integer count);

    Long isCountLearnWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                          @Param("studyModel") String studyModel);

    /**
     * 查询学生当前单元当前模块下已学习单词/例句的个数，即学习进度 By Type
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel
     * @return
     */
    Long countLearnWordAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 查询学生当前课程当前模块下已学习单词/例句的个数，即学习进度
     *
     * @param studentId  学生id
     * @param courseId   课程id
     * @param studyModel
     * @param count      当前课程的学习遍数
     * @return
     */
    Long countLearnWordByCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("studyModel") String studyModel, @Param("count") Integer count);

    /**
     * 查询当前单词的学习记录数据
     *
     * @param studentId
     * @param learn
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @return
     */
    Learn selectLearn(@Param("studentId") Long studentId, @Param("learn") Learn learn,
                      @Param("studyModel") String studyModel, @Param("type") Integer type);

    /**
     * 根据学生id删除
     *
     * @param studentId
     */
    @Delete("delete from learn where student_id = #{studentId}")
    int deleteByStudentId(Long studentId);

    /**
     * 从末尾查询指定数量的单词id
     *
     * @param studentId
     * @param unitId
     * @param wordCount 需要查询的单词的个数
     * @return
     */
    List<Long> selectByCount(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                             @Param("wordCount") Integer wordCount);

    /**
     * 查询指定单词/句子、模块的学习记录
     *
     * @param studentId
     * @param unitId
     * @param wordId     单词id
     * @param sentenceId 例句id
     * @param studyModel
     * @return
     */
    List<Learn> selectLearnByIdAmdModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                        @Param("wordId") Long wordId, @Param("sentenceId") Long sentenceId, @Param("studyModel") String studyModel);


    /**
     * 查询当前课程下的单词/例句的学习次数
     *
     * @param studentId  当前学生id
     * @param courseId   当前课程id
     * @param unitId     当前单元id，如果id为0，根据课程查询；否则根据单元id查询
     * @param id         单词/例句id
     * @param studyModel 学习模块
     * @return 学习次数
     */
    Integer selectStudyCountByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("unitId") Long unitId,
                                       @Param("id") Long id, @Param("studyModel") String studyModel);

    /**
     * 查询当前单元下生词/生句的个数
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @return
     */
    Long countNotKnownWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                           @Param("studyModel") String studyModel);

    /**
     * 查询当前课程下生词/生句的个数
     *
     * @param studentId
     * @param courseId
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写
     * @param count      当前课程的学习遍数
     * @return
     */
    Long countNotKnownWordByCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                                   @Param("studyModel") String studyModel, @Param("count") Integer count);

    @Select("select id from learn where student_id = #{id} and study_model = '慧听写' LIMIT 0,1")
    Integer theFirstTime(@Param("id") Long id);

    /**
     * 将当前学生当前课程的学习记录中的单词和例句置为生词和生句
     *
     * @param studentId
     * @param courseId
     * @return
     */
    int updateByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 查询当前课程下指定模块的已学习单词/例句个数
     *
     * @param studentId
     * @param unitIds
     * @param studyModel 学习模块 （单词图鉴，慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写）
     * @return
     */
    int countByCourseId(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitIds, @Param("studyModel") String studyModel);

    /**
     * 查看学生当天学习的单元数
     *
     * @param studentId
     * @return
     */
    @Select("select count(study_model) from learn where student_id = #{studentId} and to_days(update_time) = to_days(now())")
    int countLearnModelByStuId(Long studentId);

    /**
     * 查询学生当日学习的单元ids
     *
     * @param studentId
     * @return
     */
    List<Long> selectLearnUnitsByStuId(@Param("studentId") Long studentId);

    /**
     * 获取当前单元下当日已学习的模块总数
     *
     * @param studentId
     * @param unitId
     * @return
     */
    int countLearnModelByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 获取当前模块下今日学习的单词或例句的个数
     *
     * @param studentId
     * @param unitId
     * @param studyModel 学习模块
     * @return
     */
    int countTodayLearnedByStudyModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 查询当日学生在慧记忆，慧听写，慧默写三个模块学习的熟词总数
     *
     * @return
     */
    int countTodayLearnedKnownWord();

    /**
     * 查询当日学生在慧记忆，慧听写，慧默写三个模块复习的生词总数
     *
     * @return
     */
    int countTodayLearnedUnknownWord();

    /**
     * 查询当前学生所有课程中熟词的个数
     *
     * @param stuId
     * @return
     */
    @Select("select count(distinct l.vocabulary_id) from learn l,vocabulary v where l.student_id = #{stuId} and l.`status` = 1 and l.vocabulary_id is not null and l.vocabulary_id <> '' and l.vocabulary_id = v.id and v.delStatus = 1")
    int countKnownWordByStudentId(@Param("stuId") Long stuId);

    /**
     * 查找学生的最后一次学习时间
     *
     * @param stuId
     * @return
     */
    Date selectLastStudyTimeByStudentId(@Param("stuId") Long stuId);

    /**
     * 根据学生id和课程id查询学习记录
     *
     * @param studentId
     * @param courseId
     * @return
     */
    List<Learn> selectByStuIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 批量保存学习记录
     *
     * @param learns
     * @return
     */
    Integer insertList(@Param("list") List<Learn> learns);

    @Select("select MAX(learn_time) from learn WHERE student_id = #{student_id} AND course_id = #{id} AND example_id IS NULL")
    String selectBylLearn_time(@Param("id") Long id, @Param("student_id") Long student_id);

    /**
     * 查询当前学生已学单词/例句数（单词/例句的三个学习模块学习的单词/例句数之和）
     *
     * @param stuId
     * @param courses
     * @param flag    1:查询单词个数；2：查询例句个数
     * @return map key：课程id，value：当前课程的单词/例句的三个学习模块学习的单词/例句数之和
     */
    @MapKey("id")
    Map<Long, Map<String, Long>> countWordsByCourseId(@Param("stuId") Long stuId, @Param("list") List<Course> courses, @Param("flag") int flag);

    @Select("select date_format(learn_time, '%Y') from learn where student_id = #{studentId} and learn_time IS NOT NULL GROUP BY  date_format(learn_time, '%Y') ORDER BY learn_time DESC")
    List<Integer> selectLearn_times(@Param("studentId") Long stuentId);

    Integer selectNumberByStudentId(@Param("student_id") Long student_id, @Param("unit_id") Integer unit_id, @Param("model") int model);

    /**
     * 查询当前单元下指定模块的熟词/熟句数
     *
     * @param stuId
     * @param model      学习类型 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @param flag       1：熟词/熟句；2：生词/生句
     * @param learnCount 第几遍学习当前课程
     * @return
     */
    Integer countKnownCountByStudentIdAndUnitId(@Param("stuId") Long stuId, @Param("unitId") Long unitId, @Param("model") int model, @Param("flag") int flag, @Param("learnCount") Integer learnCount);

    Map<String, Object> mapWeekCountQuantity(@Param("weekStart") String weekStart, @Param("weekEnd") String weekEnd, @Param("studentId") Long studentId);

    /**
     * 学生所有课程下学习的模块(去重)
     *
     * @param student
     * @return
     */
    @Select("select DISTINCT study_model from learn where student_id = #{student.id}")
    List<String> selectLearnedModelByStudent(@Param("student") Student student);

    String countCourseStudyModel(@Param("studentId") Long studentId, @Param("course_id") Long course_id, @Param("model") int model);

    /**
     * 查看当前学生所有课程指定模块的熟词个数，相同单词算一个，所有课程下该单词都是熟词才能算作一个熟词个数
     *
     * @param student
     * @param model   1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @return
     */
    int countKnownCountByStudentId(@Param("student") Student student, @Param("model") int model);

    /**
     * 今日学习单词数量
     */
    @Select("select count(DISTINCT vocabulary_id) from learn where date_format(learn_time, '%Y-%m-%d') = #{formatYYYYMMDD} AND student_id = #{studentId} AND example_id IS NULL")
    int getTodayWord(@Param("formatYYYYMMDD") String formatYYYYMMDD, @Param("studentId") long studentId);

    /**
     * 今日学习句子数量
     */
    @Select("select count(DISTINCT example_id) from learn where date_format(learn_time, '%Y-%m-%d') = #{formatYYYYMMDD} AND student_id = #{studentId} AND vocabulary_id IS NULL")
    int getTodaySentence(@Param("formatYYYYMMDD") String formatYYYYMMDD, @Param("studentId") long studentId);

    @Select("select count(DISTINCT unit_id) from learn where date_format(learn_time, '%Y-%m-%d') = #{formatYYYYMMDD} AND student_id = #{studentId} AND vocabulary_id IS NULL AND example_id IS NULL")
    int getTodyTeks(@Param("formatYYYYMMDD") String formatYYYYMMDD, @Param("studentId") long studentId);


    /**
     * 根据学习时间倒叙查询学生所学的单词或者例句
     *
     * @param stuId
     * @param courseId 当前课程id
     * @param model    1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @return
     */
    List<Learn> selectLearnedByStudentId(@Param("stuId") long stuId, @Param("courseId") Long courseId, @Param("model") int model);

    /**
     * 查看学生当前课程下学习的单词数
     *
     * @param stuId
     * @param courseId 当前课程id
     * @param model    1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @return
     */
    int countLearnWordByStudentIdAndCourseIdAndStudyModel(@Param("stuId") long stuId, @Param("courseId") Long courseId, @Param("model") int model);

    /**
     * 查询指定课程和模块的单词或例句id
     *
     * @param stuId
     * @param courseId
     * @param model
     * @return
     */
    List<Long> selectVocabularyIdByStudentIdAndCourseId(@Param("stuId") Long stuId, @Param("courseId") Long courseId, @Param("model") int model);

    @Select("select b.id, b.course_name as courseName from learn a join course b on a.course_id = b.id and a.student_id = #{student_id} AND a.vocabulary_id IS NULL GROUP BY b.id")
    List<Course> selectByCourseIdSentence(@Param("student_id") Long student_id);

    @Select("select MAX(learn_time) from learn WHERE student_id = #{student_id} AND course_id = #{id} AND vocabulary_id IS NULL")
    String selectBylLearn_timeSentence(@Param("id") Long id, @Param("student_id") Long student_id);

    @Select("select MAX(unit_id) from learn a where a.course_id = #{courseId} AND a.student_id=#{studentId}")
    Integer selectMaxUnitId(@Param("studentId") long studentId, @Param("courseId") Integer courseId);

    /**
     * 查询学生当前单元当前模块下已学习单词/例句的个数，即学习进度
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel
     * @return
     */
    Long learnCountWord(@Param("studentId") long studentId, @Param("unitId") int unitId, @Param("studyModel") String studyModel);

    /**
     * 查询学生当前模块学习的所有单词/例句id
     *
     * @param student
     * @param unitId
     * @param studyModel
     * @return
     */
    List<Long> selectLearnedWordIdByUnitId(@Param("student") Student student, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 查询指定学生指定单词的学习信息
     *
     * @param studentId
     * @param wordId
     * @return
     */
    List<Learn> selectByStudentIdAndWorldId(@Param("studentId") Long studentId, @Param("wordId") Long wordId);

    /**
     * 将指定单词置为生词
     *
     * @param student
     * @param unitId
     * @param courseId
     * @param wordId
     * @return
     */
    @Update("update learn set `status` = 0 where student_id = #{student.id} and unit_id = #{unitId} and course_id = #{courseId} and vocabulary_id = #{wordId} and type=1 ")
    int updateUnknownWord(@Param("student") Student student, @Param("unitId") Long unitId, @Param("courseId") Long courseId, @Param("wordId") Long wordId);

    /**
     * 将指定模块的指定单词/例句置为生词/生句
     *
     * @param student    学生信息
     * @param unitId     单词所属单元id
     * @param courseId   单词所属课程id
     * @param wordIds    单元id数组
     * @param studyModel 学习模块
     * @param studyCount 课程学习次数
     * @return
     */
    int updateUnknownWords(@Param("student") Student student, @Param("unitId") Long unitId, @Param("courseId") Long courseId, @Param("wordIds") Long[] wordIds, @Param("model") Integer studyModel, @Param("studyCount") int studyCount);

    /**
     * 获取当前课程下最新学习的30个单词用于阶段测试
     *
     * @param stuId    学生id
     * @param courseId 课程id
     * @return
     */
    List<Long> selectThirtyVocabularyIdByStudentIdAndCourseId(@Param("stuId") Long stuId, @Param("courseId") Long courseId);

    @Select("select id from learn where student_id = #{studentId} and study_model = '单词图鉴' LIMIT 0,1")
    Integer theFirstTimeToWordPic(Long studentId);

    /**
     * 查看当前学生当前学段下已掌握的单词个数
     * <p>
     * 已掌握单词：在慧记忆，慧听写，慧默写三个学习模块下都是熟词的单词才是已掌握的单词
     * </p>
     *
     * @param student
     * @param phase   学生学段    初中；高中
     * @return
     */
    int countMasterWord(@Param("student") Student student, @Param("phase") String phase);

    /**
     * 查看当前学生当前学段下已掌握的例句数
     * <p>
     * 已掌握单词：在例句听力，例句翻译，例句默写三个学习模块下都是熟句的例句才是已掌握的例句
     * </p>
     *
     * @param student
     * @param phase   学生学段    初中；高中
     * @return
     */
    int countMasterSentence(@Param("student") Student student, @Param("phase") String phase);

    /**
     * 查询学生学习的单词总个数
     *
     * @param studentId
     * @param flag      1:单词；2：例句
     * @return
     */
    int countByStudentId(@Param("studentId") Long studentId, @Param("flag") int flag);

    /**
     * 已学知识点大于learnedCount的学生个数
     *
     * @param learnedCount
     * @param phase        学段
     * @param student
     * @return
     */
    int countGreaterLearnedCount(@Param("learnedCount") int learnedCount, @Param("phase") String phase, @Param("student") Student student);

    Integer countHaveToLearnByModelAll(@Param("studentId") Long studentId, @Param("unitId") Integer id, @Param("model") int i);

    Integer countUnitAllStudyModel(@Param("studentId") Long studentId, @Param("unitId") Integer unitId, @Param("model") Integer model);

    /**
     * 已掌握单词
     *
     * @param studentId
     * @return
     */
    @Select("SELECT COUNT(DISTINCT l.vocabulary_id) FROM learn l, vocabulary v where l.vocabulary_id = v.id and v.delStatus = 1 and student_id = #{studentId} AND vocabulary_id is not NULL AND `status` = 1")
    int labelGraspWordsByStudentId(@Param("studentId") Long studentId);

    /**
     * 已掌握例句
     *
     * @param studentId
     * @return
     */
    @Select("SELECT COUNT(DISTINCT example_id) FROM learn where student_id = #{studentId} AND example_id is not NULL AND `status` = 1")
    int labelGraspExamplesByStudentId(@Param("studentId") Long studentId);

    Integer learnCourseCountSentence(@Param("studentId") Long studentId, @Param("model") int model, @Param("courseId") Long courseId);

    /**
     * 查询学生已学的单元ids
     *
     * @param studentId
     * @return
     */
    @Select("select unit_id unitId from learn where student_id = #{studentId} and unit_id is not null group by unit_id")
    List<Long> selectLearnedUnitsByStudentId(@Param("studentId") Long studentId);

    @Select("select unit_id from learn where student_id = #{studentId} GROUP BY unit_id ORDER BY id DESC LIMIT 1")
    String getEndUnitIdByStudentId(@Param("studentId") Long studentId);

    /**
     * 随机获取学生当前学习的课程下的110个单词
     *
     * @param courseId
     * @param wordIds  需要过滤掉的单词id
     * @return
     */
    List<Map<String, String>> selectWordInCurrentCourse(@Param("courseId") Long courseId, @Param("wordIds") List<Long> wordIds);

    /**
     * 获取学生上次登录期间单词的学习信息
     *
     * @param studentId
     * @param loginTime
     * @param loginOutTime
     * @param classify
     * @return
     */
    List<Learn> selectLastLoginStudy(@Param("studentId") Long studentId, @Param("loginTime") Date loginTime, @Param("loginOutTime") Date loginOutTime, @Param("classify") Integer classify);

    /**
     * 查找学生还没有学习的单元
     *
     * @param studentId
     * @param unitIds
     * @return key unitId value:courseId
     */
    @MapKey("unitId")
    Map<Long, Map<Long, Long>> selectUnlearnUnit(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitIds);

    /**
     * 从学生智能版课程中随机取出count个单词
     *
     * @param studentId
     * @param count
     * @param wordIds
     * @return
     */
    List<Map<String, String>> selectWordRandomInCourse(@Param("studentId") Long studentId, @Param("count") int count, @Param("wordIds") List<Long> wordIds);

    /**
     * 查询学生当前课程已学信息
     *
     * @param studentId
     * @param courseId
     * @return
     */
    List<Map<String, Object>> selectLearnedByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 查询学生当前单元已学信息
     *
     * @param unitId
     * @return
     */
    List<Map<String, Object>> selectLearnedByUnitId(@Param("unitId") Long unitId, @Param("beginRow") Integer beginRow, @Param("pageSize") Integer pageSize);

    /**
     * 修改单元下正在学习单词学习状态
     *
     * @param studentId
     * @param studyModel
     * @param unitId
     * @return
     */
    Integer updLearnByUnitIdAndStudyModelAndStudentId(@Param("studentId") Long studentId, @Param("studyModel") String studyModel, @Param("unitId") Integer unitId);

    Long selByStudentIdAndCourseIdDisVersion(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 查看课文id
     *
     * @param learn
     * @return
     */
    @Select("select id from learn where unit_id =#{unitId} and student_id=#{studentId} and course_id=#{courseId} and study_model=#{studyModel} and vocabulary_id is null and example_id is null")
    Long selTeksLearn(Learn learn);

    /**
     * 根据id修改学习时间
     *
     * @param learn
     * @return
     */
    @Update("update learn set update_time=#{updateTime} where id=#{id} ")
    Integer updTeksLearn(Learn learn);

    /**
     * 获取最后学习课文信息
     *
     * @param studentId
     * @return
     */
    List<Map<String, Object>> selectTeksLaterLearnTimeByStudentId(@Param("studentId") Long studentId);

    /**
     * 获取最后学习课文单元
     *
     * @param studentId
     * @param courseId
     * @return
     */
    Long selLaterLearnTeks(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 获取最后学习课文信息
     *
     * @param studentId
     * @return
     */
    Map<String, Object> selTeksLaterCourse(Long studentId);

    /**
     * 查看学习课文数量
     *
     * @param studentId
     * @param studyModel
     * @param unitId
     * @return
     */
    Integer selLearnTeks(@Param("studentId") Long studentId, @Param("studyModel") String studyModel, @Param("unitId") Long unitId);

    /**
     * 获取单元，模块下学习单元数量
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @return
     */
    Integer selAllTeksLearn(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitId, @Param("studyModel") String studyModel);

    /**
     * 将学生当前指定范围的单元学习记录置为以往学习
     *
     * @param studentId
     * @param studyType 1:单词；2：例句；3：课文
     * @param startUnit
     * @param endUnit
     */
    void updateTypeToLearned(@Param("studentId") Long studentId, @Param("studyType") int studyType, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit);

    /**
     * 学生当前课程下已学习的单元个数
     *
     * @param courseId
     * @param studentId
     * @return
     */
    @Select("select count(distinct unit_id) from learn where student_id = #{studentId} and course_id = #{courseId} ")
    int countLearnedUnitByCourseId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * 查询正在学习的学习记录 id
     *
     * @param studentId
     * @param learn
     * @param studyModel
     * @param type
     * @return
     */
    List<Long> selectLearnIds(@Param("studentId") Long studentId, @Param("learn") Learn learn,
                              @Param("studyModel") String studyModel, @Param("type") int type);

    /**
     * 查询学生当前单元当前流程的学习次数
     *
     * @param studentId
     * @param unitId
     * @param flowName
     * @return
     */
    int countByStudentIdAndFlow(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("flowName") String flowName);

    /**
     * 获取最后句子学习时间
     *
     * @param studentId
     * @param unitId
     * @return
     */
    Learn selLaterSentence(@Param("studentId") Long studentId, @Param("unitId") long unitId);

    /**
     * 统计学生当前模块的当前已经学习
     *
     * @param studentId  学生id
     * @param studyModel 模块类型
     * @param unitId     单元id
     * @return
     */
    int countByStudentIdAndStudyModel(@Param("studentId") Long studentId, @Param("studyModel") String studyModel, @Param("unitId") Long unitId);


    /**
     * 查看字母学习数量
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel 模块类型
     * @return
     */
    Integer selLetterLearn(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);


    /**
     * 查看已学字母播放器字母
     *
     * @param studentId
     * @param wordId
     * @param unitId
     * @param studyModel
     * @return
     */
    Learn selLetter(@Param("studentId") Long studentId, @Param("wordId") Long wordId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 修改模块学习状态
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel 模块类型
     */
    @Update("update learn set type=2 where student_id=#{studentId} and unit_id =#{unitId} and study_model=#{studyModel}")
    void updLetterPair(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 修改学习记录中的状态
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @param type       状态
     */
    void updateTypeByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel, @Param("type") int type);

    /**
     * 统计今天学生复习的记忆强度大于等于50%的生词数个数
     *
     * @param student
     * @return
     */
    int countTodayRestudyAndMemoryStrengthGePercentFifty(@Param("student") Student student);

    List<Learn> selectAllCapacityReview(@Param("studentId") Long id, @Param("classify") Integer classify);

    /**
     * 获取学生阅读生词手册中单词的学习信息
     *
     * @param readWord
     * @param studyModel
     * @return
     */
    Learn selectReadWord(@Param("readWord") ReadWord readWord, @Param("studyModel") String studyModel);

    /**
     * 统计学生今日学习的字母个数
     *
     * @param studentId
     * @return
     */
    int countTodayLearnedLetter(@Param("studentId") Long studentId);

    /**
     * 统计学生今日学习的音标个数
     *
     * @param studentId
     * @return
     */
    int countTodayLearnedPhoneticSymbol(@Param("studentId") Long studentId);

    /**
     * 获取指定语法模块的学习个数
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @return
     */
    int countLearnedSyntax(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 根据学生id，单元id、语法id、语法学习模块
     *
     * @param learn
     * @return
     */
    Learn selectLearnedSyntaxByUnitIdAndStudyModelAndWordId(@Param("learn") Learn learn);

    /**
     * 将当前课程的语法学习记录置为已学习状态
     *
     * @param studentId
     * @param courseId
     */
    void updateSyntaxToLearnedByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    int countSyntax(@Param("studentId") Long studentId);

    /**
     * 查询学习过的指定模块的语法
     *
     * @param studentStudySyntax
     * @return
     */
    List<Learn> selectSyntaxByUnitIdAndStudyModel(@Param("studentStudySyntax") StudentStudySyntax studentStudySyntax);


    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}
