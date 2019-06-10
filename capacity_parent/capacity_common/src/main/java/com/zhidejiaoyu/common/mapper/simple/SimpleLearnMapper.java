package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SimpleLearnMapper extends BaseMapper<Learn> {
    int countByExample(LearnExample example);

    int deleteByExample(LearnExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Learn record);

    List<Learn> selectByExample(LearnExample example);

    Learn selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Learn record, @Param("example") LearnExample example);

    int updateByExample(@Param("record") Learn record, @Param("example") LearnExample example);

    int updateByPrimaryKeySelective(Learn record);

    int updateByPrimaryKey(Learn record);

    /**
     * 查询学生当前模块下已学习单词的个数，即学习进度
     *
     * @param studentId  学生id
     * @param courseIds     课程id
     * @param typeStr
     * @return
     */
    Long countLearnWordByCourse(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds,
                                   @Param("typeStr") String typeStr);

    /**
     * 查询当前单词的学习记录数据
     *
     * @param studentId
     * @param learn
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @param count      当前课程的学习遍数
     * @return
     */
    Learn selectLearn(@Param("studentId") Long studentId, @Param("learn") Learn learn,
                      @Param("studyModel") String studyModel, @Param("count") Integer count);

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
     * @param count      当前课程的学习遍数
     * @return
     */
    Learn selectLearnByIdAmdModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                  @Param("wordId") Long wordId, @Param("sentenceId") Long sentenceId, @Param("studyModel") String studyModel, @Param("count") Integer count);

    /**
     * 查询当前学生已学单词 的个数
     *
     * @param studentId
     * @param unitId
     * @return
     */
    @Select("SELECT count(l.id) FROM learn l,vocabulary v where l.vocabulary_id = v.id AND l.course_id = 2479 AND student_id = 3155 AND v.delStatus = 1 and learn_count = #{count}")
    Long selectWordCountByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("count") Integer count);


    /**
     * 查询当前课程下的单词/例句的学习次数
     *
     * @param studentId  当前学生id
     * @param courseId   当前课程id
     * @param unitId  当前单元id，如果id为0，根据课程查询；否则根据单元id查询
     * @param id         单词/例句id
     * @param studyModel 学习模块
     * @return 学习次数
     */
    Integer selectStudyCountByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("unitId") Long unitId,
                                       @Param("id") Long id, @Param("studyModel") String studyModel, @Param("count") Integer count);

    /**
     * 查询当前单元下生词/生句的个数
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @return
     */
    Long countNotKnownWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                           @Param("studyModel") String studyModel, @Param("count") Integer count);

    @Select("select id from learn where student_id = #{id} and study_model = '慧听写' LIMIT 0,1")
    Integer theFirstTime(@Param("id") Long id);

    /**
     * 查询当前课程下指定模块的已学习单词/例句个数
     *
     * @param studentId
     * @param courseId
     * @param studyModel 学习模块 （单词图鉴，慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写）
     * @return
     */
    int countByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("studyModel") String studyModel);

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

    @Select("select MAX(learn_time) from learn WHERE student_id = #{student_id} AND course_id = #{id} and type = 1 AND example_id IS NULL")
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

    @Select("select date_format(learn_time, '%Y') from learn where student_id = #{studentId} and type = 1 and learn_time IS NOT NULL GROUP BY  date_format(learn_time, '%Y') ORDER BY learn_time DESC")
    List<Integer> selectLearn_times(@Param("studentId") Long studentId);

    /**
     * 课程首次学习时间
     *
     * @param stuId
     * @param courses
     * @return map key:课程id，value：当前课程首次学习日期
     */
    @MapKey("id")
    Map<Long, Map<String, Date>> selectFirstStudyTimeMapByCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses);

    /**
     * 查询当前课程上次学习时间
     *
     * @param stuId
     * @param courses
     * @return
     */
    @MapKey("id")
    Map<Long, Map<String, Date>> selectLastStudyTimeMapByCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses);

    Integer selectNumberByStudentId(@Param("student_id") Long student_id, @Param("unit_id") Integer unit_id, @Param("type") int model);

    /**
     * 查询当前课程下指定模块的熟词/熟句数
     *
     * @param stuId
     * @param courseId
     * @param model      学习类型 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @param flag       1：熟词/熟句；2：生词/生句
     * @param learnCount 第几遍学习当前课程
     * @return
     */
    Integer countKnownCountByStudentIdAndCourseId(@Param("stuId") Long stuId, @Param("courseId") Long courseId, @Param("model") int model, @Param("flag") int flag, @Param("learnCount") Integer learnCount);

    /**
     * 查询当前单元下指定模块的熟词/熟句数
     *
     * @param stuId
     * @param courseId
     * @param model      学习类型 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @param flag       1：熟词/熟句；2：生词/生句
     * @param learnCount 第几遍学习当前课程
     * @return
     */
    Integer countKnownCountByStudentIdAndUnitId(@Param("stuId") Long stuId, @Param("unitId") Long unitId, @Param("model") int model, @Param("flag") int flag, @Param("learnCount") Integer learnCount);

    @Select("SELECT count(id) FROM learn where  date_format(learn_time, '%Y-%m-%d') >= #{weekStart} and date_format(learn_time, '%Y-%m-%d') <= #{weekEnd} AND student_id = #{studentId} ")
    Integer weekCountQuantity(@Param("weekStart") String weekStart, @Param("weekEnd") String weekEnd, @Param("studentId") Long studentId);

    Map<String, Object> mapWeekCountQuantity(@Param("weekStart") String weekStart, @Param("weekEnd") String weekEnd, @Param("studentId") Long studentId);

    /**
     * 学生所有课程下学习的模块(去重)
     *
     * @param student
     * @return
     */
    @Select("select DISTINCT study_model from learn where student_id = #{student.id} and study_model in ('慧记忆', '慧听写', '慧默写', '例句听力', '例句翻译')")
    List<String> selectLearnedModelByStudent(@Param("student") Student student);

    String countCourseStudyModel(@Param("studentId") Long studentId, @Param("course_id") Long course_id, @Param("model") int model);

    Map<String, Object> countUnitStudyModel(@Param("studentId") Long studentId, @Param("course_id") Long course_id, @Param("model") int model);

    Integer countByWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("model") Integer model);

    //Integer countBySentence(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("model") Integer model);

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
     * @param maxCount
     * @return
     */
    List<Long> selectLearnedWordIdByUnitId(@Param("student") Student student, @Param("unitId") Long unitId, @Param("studyModel") String studyModel, @Param("maxCount") Integer maxCount);

    @Select("select id from learn where student_id = #{id} and course_id = #{courseId} and vocabulary_id is null limit 0,1")
    Integer selectByidToStudent(@Param("id") long id, @Param("courseId") Integer courseId);

    @Select("select id from learn where student_id = #{id} and course_id = #{courseId} and example_id is null limit 0,1")
    Integer selectByidToStudentWord(@Param("id") long id, @Param("courseId") Integer courseId);

    @Select("select count(a.id) from learn a JOIN vocabulary b ON a.vocabulary_id = b.id where a.student_id = #{id} and a.course_id = #{course_id} and a.study_model = #{model} and b.delStatus = 1 and a.type = 1")
    Long learnCourseCountWord(@Param("id") Long id, @Param("course_id") String course_id, @Param("model") String model);

    /**
     * 查看学生学习当前单词的个数
     *
     * @param student
     * @param wordId
     * @return
     */
    @Select("select count(id) from learn where student_id = #{student.id} and vocabulary_id = #{wordId}")
    int countByStudentIdAndWordId(@Param("student") Student student, @Param("wordId") Long wordId);

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
    @Update("update learn set `status` = 0 where student_id = #{student.id} and unit_id = #{unitId} and course_id = #{courseId} and vocabulary_id = #{wordId} ")
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
     *                   @return
     */
    int updateUnknownWords(@Param("student") Student student, @Param("unitId") Long unitId, @Param("courseId") Long courseId, @Param("wordIds") Long[] wordIds, @Param("model") Integer studyModel, @Param("studyCount") int studyCount);

    /**
     * 获取当前课程下最新学习的30个单词用于阶段测试
     *
     * @param stuId 学生id
     * @param courseId  课程id
     * @return
     */
    List<Long> selectThirtyVocabularyIdByStudentIdAndCourseId(@Param("stuId") Long stuId, @Param("courseId") Long courseId);

    @Delete("delete from learn where vocabulary_id = #{id}")
    void delWord(String id);

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
     * @param flag  1:单词；2：例句
     * @return
     */
    int countByStudentId(@Param("studentId") Long studentId, @Param("flag") int flag);

    /**
     * 已学知识点大于learnedCount的学生个数
     *
     * @param learnedCount
     * @param phase 学段
     * @param student
     * @return
     */
    int countGreaterLearnedCount(@Param("learnedCount") int learnedCount, @Param("phase") String phase, @Param("student") Student student);



    Integer countUnitAllStudyModel(@Param("studentId") Long studentId, @Param("unitId") Integer unitId, @Param("model") Integer model);

    /**
     * 根据金币获取对应的等级名
     *
     * @param myGold 金币
     * @return 子等级名
     */
    @Select("select child_name from level where gold >= #{myGold} limit 0,1")
    String getLevelNameByGold(@Param("myGold") int myGold);

	Long getSimpleLearnByStudentIdByModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    /**
     * 获取清学版课程 id
     * @param studentId
     * @return
     */
	List<StudentCourse> getSimpleCourseId(@Param("student_id") Long studentId);

	@Select("select id from learn where student_id = #{student_id} and study_model = #{testModelStr} limit 1")
	Integer getModelLearnInfo(@Param("student_id") Long student_id, @Param("testModelStr") String testModelStr);

	@Select("select count(id) from learn where unit_id = #{unitId} AND student_id = #{studentId} and type = 1")
	Integer countHaveToLearnByModelAll(@Param("studentId") Long studentId, @Param("unitId") Object unitId);

    /**
     * 统计学生所有课程下当前模块的生词数量
     *
     * @param studentId
     * @param courseIds
     * @param typeStr
     * @return
     */
    Long countUnknownWord(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds, @Param("typeStr") String typeStr);

	Integer selectCourseWordNumberByStudentId(@Param("student_id") Long student_id, @Param("course_id") Integer course_id, @Param("type") int i);

    /**
     * 课程下已学单词数量
     *
     * @param studentId
     * @param course_id
     * @return
     */
	@Select("select count(id) from learn where student_id = #{studentId} AND course_id = #{course_id} and type = 1")
	Integer countSimpleByCourseId(@Param("studentId") Long studentId, @Param("course_id") Long course_id);

	@Select("select count(DISTINCT(b.id)) from learn a join vocabulary b ON a.vocabulary_id = b.id AND b.delStatus = 1 AND a.student_id = #{studentId} AND a.course_id = #{courseId} and type = 1")
	int getAllCountWordByCourse(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * 获取学生当前单词的学习记录
     *
     * @param student
     * @param unitId
     * @param vocabularyId
     * @param type
     * @return
     */
    Learn selectLearnRecord(@Param("student") Student student, @Param("unitId") Long unitId, @Param("vocabularyId") Long vocabularyId, @Param("type") String type);

    /**
     * 统计今天学生复习的记忆强度大于等于50%的生词数个数
     *
     * @param student
     * @return
     */
    int countTodayRestudyAndMemoryStrengthGePercentFifty(@Param("student") Student student);

    /**
     * 更新学习记录指定模块为以往学习状态
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     */
    @Update("update learn set type = 2 where student_id = #{studentId} and unit_id = #{unitId} and study_model = #{studyModel}")
    void updateTypeByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 各个单元学习的单词个数
     *
     * @param studentId
     * @param unitIds
     * @param modelName
     * @return
     */
    @MapKey("unitId")
    Map<Long, Map<Long, Long>> countUnitLearnWordMapByUnitIds(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitIds, @Param("modelName") String modelName);
}
