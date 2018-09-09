package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.common.mapper.AwardMapper;
import com.zhidejiaoyu.common.mapper.MedalMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.vo.AwardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 拔得头筹勋章计算<br>
 * 学生每次金币增加的的时候判断他的总金币数在学校内是不是最多的，
 * 如果是，将当前时间记录到 student 中的 schoolGoldFirstTime，并计算上个第一名距当前时间，根据时间给与相应的勋章奖励；
 *
 * <br>规则
 * <br>
 *     本校金币总数第一名保持一分钟，点亮LV1。保持一小时，点亮LV2。保持两小时，点亮LV3。保持一天，点亮LV4。保持一周，点亮LV5。
 *
 * @author wuchenxi
 * @date 2018/8/25
 */
@Component
public class CountMyGoldUtil {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private MedalMapper medalMapper;

    public void countMyGold(Student student) {

        List<Medal> children = medalMapper.selectChildrenIdByParentId(11);

        String schoolName = student.getSchoolName();
        // 查询本校最高的金币数的学生
        Student maxGoldStudent = studentMapper.selectMaxGoldForSchool(schoolName);
        if (maxGoldStudent != null) {
            double maxGold = maxGoldStudent.getSystemGold() + maxGoldStudent.getOfflineGold();
            double myGold = student.getSystemGold() + student.getOfflineGold();
            if (myGold == maxGold) {
                student.setSchoolGoldFirstTime(new Date());
            } else if (myGold > maxGold) {
                // 当前学生金币在本校第一名
                student.setSchoolGoldFirstTime(new Date());

                // 查询本校所有金币总和为 maxGold 的学生信息
                List<Student> students = studentMapper.selectMaxGoldForGold(schoolName, maxGold);
                // 查询未获取“拔得头筹”全部勋章的学生信息
                Map<String, Object> map = notGetModalStudents(students, children);

                updateAward(map, children, students);
            }
        } else {
            student.setSchoolGoldFirstTime(new Date());

        }
        studentMapper.updateByPrimaryKeySelective(student);
    }

    /**
     * 更新 拔得头筹 勋章奖励信息
     *  @param map
     * @param children
     * @param students
     */
    private void updateAward(Map<String, Object> map, List<Medal> children, List<Student> students) {
        final int[] totalCount = {1, 60, 120, 1440, 10080};
        List<Student> notGetModalStudents = (List<Student>) map.get("students");
        notGetModalStudents.parallelStream().forEach(student -> {
            int completeCount = 0;
            // 学生上次第一名获取时间距当前时长
            long time = (System.currentTimeMillis() - student.getSchoolGoldFirstTime().getTime()) / 1000;
            for (long aTotalCount : totalCount) {
                if (time >= aTotalCount) {
                    completeCount++;
                }
            }
            Map<Long, List<Award>> studentAwards = (Map<Long, List<Award>>) map.get("studentAwards");
            List<Award> awards = studentAwards.get(student.getId());

            packageMedal(student, new ArrayList<>(), completeCount, totalCount, children, awards == null ? new ArrayList<>(0) : awards);
        });

        // 去掉金币排名不再是学校第一名学生的时间标识
        studentMapper.updateSchoolGoldFirstTimeToNull(students);
    }

    private void packageMedal(Student student, List<AwardVo> awardVos, int completeCount, int[] totalCount, List<Medal> children, List<Award> awards) {
        packageMedal(student, awardVos, completeCount,totalCount, children, awards, awardMapper);
    }

    public static void packageMedal(Student student, List<AwardVo> awardVos, int completeCount, int[] totalCount,  List<Medal> children,
                                    List<Award> awards, AwardMapper awardMapper) {
        List<Award> awardList = new ArrayList<>();
        Award award;
        AwardVo awardVo;
        Medal medal;
        int currentPlan = 0;
        boolean canGet;

        int awardSize = awards.size();
        int canGetCount = (int) awards.stream().filter(award1 -> award1.getCanGet() == 1).count();
        if (awardSize == 0) {
            int size = children.size();
            for (int i = 0; i < size; i++) {
                canGet = false;
                award = new Award();
                awardVo = new AwardVo();
                medal = children.get(i);

                award.setType(3);
                award.setStudentId(student.getId());
                if (completeCount >= totalCount[i]) {
                    award.setGetFlag(2);
                    award.setCanGet(1);

                    canGet = true;
                    currentPlan++;
                } else {
                    award.setGetFlag(2);
                    award.setCanGet(2);
                }
                award.setMedalType(medal.getId());
                award.setCreateTime(new Date());
                // 保存award获取其id
                awardMapper.insert(award);

                awardVo.setContent(medal.getMarkedWords());
                awardVo.setCurrentPlan(currentPlan * 1.0);
                awardVo.setTotalPlan(totalCount[i]);
                awardVo.setImgUrl(medal.getChildImgUrl());
                awardVo.setGetFlag(false);
                awardVo.setCanGet(canGet);
                awardVo.setId(award.getId());

                awardVos.add(awardVo);
            }
        } else {
            // 不是第一次查看勋章只需更新勋章是否可领取状态即可
            int size = children.size();
            for (int i = 0; i < size; i++) {
                awardVo = new AwardVo();
                medal = children.get(i);
                // 有需要更新的奖励数据
                award = awards.get(i);
                canGet = award.getCanGet() == 1;
                if (canGetCount < completeCount) {
                    if (award.getCanGet() != 1) {
                        award.setCanGet(1);
                        canGet = true;
                    }
                    awardList.add(award);
                }
                if (completeCount >= totalCount[i]) {
                    currentPlan++;
                }
                awardVo.setContent(medal.getMarkedWords());
                awardVo.setCurrentPlan(currentPlan * 1.0);
                awardVo.setTotalPlan(totalCount[i]);
                awardVo.setImgUrl(medal.getChildImgUrl());
                awardVo.setGetFlag(award.getGetFlag() == 1);
                awardVo.setCanGet(canGet);
                awardVo.setId(award.getId());

                awardVos.add(awardVo);
            }
            awardList.forEach(awardMapper::updateByPrimaryKeySelective);
        }
    }

    /**
     * 获取学生关于 拔得头筹 勋章的信息
     *
     * @param students
     * @param children
     * @return
     * <ul>
     *     <li>key:students 为获取 拔得头筹 全部勋章的学生集合</li>
     *     <li>key:studentAwards value:Map<Long, List<Award>>     每个学生对应的奖励信息</li>
     * </ul>
     */
    private Map<String, Object> notGetModalStudents(List<Student> students, List<Medal> children) {
        List<String> conditionList = new ArrayList<>();
        StringBuilder condition = new StringBuilder();
        students.forEach(student -> children.forEach(child -> {
            condition.setLength(0);
            condition.append("(student_id=").append(student.getId()).append(" and medal_type=").append(child.getId()).append(")");
            conditionList.add(condition.toString());
        }));

        List<Award> awards = awardMapper.selectMedalByStudentsIdAndMedalType(conditionList, children);
        int size = children.size();

        // 存放学生与获得 拔得头筹 勋章个数的对应关系
        Map<Long, Integer> countMap = new HashMap<>(16);
        awards.parallelStream().forEach(award -> {
            if (countMap.containsKey(award.getStudentId()) && award.getGetFlag() == 1) {
                countMap.put(award.getId(), countMap.get(award.getId() + 1));
            } else if (!countMap.containsKey(award.getStudentId())) {
                countMap.put(award.getStudentId(), 1);
            }
        });

        // 上次第一名的学生中未获取所有 拔得头筹 奖励的学生信息
        List<Student> notStudents = new ArrayList<>();
        students.parallelStream().forEach(student -> {
            if (!countMap.containsKey(student.getId()) || countMap.get(student.getId()) < size) {
                notStudents.add(student);
            }
        });

        Map<Long, List<Award>> studentAwards = awards.parallelStream().collect(Collectors.groupingBy(Award::getStudentId));

        Map<String, Object> map = new HashMap<>(16);
        map.put("students", notStudents);
        map.put("studentAwards", studentAwards);

        return map;
    }


}
