package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Gauntlet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 挑战书表； Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
public interface SimpleGauntletMapper extends BaseMapper<Gauntlet> {

     List<Gauntlet> selByStudentIdAndFormat(@Param("studentId") Long studentId);

    /**
     * 查询数据
     * @param studentId
     * @param type 1,student发起人 总挑战数量 2,student发起人 总胜利数量  3,student被挑战人 总被挑战数量  4,student被挑战人 总胜利数量
     * @return
     */
    Integer getInformation(@Param("studentId") Long studentId, @Param("type") Integer type);

    List<Gauntlet> selGauntletByTypeAndChallengeType(@Param("type") Integer type, @Param("challengeType") Integer challengeType,
                                                     @Param("start") Integer start, @Param("end") Integer end, @Param("studentId") Long studentId,
                                                     @Param("time") String time);

    Integer getCountPkForMe(@Param("studentId") Long studentId, @Param("beStudentId") Long id, @Param("type") int type);

    Integer getCount(@Param("type") Integer type, @Param("challengeType") Integer challengeType, @Param("studentId") Long studentId, @Param("time") String time);

    Gauntlet getByStudentIdAndBeStudentId(@Param("studentId") Long studentId, @Param("beStudentId") Long id);

    Gauntlet getInformationById(Long gauntletId);

    List<Gauntlet> selByStudentId(@Param("studentId") Long studentId, @Param("start") Integer start, @Param("end") Integer end, @Param("type") Integer type);

    Integer selCountByStudentId(Long id);

    List<Gauntlet> selDelGauntlet(@Param("studentId") Long studentId, @Param("format") String date);

    void updateStatus(@Param("status") int status, @Param("gauntletIds") List<Long> gauntletIds);

    @Select("select count(id) from  gauntlet where be_challenger_student_id=#{studentId} and be_challenger_status=3 and challenger_point is not null")
    Integer selReceiveChallenges(@Param("studentId") Long studentId);

    @Update("update gauntlet set challenge_status=4 , be_challenger_status=4 where id =#{gauntletId}")
    Integer updateByStatus(@Param("gauntletId") Long gauntletId);

    @Update("update gauntlet set challenge_status=4 , be_challenger_status=4 where be_challenger_status=3 and create_time < #{time}")
    Integer updateByTime(String time);


    List<Gauntlet> selectStudy(@Param("type") int type, @Param("studentId") Long studentId);
}
