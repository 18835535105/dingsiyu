package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PayCard;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

public interface PayCardMapper extends BaseMapper<PayCard> {

    /**
     * 修改卡为使用状态
     *
     * @param card 卡号
     * @param account 使用者账号
     * @return 1=成功， 2=失败
     */
    @Update("update pay_card set use_user = #{account}, use_time = #{date} where card_no = #{card} AND use_user is NULL")
    int updateCreateUserByCardNo(@Param("card") String card, @Param("account") String account, @Param("date") Date date);

    /**
     * 根据卡号获取id
     *
     * @param card 卡号
     * @return 主键id
     */
    @Select("select id from pay_card where card_no = #{card} ")
    Long getIdByCardNo(@Param("card") String card);

    @Select("select validity_time from pay_card where card_no = #{card}")
	int getValidityTimeByCard(@Param("card") String card);
}
