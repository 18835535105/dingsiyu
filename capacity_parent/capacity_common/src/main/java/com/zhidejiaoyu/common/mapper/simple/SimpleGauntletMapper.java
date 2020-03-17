package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Gauntlet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 挑战书表； Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
public interface SimpleGauntletMapper extends BaseMapper<Gauntlet> {

    /**
     * 学生当天已发出的挑战
     *
     * @param studentId
     * @return
     */
     List<Gauntlet> selByStudentIdAndFormat(@Param("studentId") Long studentId, @Param("time") Date time);

    /**
     * 查询数据
     * @param studentId
     * @param type 1,student发起人 总挑战数量 2,student发起人 总胜利数量  3,student被挑战人 总被挑战数量  4,student被挑战人 总胜利数量
     * @return
     */
    Integer getInformation(@Param("studentId") Long studentId, @Param("type") Integer type);

    /**
     * 查看显示数据
     * @param type
     * @param challengeType
     * @param start
     * @param end
     * @param studentId
     * @param time
     * @return
     */
    List<Gauntlet> selGauntletByTypeAndChallengeType(@Param("type") Integer type, @Param("challengeType") Integer challengeType,
                                                     @Param("start") Integer start, @Param("end") Integer end, @Param("studentId") Long studentId,
                                                     @Param("time") String time);

    /**
     * 查看挑战当前学生的挑战数量
     * @param studentId
     * @param id
     * @param type
     * @return
     */
    Integer getCountPkForMe(@Param("studentId") Long studentId, @Param("beStudentId") Long id, @Param("type") int type);

    /**
     * 查看全部挑战当前学生的数量
     * @param type
     * @param challengeType
     * @param studentId
     * @param time
     * @return
     */
    Integer getCount(@Param("type") Integer type, @Param("challengeType") Integer challengeType, @Param("studentId") Long studentId, @Param("time") String time);

    Gauntlet getByStudentIdAndBeStudentId(@Param("studentId") Long studentId, @Param("beStudentId") Long beStudentId);

    /**
     * 根据挑战id查询挑战信息详情
     * @param gauntletId
     * @return
     */
    Gauntlet getInformationById(Long gauntletId);

    /**
     * 根据type 和学生id查询挑战信息
     * @param studentId
     * @param start
     * @param end
     * @param type
     * @return
     */
    List<Gauntlet> selByStudentId(@Param("studentId") Long studentId, @Param("start") Integer start, @Param("end") Integer end, @Param("type") Integer type);

    /**
     * 查看挑战次数
     * @param id
     * @return
     */
    Integer selCountByStudentId(Long id);

    /**
     * 查看需要去除的挑战数据
     * @param studentId
     * @param date
     * @return
     */
    List<Gauntlet> selDelGauntlet(@Param("studentId") Long studentId, @Param("format") String date);

    /**
     * 根据挑战id修改挑战数据
     * @param status
     * @param gauntletIds
     */
    void updateStatus(@Param("status") int status, @Param("gauntletIds") List<Long> gauntletIds);

    /**
     * 获得当前学生为别挑战人时的挑战数量
     * @param studentId
     * @return
     */
    @Select("select count(id) from  gauntlet where be_challenger_student_id=#{studentId} and be_challenger_status=3 and challenger_point is not null")
    Integer selReceiveChallenges(@Param("studentId") Long studentId);

    /**
     * 根据挑战id修改超时状态
     * @param gauntletId
     * @return
     */
    @Update("update gauntlet set challenge_status=4 , be_challenger_status=4 where id =#{gauntletId}")
    Integer updateByStatus(@Param("gauntletId") Long gauntletId);

    /**
     * 清楚当前时间之前的数据信息
     * @param time
     * @return
     */
    @Update("update gauntlet set challenge_status=4 , be_challenger_status=4 where be_challenger_status=3 and create_time < #{time}")
    Integer updateByTime(String time);


    /**
     * 根据状态查看挑战信息
     * @param type
     * @param studentId
     * @return
     */
    List<Gauntlet> selectStudy(@Param("type") int type, @Param("studentId") Long studentId);

    void deleteByChallengerStudentIdsOrBeChallengerStudentIds(@Param("studentIds") List<Long> studentIds);

    int getCountByStudentIdAndTime(@Param("studentId") Long studentId,@Param("time") String toString,@Param("beforeTime") String beforeTime);

    List<Map<String,Object>> getPkRecord(@Param("student") Long studentId);

    int getPkGames(@Param("studentId") Long studentId,@Param("type") int type);
}
