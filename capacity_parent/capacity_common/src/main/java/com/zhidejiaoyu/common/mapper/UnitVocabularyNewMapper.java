package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitVocabularyNew;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.vo.beforelearngame.VocabularyVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    @Select("SELECT count(v.id) FROM unit_vocabulary_new uv, vocabulary v WHERE uv.vocabulary_id = v.id AND v.delStatus = 1 and uv.group=#{group} AND recordpicurl IS NOT NULL AND uv.unit_id = #{unitId}")
    int countWordPictureByUnitId(@Param("unitId") Long unitId, @Param("group") Integer group);

    @Select("select count(id) from unit_vocabulary_new where unit_id=#{unitId} and `group` =#{group}")
    int countByUnitId(@Param("unitId") Long unitId, @Param("group") Integer group);

    List<String> selectInterferenceTerm(@Param("unitId") Long unitId, @Param("wordId") Long vocabularyId, @Param("chinese") String wordChinese);

    @Select("select word_chinese from unit_vocabulary_new where unit_id=#{unitId} and vocabulary_id =#{wordId}")
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
}
