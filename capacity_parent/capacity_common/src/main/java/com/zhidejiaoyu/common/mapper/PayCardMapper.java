package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PayCard;
import com.zhidejiaoyu.common.pojo.PayCardExample;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PayCardMapper extends BaseMapper<PayCard> {
    int countByExample(PayCardExample example);

    int deleteByExample(PayCardExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(PayCard record);

    List<PayCard> selectByExample(PayCardExample example);

    PayCard selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PayCard record, @Param("example") PayCardExample example);

    int updateByExample(@Param("record") PayCard record, @Param("example") PayCardExample example);

    int updateByPrimaryKeySelective(PayCard record);

    int updateByPrimaryKey(PayCard record);

    /**
     * 修改卡为使用状态
     * @param card 卡号
     * @param account 使用者账号
     * @return 1=成功， 2=失败
     */
    @Update("update pay_card set use_user = #{account}, use_time = #{date} where card_no = #{card} AND use_user is NULL")
    int updateCreateUserByCardNo(@Param("card") String card, @Param("account") String account, @Param("date") Date date);

    /**
     * 根据卡号获取id
     * @param card 卡号
     * @return 主键id
     */
    @Select("select id from pay_card where card_no = #{card} ")
    Long getIdByCardNo(@Param("card") String card);
}