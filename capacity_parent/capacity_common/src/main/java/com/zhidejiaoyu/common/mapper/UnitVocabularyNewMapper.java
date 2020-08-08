package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.UnitVocabularyNew;
import com.zhidejiaoyu.common.vo.beforelearngame.VocabularyVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 单元-词汇表 (中间表)
 * 导入词汇关联课程单元方法:
 * 1.查询所有课程表中的课程单元拼接名(key=名 value=课程id);  2.保存完词汇,主建返回, 通过课程单元拼接名去map遍历中找一样的key获取value, 把value(课程主建) 和 返回的主建保存到中间表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface UnitVocabularyNewMapper extends BaseMapper<UnitVocabularyNew> {

    /**
     * 查询当前单元的下一个group
     *
     * @param unitId
     * @param group
     * @return 下一个group
     */
    @Select("select `group` from unit_vocabulary_new where unit_id = #{unitId} and `group` > #{group} order by id limit 1")
    Integer selectNextGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询单词模块当前单元group的个数
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("SELECT COUNT(1) FROM unit_vocabulary_new WHERE (unit_id = #{unitId} AND `group` = #{group})")
    Integer countUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询当前单元下含有图片的单词总个数
     *
     * @param unitId
     * @return
     */
    @Select("SELECT count(distinct v.id) FROM unit_vocabulary_new uv, vocabulary v WHERE uv.vocabulary_id = v.id AND v.delStatus = 1 and uv.group=#{group} AND recordpicurl IS NOT NULL AND uv.unit_id = #{unitId}")
    int countWordPictureByUnitId(@Param("unitId") Long unitId, @Param("group") Integer group);

    @Select("select count(distinct vocabulary_id) from unit_vocabulary_new where unit_id=#{unitId} and `group` =#{group}")
    int countByUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 获取当前单元下的所有不是删除状态单词的总个数
     *
     * @param unitId
     * @return
     */
    @Select("select count(distinct uv.vocabulary_id) from unit_vocabulary_new uv where uv.unit_id=#{unitId}")
    Long countByUnitId(Long unitId);

    List<String> selectInterferenceTerm(@Param("unitId") Long unitId, @Param("wordId") Long vocabularyId, @Param("chinese") String wordChinese);

    /**
     * 根据单元id和单词id查找单词的释义
     *
     * @param unitId
     * @param wordId
     * @return
     */
    @Select("select word_chinese from unit_vocabulary_new where unit_id=#{unitId} and vocabulary_id =#{wordId} limit 1")
    String selectWordChineseByUnitIdAndWordId(@Param("unitId") Long unitId, @Param("wordId") Long wordId);

    /**
     * 统计当前单元下含有单词图鉴的单词个数
     *
     * @param unitId
     * @param group
     * @return
     */
    int countPicture(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询当前单元当前group下的单词数据
     *
     * @param unitId
     * @param group
     * @return
     */
    List<VocabularyVO> selectByUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询当前课程下所有单词数
     *
     * @param courseId
     * @return
     */
    @Select("SELECT count(DISTINCT(b.vocabulary_id)) FROm unit_new a JOIN unit_vocabulary_new b ON a.id = b.unit_id AND a.course_id = #{courseId}")
    int countAllCountWordByCourse(Long courseId);

    /**
     * 查询指定单元一批单词的释义
     *
     * @param unitId
     * @param idSet  单词id集合
     * @return map key:单词id， map:key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByUnitIdAndWordIds(@Param("unitId") Long unitId, @Param("idSet") Set<Long> idSet);


    /**
     * 查询当前课程一批单词的释义
     *
     * @param courseId 当前课程id
     * @param idSet    单词id集合
     * @return map key:单词id， map:key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByCourseIdIdAndWordIds(@Param("courseId") Long courseId, @Param("idSet") Set<Long> idSet);

    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByCourseIdIdAndWordIds5DTest(@Param("idSet") Set<Long> idSet, @Param("start") String start, @Param("end") String end);

    /**
     * 查询各个单元的总单词数
     *
     * @param unitIds
     * @return
     */
    @MapKey("unitId")
    Map<Long, Map<Long, Long>> countTotalWordMapByUnitIds(@Param("unitIds") List<Long> unitIds);

    /**
     * 获取当前课程下的所有不是删除状态单词的总个数
     *
     * @param courseId
     * @return
     */
    @Select("select count(distinct uv.vocabulary_id) from unit_vocabulary_new uv, unit_new u where u.id = uv.unit_id and u.course_id = #{courseId}")
    Long countByCourseId(Long courseId);

    /**
     * 根据单元id查询出当前单元的所有单词
     *
     * @param unitId
     * @return key：单词id value: map  key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByUnitId(@Param("unitId") Long unitId);

    List<Long> selectWordIdByUnitIdAndGroup(@Param("unitId") Long unitId,@Param("group") Integer group);

    /**
     * 获取接下来要学的词信息
     *
     * @param unitId
     * @param wodIds
     * @return
     */
    Map<String, Object> selectStudyMap(@Param("unitId") Long unitId,
                                       @Param("wordIds") List<Long> wodIds,
                                       @Param("type") Integer type,
                                       @Param("group") Integer group);
}
