package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitVocabularyNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * @return
     */
    @Select("select group from unit_vocabulary_new where unit_id = #{unitId} and group > #{group}")
    Integer selectNextGroup(@Param("unitId") Long unitId, @Param("group") Integer group);
}
