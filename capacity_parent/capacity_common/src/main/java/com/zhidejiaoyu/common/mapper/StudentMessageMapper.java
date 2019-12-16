package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.vo.studentMessage.StudentMessageListVo;
import com.zhidejiaoyu.common.pojo.StudentMessage;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 个人中心—》消息中心—》消息通知表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
public interface StudentMessageMapper extends BaseMapper<StudentMessage> {

    /**
     * 获取消息通知列表内容
     *
     * @param studentId
     * @return
     */
    List<StudentMessageListVo> selectMessageList(@Param("studentId") Long studentId);

    /**
     * 查看当前消息是否属于当前学生
     *
     * @param studentId
     * @param messageId
     * @return
     */
    StudentMessage selectByStudentIdAndMessageId(@Param("studentId") Long studentId, @Param("messageId") Integer messageId);

    /**
     * 更新消息状态
     *
     * @param messageId
     * @param state
     */
    @Update("update student_message set state = #{state} where id = #{messageId}")
    void updateMessageState(@Param("messageId") Integer messageId, @Param("state") int state);
}
