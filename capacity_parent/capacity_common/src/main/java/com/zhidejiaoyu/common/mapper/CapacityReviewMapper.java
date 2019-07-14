package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.CapacityReview;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 记忆追踪mapper
 *
 * @author qizhentao
 * @version 1.0
 */
public interface CapacityReviewMapper {

	/** 个个模块许复习量, 可有多个条件查询 */
	Integer countCapacity_memory(CapacityReview cr);

	/**
	 * 根据模块查询需要复习的单词
	 *
	 * @param id 学生id
	 * @param unit_id 单元id
	 * @param classify 类型
	 * @return
	 */
	List<Vocabulary> selectCapacity(CapacityReview ca);

	double selectMemory_strength(CapacityReview cr);

	/** 根据{学生id, 单词或例句, 单元id} 修改记忆强度*/
	void updateMemory_strength(CapacityReview cr);

	CapacityReview ReviewCapacity_memory(CapacityReview ca);

	@Select("select vocabulary_id, word, word_chinese, memory_strength from sentence_listen where student_id = #{student_id} and unit_id = #{unit_id} and push < #{dateTime} order by push asc limit 0,1")
	CapacityReview ReviewSentence_listen(@Param("student_id") Long student_id,@Param("unit_id") String unit_id,@Param("dateTime") String dateTime);
	@Select("select vocabulary_id, word, word_chinese, memory_strength, unit_id from sentence_listen where student_id = #{student_id} and course_id = #{course_id} and push < #{dateTime} order by push asc limit 0,1")
	CapacityReview ReviewSentence_listenCourseId(@Param("student_id") Long student_id,@Param("course_id") String course_id,@Param("dateTime") String dateTime);

	void updatePush(CapacityReview cr);

	void updateLearn(CapacityReview cr);

	void updateLearnStudy_count(CapacityReview cr);

	@Select("select id from vocabulary where word = #{word}")
	Integer selectWordId(@Param("word") String word);

	@Select("select id from sentence where centreExample = #{word} or tallExample = #{word}")
	Integer selectSentenceId(@Param("word") String word);

	/**
	 * 学生当前课程下已学单词数
	 *
	 * @param studentId
	 * @param courseIds
	 * @param classify
	 * @param flag
	 * @return
	 */
	Integer countAlreadyStudyWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
								  @Param("classify") String classify, @Param("flag") int flag);

	/**
	 * 学生当前课程下已学生词数
	 *
	 * @param studentId
	 * @param unitId
	 * @param classify
	 * @param flag
	 * @return
	 */
	Integer countAccrueWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
							@Param("classify") String classify,  @Param("flag") int flag);

	/**
	 * 学生当前课程下已学熟词数
	 *
	 * @param studentId
	 * @param unitId
	 * @param classify
	 * @param flag
	 * @return
	 */
	Integer countRipeWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
						  @Param("classify") String classify,  @Param("flag") int flag);

	/**
	 * 模块1已学题
	 *
	 * @param student_id
	 * @param unit_id
	 * @param classifyStr
	 * @return
	 */
	@Select("select a.id, a.word, a.word_chinese as wordChinese, a.recordpicurl from vocabulary a INNER JOIN learn b on a.id = b.vocabulary_id and b.type = 1 and b.unit_id = #{unit_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} INNER JOIN unit_vocabulary uv ON uv.unit_id = b.unit_id AND uv.vocabulary_id = b.vocabulary_id  AND a.delStatus = 1")
	List<Vocabulary> alreadyWordStrOne(@Param("student_id") Long student_id,@Param("unit_id") String unit_id,@Param("classifyStr") String classifyStr);

	/**
	 * 模块1生词题
	 *
	 * @param student_id
	 * @param unit_id
	 * @param classifyStr
	 * @return
	 */
	@Select("select a.id, a.word, a.word_chinese as wordChinese, a.recordpicurl from vocabulary a INNER JOIN learn b on a.id = b.vocabulary_id and b.type = 1 and b.unit_id = #{unit_id} and b.student_id = #{student_id}	and b.study_model = #{classifyStr} and b.status = 0 INNER JOIN unit_vocabulary uv ON uv.unit_id = b.unit_id AND uv.vocabulary_id = b.vocabulary_id  AND a.delStatus = 1")
	List<Vocabulary> accrueWordStrOne(@Param("student_id") Long student_id,@Param("unit_id") String unit_id,@Param("classifyStr")  String classifyStr);

	/**
	 * 模块1熟词题
	 *
	 * @param student_id
	 * @param unit_id
	 * @param classifyStr
	 * @return
	 */
	@Select("select a.id, a.word, a.word_chinese as wordChinese, a.recordpicurl from vocabulary a INNER JOIN learn b on a.id = b.vocabulary_id and b.type = 1 and b.unit_id = #{unit_id} and b.student_id = #{student_id}	and b.study_model = #{classifyStr} and b.status = 1 INNER JOIN unit_vocabulary uv ON uv.unit_id = b.unit_id AND uv.vocabulary_id = b.vocabulary_id  AND a.delStatus = 1")
	List<Vocabulary> ripeWordStrOne(@Param("student_id") Long student_id, @Param("unit_id") String unit_id, @Param("classifyStr") String classifyStr);

	/**
	 * 模块2,3已学题
	 *
	 * @param student_id
	 * @param unit_id
	 * @param classifyStr
	 * @return
	 */
	@Select("select a.id, a.word, a.word_chinese as wordChinese, b.unit_id as status from vocabulary a INNER JOIN learn b on a.id = b.vocabulary_id and b.type = 1 and b.unit_id = #{unit_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} INNER JOIN unit_vocabulary uv ON uv.unit_id = b.unit_id AND uv.vocabulary_id = b.vocabulary_id")
	List<Vocabulary> alreadyWordStr(@Param("student_id") Long student_id, @Param("unit_id") String unit_id, @Param("classifyStr") String classifyStr);

	/**
	 * 模块2,3生词题
	 *
	 * @param student_id
	 * @param unit_id
	 * @param classifyStr
	 * @return
	 */
	@Select("select a.id, a.word, a.word_chinese as wordChinese, b.unit_id as status from vocabulary a INNER JOIN learn b on a.id = b.vocabulary_id and b.type = 1 and b.unit_id = #{unit_id} and b.student_id = #{student_id}	and b.study_model = #{classifyStr} and b.status = 0 INNER JOIN unit_vocabulary uv ON uv.unit_id = b.unit_id AND uv.vocabulary_id = b.vocabulary_id")
	List<Vocabulary> accrueWordStr(@Param("student_id") Long student_id, @Param("unit_id") String unit_id, @Param("classifyStr") String classifyStr);

	/**
	 * 模块2,3熟词题
	 *
	 * @param student_id
	 * @param unit_id
	 * @param classifyStr
	 * @return
	 */
	@Select("select a.id, a.word, a.word_chinese as wordChinese, b.unit_id as status from vocabulary a INNER JOIN learn b on a.id = b.vocabulary_id and b.type = 1 and b.unit_id = #{unit_id} and b.student_id = #{student_id}	and b.study_model = #{classifyStr} and b.status = 1 INNER JOIN unit_vocabulary uv ON uv.unit_id = b.unit_id AND uv.vocabulary_id = b.vocabulary_id")
	List<Vocabulary> ripeWordStr(@Param("student_id") Long student_id, @Param("unit_id") String unit_id, @Param("classifyStr") String classifyStr);

	// 4,5,6模块初中已学题
	@Select("select a.id, a.centreExample, a.centreTranslate from learn b INNER JOIN sentence a on a.id = b.example_id and b.unit_id = #{unit_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} and type=1 limit 0,20")
	List<Sentence> centreReviewSentence_listen(@Param("student_id") Long student_id,@Param("unit_id") String unit_id,@Param("classifyStr") String classifyStr);
	@Select("select a.id, a.centreExample, a.centreTranslate from learn b INNER JOIN sentence a on a.id = b.example_id and b.unit_id = #{unit_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} and type=1 and b.status = 0  limit  0,20")
	List<Sentence> accrueCentreReviewSentence_listen(@Param("student_id") Long student_id, @Param("unit_id") String unit_id, @Param("classifyStr") String classifyStr);
	@Select("select a.id, a.centreExample, a.centreTranslate from learn b INNER JOIN sentence a on a.id = b.example_id and b.unit_id = #{unit_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} and type=1 and b.status = 1  limit 0,20")
	List<Sentence> ripeCentreReviewSentence_listen(@Param("student_id") Long student_id, @Param("unit_id") String unit_id, @Param("classifyStr") String classifyStr);

	// 4,5,6模块高中已学题
	@Select("select a.id, a.tallExample, a.tallTranslate from learn b INNER JOIN sentence a on a.id = b.example_id and b.course_id = #{course_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} limit 0,20")
	Sentence tallReviewSentence_listen(@Param("student_id") Long student_id,@Param("course_id") String course_id,@Param("classifyStr") String classifyStr);
	@Select("select a.id, a.tallExample, a.tallTranslate from learn b INNER JOIN sentence a on a.id = b.example_id and b.course_id = #{course_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} and b.status = 0  limit 0,20")
	Sentence accrueTallReviewSentence_listen(Long student_id, String course_id, String classifyStr);
	@Select("select a.id, a.tallExample, a.tallTranslate from learn b INNER JOIN sentence a on a.id = b.example_id and b.course_id = #{course_id} and b.student_id = #{student_id} and b.study_model = #{classifyStr} and b.status = 1  limit 0,20")
	Sentence ripeTallReviewSentence_listen(Long student_id, String course_id, String classifyStr);

	//-- 根据单词,课程查询单元id
	@Select("SELECT a.id FROM unit a INNER JOIN unit_vocabulary b on a.id = b.unit_id INNER JOIN vocabulary c on b.vocabulary_id = c.id and c.word = #{word} and a.course_id = #{course_id} LIMIT 0,1")
	Long selectWordUnit_id(@Param("word") String word,@Param("course_id") Integer course_id);

	/**
	 * 根据课程id和学生id查询 learn表得到单词id,名,翻译
	 *
	 * @param course_id
	 * @param student_id
	 * @return
	 */

	//@Select("select c.id, c.word, b.word_chinese from unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{course_id} AND c.delStatus = 1 limit 0,30")
	@Select("select a.id, a.word, a.word_chinese as wordChinese from vocabulary a INNER join learn b on a.id = b.vocabulary_id and b.course_id = #{course_id} and b.student_id = #{student_id} AND a.delStatus = 1 limit 0,30")
	List<Vocabulary> testeffect(@Param("course_id") String course_id,@Param("student_id") Long student_id);

	@Delete("delete from capacity_listen where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataOne(@Param("course_id") Integer course_id,@Param("student_id")  Long student_id);
	@Delete("delete from capacity_memory where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataTwo(@Param("course_id") Integer course_id,@Param("student_id")  Long student_id);
	@Delete("delete from capacity_write where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataThree(@Param("course_id") Integer course_id,@Param("student_id")  Long student_id);
	@Delete("delete from sentence_listen where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataFour(@Param("course_id") Integer course_id,@Param("student_id")  Long student_id);
	@Delete("delete from sentence_translate where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataFive(@Param("course_id") Integer course_id,@Param("student_id")  Long student_id);
	@Delete("delete from sentence_write where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataSix(@Param("course_id") Integer course_id,@Param("student_id") Long student_id);
	@Delete("delete from learn where student_id = #{student_id} and course_id = #{course_id}")
	void delClearCourseDataSeven(Integer course_id, Long student_id);

	List<Vocabulary> fiveDimensionTest(@Param("course_id")String course_id, @Param("b")int b);

	List<Vocabulary> fiveDimensionTestTwo(@Param("course_id")String course_id, @Param("b")int b, @Param("c")int c);

	@Select("select * from unit_sentence a INNER JOIN sentence b on a.sentence_id = b.id INNER JOIN unit c ON a.unit_id = c.id AND b.centreExample=#{word} AND c.course_id=#{course_id} GROUP BY b.id")
	Long selectSentenceToUnitId(@Param("word") String word,@Param("course_id") Integer course_id);

	@Select("select vocabulary_id, word, word_chinese, memory_strength from sentence_translate where student_id = #{student_id} and unit_id = #{unit_id} and push < #{dateTime} order by push asc limit 0,1")
	CapacityReview ReviewSentence_translate(@Param("student_id") Long student_id,@Param("unit_id") String unit_id,@Param("dateTime") String dateTime);

	@Select("select vocabulary_id, word, word_chinese, memory_strength from sentence_write where student_id = #{student_id} and unit_id = #{unit_id} and push < #{dateTime} order by push asc limit 0,1")
	CapacityReview ReviewSentence_write(@Param("student_id") Long student_id,@Param("unit_id") String unit_id,@Param("dateTime") String dateTime);

	@Select("select vocabulary_id, word, word_chinese, memory_strength, unit_id from sentence_translate where student_id = #{student_id} and course_id = #{course_id} and push < #{dateTime} order by push asc limit 0,1")
	CapacityReview Reviewsentence_translateCourseId(@Param("student_id") Long stuId, @Param("course_id") String course_id, @Param("dateTime") String dateTime);

	@Select("select vocabulary_id, word, word_chinese, memory_strength, unit_id from sentence_write where student_id = #{student_id} and course_id = #{course_id} and push < #{dateTime} order by push asc limit 0,1")
	CapacityReview ReviewSentence_writeCourseId(@Param("student_id") Long stuId, @Param("course_id") String course_id, @Param("dateTime") String dateTime);

	/**
	 * 查找指定单词/例句的记忆追踪信息
	 *
	 * @param student  学生信息
	 * @param courseId 当前单词/例句的课程id
	 * @param unitId   当前单词/例句的单元id
	 * @param wordIds  所需查询的单元/例句的id数组student_id
	 * @param classify 学习模块  1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
	 * @return 包含 id, push, memory_strength
	 */
	List<Long> selectByWordIdsAndStudyModel(@Param("student") Student student, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("wordIds") Long[] wordIds, @Param("classify") Integer classify);

	/**
	 * 根据id批量更新记忆追踪中 黄金记忆时间 和 记忆强度
	 *
	 * @param updateIds      需要更新的记忆追踪id
	 * @param push           黄金记忆时间
	 * @param memoryStrength 记忆强度
	 * @param classify       学习模块  1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
	 * @return
	 */
	int updatePushAndMemoryStrengthByPrimaryKeys(@Param("updateIds") List<Long> updateIds, @Param("push") Date push, @Param("memoryStrength") Double memoryStrength, @Param("classify") Integer classify);

	/**
	 * 批量增加记忆追踪信息
	 *
	 * @param capacityReviews 记忆追踪信息
     * @param classify        学习模块  0=单词图鉴，1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
	 * @return
	 */
	int insertByBatch(@Param("capacityReviews") List<CapacityReview> capacityReviews, @Param("classify") Integer classify);

	@Select("select c.id,c.word, c.word_chinese as wordChinese from unit a INNER JOIN unit_vocabulary b on a.id = b.unit_id INNER JOIN vocabulary c on b.vocabulary_id = c.id and a.course_id = #{course_id} and c.delStatus = 1 order by rand()")
	List<Vocabulary> fiveDimensionTestAll(@Param("course_id") String course_id);

    /**
     * 查询记忆追踪中指定单词/例句信息
     *
     * @param student    学生信息
     * @param courseId   课程id
     * @param unitId     单元id
     * @param id         当前单词/例句id
     * @param studyModel 学习模块 慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写
     * @return
     */
    CapacityReview selectByCourseIdOrUnitId(@Param("student") Student student, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("id") Long id, @Param("studyModel") String studyModel);

    /**
     * 查询慧追踪中生词/生句个数
     *
     * @param student    学生信息
     * @param courseId   课程id
     * @param unitId     单元id
     * @param studyModel 学习模块 慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写
     * @return
     */
    List<CapacityReview> selectNewWordsByCourseIdOrUnitId(@Param("student") Student student, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 查询慧追踪中需要复习的单词/例句个数（达到黄金记忆点的单词/例句个数）
     *
     * @param student    学生信息
     * @param courseId   课程id
     * @param unitId     单元id
     * @param studyModel 学习模块 慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写
     * @return
     */
    int countNeedReviewByCourseIdOrUnitId(@Param("student") Student student, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

	/**
	 * 查询指定例句学习模块达到黄金记忆点的句子信息
	 *
	 * @param studentId
	 * @param unitId
	 * @param classify
	 * @return
	 */
	List<CapacityReview> selectSentenceCapacity(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("classify") int classify);

    /**
     * 获取当前所学课程下单词图鉴需要复习的单词
     *
     * @param stuId
     * @return
     */
    List<Map<String, Object>> selectPictureNeedReviewInCurrentCourse(@Param("stuId") Long stuId);


    /**
     * 获取当前所学课程下慧记忆需要复习的单词
     *
     * @param stuId
     * @param wordIds
     * @param limit
     * @return
     */
    List<Map<String, Object>> selectMemoryNeedReviewInCurrentCourse(@Param("stuId") Long stuId, @Param("wordIds") Set<Long> wordIds, @Param("limit") int limit);

    /**
     * 获取当前所学课程下慧听力需要复习的单词
     *
     * @param stuId
     * @param wordIds
     * @param limit
	 * @return
     */
    List<Map<String, Object>> selectListenNeedReviewInCurrentCourse(@Param("stuId") Long stuId, @Param("wordIds") Set<Long> wordIds, @Param("limit") int limit);

    /**
     * 获取当前所学课程下慧默写需要复习的单词
     *
     * @param stuId
     * @param wordIds
     * @param limit
	 * @return
     */
    List<Map<String, Object>> selectWriteNeedReviewInCurrentCourse(@Param("stuId") Long stuId, @Param("wordIds") Set<Long> wordIds, @Param("limit") int limit);

	/**
	 * 获取学生指定单元下指定单词需要复习的个数
	 *
	 * @param studentId
	 * @param maps
	 * @param classify
	 * @param typeStr
	 * @return
	 */
	Integer countCapacityByUnitIdAndWordId(@Param("studentId") Long studentId, @Param("maps") List<Map<String, Object>> maps, @Param("classify") int classify, @Param("typeStr") String typeStr);

	/**
	 * 查询上次登录期间需要复习的生词(取黄金记忆点最大的一个)
	 *
	 * @param studentId
	 * @param maps
	 * @param classify
	 * @return
	 */
	Map<String, Object> selectLastLoginNeedReview(@Param("studentId") Long studentId, @Param("maps") List<Map<String, Object>> maps, @Param("classify") int classify);

	/**
	 * 查询上次登录期间需要复习的生词个数
	 *
	 * @param studentId
	 * @param maps
	 * @param classify
	 * @return
	 */
	int countLastLoginNeedReview(@Param("studentId") Long studentId, @Param("maps") List<Map<String, Object>> maps, @Param("classify") int classify);

	/**
	 * 统计各个模块需要进行复习的个数
	 *
	 * @param studentId
	 * @param now
	 * @param i	学习模块 0：单词图鉴，1：慧记忆 2：慧听写 3：慧默写 4：例句听力 5：例句翻译 6：例句默写
	 * @return
	 */
    int countByPushByCourseId(@Param("studentId") Long studentId, @Param("now") String now, @Param("i") int i);

    CapacityReview selectSentenceCapacitys(@Param("studentId") Long studentId,@Param("classify") Integer classify);

	Integer selSentenceCountCapacitys(@Param("studentId") Long id,@Param("classify") Integer classify);

	/**
	 * 删除学生指定单元指定类型的慧追踪信息
	 *
	 * @param studentId
	 * @param unitId
	 * @param classify
	 * @return
	 */
    int deleteByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("classify") Integer classify);
}
