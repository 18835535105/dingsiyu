package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.AwardExample;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AwardMapper {
    int countByExample(AwardExample example);

    int deleteByExample(AwardExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Award record);

    int insertSelective(Award record);

    List<Award> selectByExample(AwardExample example);

    Award selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Award record, @Param("example") AwardExample example);

    int updateByExample(@Param("record") Award record, @Param("example") AwardExample example);

    int updateByPrimaryKeySelective(Award record);

    int updateByPrimaryKey(Award record);

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
     * 重置学生日奖励信息
     *
     * @return
     */
    int resetDayAward();

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

    @MapKey("id")
    Map<Long, Map<String, Long>> getMapKeyStudentXZ();

    /**
     * 获取当前班级的学生最新领取勋章的信息
     *
     * @param student
     * @return  niceName:学生姓名；medalNam:领取的勋章名
     */
    List<Map<String, String>> selectLatestMedalInClass(@Param("student") Student student);
}