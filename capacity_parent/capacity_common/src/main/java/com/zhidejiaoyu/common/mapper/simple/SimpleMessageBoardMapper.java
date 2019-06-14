package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.MessageBoard;
import com.zhidejiaoyu.common.pojo.MessageBoardExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SimpleMessageBoardMapper extends BaseMapper<MessageBoard> {
    int countByExample(MessageBoardExample example);

    int deleteByExample(MessageBoardExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(MessageBoard record);

    List<MessageBoard> selectByExample(MessageBoardExample example);

    MessageBoard selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MessageBoard record, @Param("example") MessageBoardExample example);

    int updateByExample(@Param("record") MessageBoard record, @Param("example") MessageBoardExample example);

    int updateByPrimaryKeySelective(MessageBoard record);

    int updateByPrimaryKey(MessageBoard record);

    /**
     * 根据条件查找后台意见反馈列表展示信息
     *
     * @param account           学生账号
     * @param studentName       学生姓名
     * @param status            回复状态
     * @param feedBackTimeBegin 反馈起始时间
     * @param feedBackTimeEnd   反馈结束时间
     * @return
     */
    List<MessageBoard> selectMessageList(@Param("account") String account, @Param("studentName") String studentName,
                                         @Param("status") Integer status, @Param("feedBackTimeBegin") String feedBackTimeBegin,
                                         @Param("feedBackTimeEnd") String feedBackTimeEnd);

    /**
     * 获取反馈条数
     *
     * @param studentIds
     * @return key 学生id value：当前学生的总反馈条数
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Object>> selectFeedBackCount(@Param("studentIds") List<Long> studentIds);

    /**
     * 获取学生反馈信息的管理人员的反馈状态
     *
     * @param studentIds
     * @return key 学生id value：当前学生反馈信息的状态
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Object>> selectFeedBackStatus(@Param("studentIds") List<Long> studentIds);

    /**
     * 更新指定学生的禁言日期
     *
     * @param endDate 禁言结束日期
     * @param studentId 学生id
     */
    void updateStopSpeakTime(@Param("endDate") Date endDate, @Param("studentId") Long studentId);

    /**
     * 将当前学生的意见反馈置为已读状态
     *
     * @param stuId
     * @param flag 1：已读；2：未读；3:已回复
     */
    void updateReadFlag(@Param("stuId") Long stuId, @Param("flag") Integer flag);

    /**
     * 更新显示金币奖励提示状态
     *
     * @param studentId 学生id
     * @param flag 金币奖励状态 1：提示；2：不提示
     */
    void updateHintFlag(@Param("studentId") Long studentId, @Param("flag") int flag);

    /**
     * 查看学生的禁言状态
     *
     * @param stuId
     * @return
     */
    List<MessageBoard> selectStopSpeakTime(@Param("stuId") Long stuId);
}
