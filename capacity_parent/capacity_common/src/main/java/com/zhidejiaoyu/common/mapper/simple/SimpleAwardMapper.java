package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.Vo.simple.AwardVo;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.AwardExample;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SimpleAwardMapper extends BaseMapper<Award> {
    int countByExample(AwardExample example);

    int updateByPrimaryKeySelective(Award record);

    /**
     * 根据奖励类型和奖励内容类型获取指定的学生任务奖励
     *
     * @param studentId
     * @param type             奖励类型：1：日奖励，2：任务奖励:3：勋章
     * @param awardContentType 奖励内容类型,详细类别在award_content_type表中
     * @return
     */
    Award selectByAwardContentTypeAndType(@Param("studentId") Long studentId, @Param("type") int type, @Param("awardContentType") int awardContentType);

    /**
     * 查询当前学生指定的奖励信息
     *
     * @param awareId 奖励id
     * @param stuId   学生id
     * @return
     */
    Award selectByIdAndStuId(@Param("awareId") Long awareId, @Param("stuId") Long stuId);

    /**
     * 根据学生信息和当前勋章子勋章查询当前勋章的领取情况
     *
     * @param student
     * @param children 子勋章对象
     * @return
     */
    List<Award> selectMedalByStudentIdAndMedalType(@Param("student") Student student, @Param("children") List<Medal> children);

    /**
     * 批量保存奖励信息
     *
     * @param awardList
     * @return
     */
    int insertList(@Param("awardList") List<Award> awardList);

    /**
     * 获取学生今天完成的日奖励信息
     *
     * @param studentId 学生id
     * @return
     */
    List<Award> selectDailyAwardByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学生指定勋章的获取情况
     *
     * @param conditionList 查询条件集合
     * @param children
     * @return
     */
    List<Award> selectMedalByStudentsIdAndMedalType(@Param("conditionList") List<String> conditionList, @Param("children") List<Medal> children);

    /**
     * 获取学生已获取的所有勋章图片url
     *
     * @param student
     * @return
     */
    List<String> selectMedalUrl(@Param("student") Student student);

    @MapKey("id")
    Map<Long, Map<String, Long>> getMapKeyStudentXZ();

    /**
     * 获取当前班级的学生最新领取勋章的信息
     *
     * @param student
     * @param date    查询指定日期
     * @return niceName:学生姓名；medalNam:领取的勋章名
     */
    List<Map<String, String>> selectLatestMedalInClass(@Param("student") Student student, @Param("date") Date date);

    /**
     * 统计各个学生已获取的勋章个数
     *
     * @return
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Long>> countMedalWithStudent();

    @MapKey("studentId")
    Map<Long, Map<String, Object>> selCountAwardByStudentIds(List<Long> studentIds);

    /**
     * 获取今天学生完成的日奖励个数
     *
     * @param student
     * @return
     */
    int countCompleteAllDailyAward(@Param("student") Student student);

    /**
     * 查询日奖励奖励信息
     *
     * @param student
     * @param type    奖励类型：1：日奖励，2：任务奖励:3：勋章
     * @return
     */
    List<AwardVo> selectAwardVos(@Param("student") Student student, @Param("type") int type);

    /**
     * 查询学生指定勋章奖励
     *
     * @param studentId
     * @param medalId
     * @return
     */
    Award selectByStudentIdAndMedalType(@Param("studentId") Long studentId, @Param("medalId") Long medalId);

    /**
     * 查询勋章奖励信息
     *
     * @param student
     * @return
     */
    List<AwardVo> selectMedalAwardVos(@Param("student") Student student);

    /**
     * 统计指定类型的奖励可获取数量
     *
     * @param student
     * @param type
     * @return
     */
    int countCanGet(@Param("student") Student student, @Param("type") int type);

    /**
     * 计算学生能够获取的所有勋章总个数
     *
     * @param studentId
     * @return
     */
    int countTotalMedal(@Param("studentId") Long studentId);

    /**
     * 计算学生已经领取的勋章个数
     *
     * @param studentId
     * @return
     */
    int countGetModel(@Param("studentId") Long studentId);

    List<Map<String,Object>> selAwardCountByStudentId(@Param("studentId") Long studentId);

    /**
     * 删除学生日奖励信息
     */
    void deleteDailyAward();
}
