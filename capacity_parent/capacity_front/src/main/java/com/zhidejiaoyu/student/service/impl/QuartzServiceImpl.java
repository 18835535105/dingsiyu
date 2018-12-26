package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.service.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Calendar;

/**
 * @author wuchenxi
 * @date 2018/6/8 16:24
 */
@Service
public class QuartzServiceImpl implements QuartzService {

    private Logger logger = LoggerFactory.getLogger(QuartzService.class);

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private RankListMapper rankListMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private StudentWorkDayMapper studentWorkDayMapper;

    /**
     * 学生升级，每年8月25日 23:59 分学生由低年级升至高年级(已废弃）
     */
    @Transactional(rollbackFor = Exception.class)
    // @Scheduled(cron = "59 59 23 25 8 ?")
    @Override
    public void studentUpgrade() {
        logger.info("定时任务 -> 学生升级 开始执行...");
        // 查询所有学生的信息
        List<Student> students = studentMapper.selectIdAndGradeAndVersion();
        for (Student student : students) {
            String grade = student.getGrade();
            switch (grade) {
                case "七年级":
                    student.setGrade("八年级");
                    break;
                case "八年级":
                    student.setGrade("九年级");
                    break;
                case "九年级":
                    student.setGrade("高一");
                    // 删除学生的初中课程信息
                    studentUnitMapper.deleteByStudentId(student.getId());
                    // 重新为学生推送高中课程，原学习记录、测试记录等保留
                    commonMethod.initUnit(student);
                    break;
                case "高一":
                    student.setGrade("高二");
                    break;
                case "高二":
                    student.setGrade("高三");
                    break;
                default:
            }
            logger.info("学生 {} -> {} 由 {} 升级至 {};", student.getId(), student.getStudentName(), grade, student.getGrade());
        }
        studentMapper.updateByPrimarykeys(students);
        logger.info("定时任务 -> 学生升级 执行完成.");
    }

    /**
     * 每日 00:10:00 更新提醒消息中学生账号到期提醒
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 10 0 * * ?")
    @Override
    public void updateNews() {
        logger.info("定时任务 -> 更新提醒消息中学生账号到期提醒 开始执行...");

        // 对距离有效期还剩3天的学生进行消息提醒
        // 查询小于等于3天到达有效期的学生
        List<Student> students = studentMapper.selectAccountTimeLessThreeDays();
        List<Long> ids = new ArrayList<>();
        for (Student student : students) {
            ids.add(student.getId());
        }

        // 根据学生id查询消息
        List<News> newsList = newsMapper.selectByStuIds(ids);

        // key:studentId    value:news
        Map<Long, News> map = new HashMap<>(16);
        for (News news : newsList) {
            map.put(news.getStudentid(), news);
        }

        List<News> updateList = new ArrayList<>();
        List<News> insertList = new ArrayList<>();
        News news;
        for (Student student : students) {
            if (map.containsKey(student.getId())) {
                // 提醒消息已存在，更新
                news = map.get(student.getId());
                news.setTitle(this.getDay(student.getAccountTime()));
                news.setTime(new Date());
                updateList.add(news);
            } else {
                // 消息不存在，新增
                news = new News();
                news.setTime(new Date());
                news.setTitle(this.getDay(student.getAccountTime()));
                news.setContent("亲爱的用户，你的账户即将在" + DateUtil.formatYYYYMMDD(student.getAccountTime()) + "到期,请及时续费，否则将对您产生无法登陆平台的影响，请知晓。");
                news.setStudentid(student.getId());
                news.setType("提醒消息");
                news.setRobotspeak("我们还会再见面了吗？在不续费我们就挥手再见了。");
                news.setRead(2);
                insertList.add(news);
            }
        }

        // 更新消息
        if (updateList.size() > 0) {
            try {
                newsMapper.updateByList(updateList);
            } catch (Exception e) {
                logger.error("批量修改学生有效期倒计时提醒消息出错！", e);
            }
        }

        // 新增消息
        if (insertList.size() > 0) {
            try {

                newsMapper.insertList(insertList);
            } catch (Exception e) {
                logger.error("批量增加学生账号有效期到期提醒消息出错", e);
            }
        }
        logger.info("定时任务 -> 更新提醒消息中学生账号到期提醒 执行完成.");
    }

    public static void main(String[] args) {
        System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 每天 00:30:00 更新学生全校日排行记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 30 0 * * ?")
    @Override
    public void updateRank() {
        logger.info("定时任务 -> 更新学生全校日排行记录 开始执行...");

        // 全校日排行
        int rank = 0;
        // 全校周排行
        int weekRank = 0;
        // 全校月排行
        int monthRank = 0;
        // 全国周排行
        int countryRank = 0;
        // 全校日排行是否发生变化
        boolean schoolDayRankIsChange;
        // 全校/全国周排行是否发生变化
        boolean weekRankIsChange;
        // 全校月排行是否发生变化
        boolean monthRankIsChange;
        Student student;
        RankList updateRankList;
        RankList insertRankList;
        double currentGold;
        double preGold;

        // 判断今天是不是周一
        boolean isMonday = false;
        final int FIRST_DAY_OF_MONTH = 1;
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            isMonday = true;
        }

        // 判断今天是不是每月的1号
        boolean beginMonth = false;
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == FIRST_DAY_OF_MONTH) {
            beginMonth = true;
        }

        // 根据省市区学校和总金币数降序查找所有学生信息,用于学校排行
        StudentExample studentExample = new StudentExample();
        StudentExample.Criteria criteria = studentExample.createCriteria();
        criteria.andPetNameIsNotNull().andPetNameNotEqualTo("").andProvinceIsNotNull().andProvinceNotEqualTo("")
                .andCityIsNotNull().andCityNotEqualTo("").andAreaIsNotNull().andAreaNotEqualTo("").andSchoolNameIsNotNull()
                .andSchoolNameNotEqualTo("");
        studentExample.setOrderByClause("province DESC,city DESC,area DESC,school_name DESC,system_gold + offline_gold DESC");
        List<Student> students = studentMapper.selectByExample(studentExample);

        int size = students.size();

        // 查询所有学生的排行榜信息
        Map<Long, RankList> rankListMap = rankListMapper.selectRankListMap();

        List<RankList> insertList = new ArrayList<>();
        List<RankList> updateList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            student = students.get(i);

            //计算学生全国周排行
            countryRank = getCountryRank(countryRank, student, isMonday, students, i);

            if (rankListMap.containsKey(student.getId())) {
                updateRankList = rankListMap.get(student.getId());
                // 学生排行已存在
                currentGold = student.getSystemGold() + student.getOfflineGold();
                if (i == 0) {
                    rank++;
                    if (isMonday) {
                        // 周一，更新全国和全校周排行
                        weekRank++;
                    }
                    if (beginMonth) {
                        // 每月1号，更新全校月排行
                        monthRank++;
                    }
                } else if (student.getArea().equals(students.get(i - 1).getArea()) && student.getSchoolName().equals(students.get(i - 1).getSchoolName())) {
                    // 是同一所学校
                    preGold = students.get(i - 1).getSystemGold() + students.get(i - 1).getOfflineGold();
                    // 与上个同学总金币相同，名次相同,不同名次累加
                    if (currentGold != preGold) {
                        rank++;
                        if (isMonday) {
                            // 周一，更新全校周排行
                            weekRank++;
                        }
                        if (beginMonth) {
                            monthRank++;
                        }
                    }
                } else {
                    // 如果不是同一所学校排名从1开始
                    rank = 1;
                    if (isMonday) {
                        weekRank = 1;
                    }
                    if (beginMonth) {
                        monthRank = 1;
                    }
                }

                schoolDayRankIsChange = updateRankList.getSchoolDayRank() != rank;
                weekRankIsChange = isMonday && (updateRankList.getCountryWeekRank() != countryRank || updateRankList.getSchoolWeekRank() != weekRank);
                monthRankIsChange = beginMonth && updateRankList.getSchoolMonthRank() != monthRank;
                if (schoolDayRankIsChange || weekRankIsChange || monthRankIsChange) {
                    // 说明学生排行发生变化
                    if (rank < updateRankList.getSchoolLowestRank()) {
                        // 说明当前学生全校排行名次低于该学生学校最低排行，将最低排行更新为当前学校日排行
                        updateRankList.setSchoolLowestRank(rank);
                    }
                    updateRankList.setSchoolDayRank(rank);
                    if (isMonday) {
                        updateRankList.setSchoolWeekRank(weekRank);
                        updateRankList.setCountryWeekRank(countryRank);
                    }
                    if (beginMonth) {
                        updateRankList.setSchoolMonthRank(monthRank);
                    }
                    updateList.add(updateRankList);
                }
            } else {
                // 学生排行不存在需要新增数据
                if (i == 0 || (student.getArea().equals(students.get(i - 1).getArea()) && student.getSchoolName().equals(students.get(i - 1).getSchoolName()))) {
                    rank++;
                    weekRank++;
                    monthRank++;
                } else {
                    // 跟上个同学不是同一所学校
                    rank = 1;
                    weekRank = 1;
                    monthRank = 1;
                }

                insertRankList = new RankList();
                insertRankList.setSchoolDayRank(rank);
                insertRankList.setStudentId(student.getId());
                insertRankList.setCountryWeekRank(countryRank);
                insertRankList.setSchoolWeekRank(weekRank);
                insertRankList.setSchoolMonthRank(monthRank);
                insertRankList.setSchoolLowestRank(rank);
                insertList.add(insertRankList);
            }
        }

        if (insertList.size() > 0) {
            try {
                rankListMapper.insertList(insertList);
            } catch (Exception e) {
                logger.error("新增学校日排行出错！", e.getMessage());
                e.printStackTrace();
            }

        }

        if (updateList.size() > 0) {
            try {
                rankListMapper.updateList(updateList);
            } catch (Exception e) {
                logger.error("更新学校日排行出错！", e.getMessage());
                e.printStackTrace();
            }
        }
        logger.info("定时任务 -> 更新学生全校日排行记录 执行完成.");

    }

    private int getCountryRank(int countryRank, Student student, boolean isMonday, List<Student> students, int i) {
        if (!isMonday) {
            // 如果后一名学生的金币总数 ！= 前一名金币总数，名次+1
            if (i == 0 || (students.get(i - 1).getSystemGold() + students.get(i - 1).getOfflineGold()) != (student.getOfflineGold() + student.getSystemGold())) {
                countryRank++;
            }
        }
        return countryRank;
    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?")
    public void updateWordDay() {
        // 查询所有工作日结束日期小于当前日期的对象
        List<StudentWorkDay> studentWorkDays = studentWorkDayMapper.selectEndTimeLessThanNow();
        if (studentWorkDays.size() > 0) {
            List<StudentWorkDay> days = new ArrayList<>(studentWorkDays.size());
            StudentWorkDay studentWorkDay;
            String sevenDays = studentWorkDayMapper.selectAfterSevenDay();
            for (StudentWorkDay workDay : studentWorkDays) {
                studentWorkDay = new StudentWorkDay();
                studentWorkDay.setWorkDayEnd(sevenDays);
                studentWorkDay.setStudentId(workDay.getStudentId());
                studentWorkDay.setWorkDayBegin(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
                studentWorkDay.setId(workDay.getId());
                days.add(studentWorkDay);
            }
            try {
                studentWorkDayMapper.insertList(days);
            } catch (Exception e) {
                logger.error("更新学生工作日失败!", e);
            }

        }
    }

    /**
     * 计算时间差并转换为中文
     *
     * @param accountTime
     * @return
     */
    private String getDay(Date accountTime) {
        int value = (int) Math.ceil((accountTime.getTime() - System.currentTimeMillis()) * 1.0 / 86400000);
        switch (value) {
            case 0:
                return "【消息通知】 账号今天到期";
            case 1:
                return "【消息通知】 账号距离有效期还有一天";
            case 2:
                return "【消息通知】 账号距离有效期还有二天";
            case 3:
                return "【消息通知】 账号距离有效期还有三天";
            default:
        }
        return null;
    }

}
