package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.Example;
import com.zhidejiaoyu.common.pojo.ExampleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SimpleExampleMapper {
    int countByExample(ExampleExample example);

    int deleteByExample(ExampleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Example record);

    int insertSelective(Example record);

    List<Example> selectByExample(ExampleExample example);

    Example selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Example record, @Param("example") ExampleExample example);

    int updateByExample(@Param("record") Example record, @Param("example") ExampleExample example);

    int updateByPrimaryKeySelective(Example record);

    int updateByPrimaryKey(Example record);

    /**
     * 批量保存固定搭配
     * @param examples
     * @return
     */
	void insertByList(List<Example> examples);
}
