package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.config.ServiceInfoUtil;
import com.zhidejiaoyu.student.service.simple.SimpleQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.Calendar;

/**
 * @author wuchenxi
 * @date 2018/6/8 16:24
 */
@Slf4j
@Service
public class SimpleQuartzServiceImpl implements SimpleQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleRankListMapper simpleRankListMapper;

    @Autowired
    private SimpleNewsMapper simpleNewsMapper;

    @Autowired
    private SimpleStudentRankMapper simpleStudentRankMapper;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private SimpleWorshipMapper worshipMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Autowired
    private SimpleGauntletMapper simpleGauntletMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SimpleLevelMapper simpleLevelMapper;

    /**
     * 每日 00:10:00 更新提醒消息中学生账号到期提醒
     */
    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 10 0 * * ?")
    @Override
    public void updateNews() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }

        log.info("定时任务 -> 更新提醒消息中学生账号到期提醒 开始执行...");

        // 对距离有效期还剩3天的学生进行消息提醒
        // 查询小于等于3天到达有效期的学生
        List<Student> students = simpleStudentMapper.selectAccountTimeLessThreeDays();
        List<Long> ids = new ArrayList<>();
        for (Student student : students) {
            ids.add(student.getId());
        }

        if (ids.size() == 0) {
            log.info("定时任务 -> 更新提醒消息中学生账号到期提醒 没有需要提醒的学生.");
            return;
        }

        // 根据学生id查询消息
        List<News> newsList = simpleNewsMapper.selectByStuIds(ids);

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
                news.setContent("亲爱的用户，你的账户即将在" + SimpleDateUtil.formatYYYYMMDD(student.getAccountTime()) + "到期,请及时续费，否则将对您产生无法登陆平台的影响，请知晓。");
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
                simpleNewsMapper.updateByList(updateList);
            } catch (Exception e) {
                log.error("批量修改学生有效期倒计时提醒消息出错！", e);
            }
        }

        // 新增消息
        if (insertList.size() > 0) {
            try {

                simpleNewsMapper.insertList(insertList);
            } catch (Exception e) {
                log.error("批量增加学生账号有效期到期提醒消息出错", e);
            }
        }
        log.info("定时任务 -> 更新提醒消息中学生账号到期提醒 执行完成.");
    }

    public static void main(String[] args) {
        System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleQuartzServiceImpl quart = new SimpleQuartzServiceImpl();
        quart.updateEnergy();
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 20 0 * * ?")
    @Override
    public void updateEnergy() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }
        log.info("定时任务 -> 能量清零...");
        simpleStudentMapper.updEnergyByAll();
        log.info("定时任务 -> 能量清零  执行完成...");
        log.info("定时任务 -> 教师创建学生清零...");
        simpleTeacherMapper.updateCreateStudentNumber();
        log.info("定时任务 -> 教师创建学生清零 执行完成...");
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 5 0 1 * ? ")
    @Override
    public void updateClassMonthRank() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }
        // 班级与学生对应关系
        List<Student> students = simpleStudentMapper.selectList(new EntityWrapper<Student>().isNotNull("account_time").gt("system_gold", 0));

        // 存放各个班级下所有学生信息
        Map<Long, List<Student>> studentClassMap = new HashMap<>(16);
        students.parallelStream().forEach(student -> {
            if (student != null) {
                List<Student> studentList = null;
                if (studentClassMap.containsKey(student.getClassId())) {
                    studentList = studentClassMap.get(student.getClassId());
                }
                if (studentList == null) {
                    studentList = new ArrayList<>();
                }
                studentList.add(student);
                studentClassMap.put(student.getClassId(), studentList);
            }
        });

        int size = students.size();

        // 更新学生班级金币月排行
        updateClassGoldMonthRank(studentClassMap, size);

        // 更新学生班级勋章总数排行榜
        updateClassMedalMonthRank(studentClassMap, size);

        // 更新学生被膜拜总数排行榜
        updateClassWorshipMonthRank(studentClassMap, size);
    }


    @Override
    public void updateStudentExpansion() {
        log.info("定时任务 -> 学习力重置...");
        List<StudentExpansion> studentExpansions = simpleStudentExpansionMapper.selectAll();
        for (StudentExpansion studentExpansion : studentExpansions) {
            //查询基础获取学习力
            Integer study = simpleLevelMapper.getStudyById(studentExpansion.getLevel());
            //查询发起挑战的胜利场次获取的学习力
            List<Gauntlet> gauntlets = simpleGauntletMapper.selectStudy(1, studentExpansion.getStudentId());
            for(Gauntlet gauntlet:gauntlets){
                if(gauntlet.getChallengeStudy()!=null){
                    study=study+gauntlet.getChallengeStudy();
                }
            }
            //获取发起挑战失败
            List<Gauntlet> gauntlets1 = simpleGauntletMapper.selectStudy(2, studentExpansion.getStudentId());
            for(Gauntlet gauntlet:gauntlets1){
                if(gauntlet.getChallengeStudy()!=null){
                    if(study-gauntlet.getChallengeStudy()>0){
                        study=study-gauntlet.getChallengeStudy();
                    }else{
                        study=0;
                    }
                }
            }
            //查询被发起挑战的胜利场次获取的学习力
            List<Gauntlet> gauntlets2 = simpleGauntletMapper.selectStudy(3, studentExpansion.getStudentId());
            for(Gauntlet gauntlet:gauntlets2){
                if(gauntlet.getBeChallengeStudy()!=null){
                    study=study+gauntlet.getBeChallengeStudy();
                }
            }
            //获取发起挑战失败
            List<Gauntlet> gauntlets3 = simpleGauntletMapper.selectStudy(4, studentExpansion.getStudentId());
            for(Gauntlet gauntlet:gauntlets3){
                if(gauntlet.getBeChallengeStudy()!=null){
                    if(study-gauntlet.getBeChallengeStudy()>0){
                        study=study-gauntlet.getBeChallengeStudy();
                    }
                }else{
                    study=0;
                }
            }
            studentExpansion.setStudyPower(study);
            simpleStudentExpansionMapper.updateById(studentExpansion);
        }
        log.info("定时任务 -> 学习力重置完成...");
    }

    /**
     * 更新学生被膜拜总数排行榜
     *
     * @param studentClassMap
     * @param size
     */
    private void updateClassWorshipMonthRank(Map<Long, List<Student>> studentClassMap, int size) {
        log.info("定时增加学生班级被膜拜次数排行信息开始。。。");
        Map<Long, Map<Long, Long>> studentWorshipCount = worshipMapper.countWorshipWithStudent();
        if (!sortStudentMedalRankAndWorshipRank(studentClassMap, size, studentWorshipCount, 3)) {
            log.error("定时增加学生班级被膜拜次数排行信息失败！");
            return;
        }

        log.info("定时增加学生班级被膜拜次数排行信息执行完成");
    }

    /**
     * 更新学生班级勋章总数排行榜
     *
     * @param studentClassMap
     * @param size            学生人数
     */
    private void updateClassMedalMonthRank(Map<Long, List<Student>> studentClassMap, int size) {
        log.info("定时增加学生班级勋章排行信息开始。。。");
        Map<Long, Map<Long, Long>> studentMedalCount = simpleAwardMapper.countMedalWithStudent();
        if (!sortStudentMedalRankAndWorshipRank(studentClassMap, size, studentMedalCount, 2)) {
            log.error("定时增加学生班级勋章排行信息失败！");
            return;
        }
        log.info("定时增加学生班级勋章排行信息执行完成");
    }

    private boolean sortStudentMedalRankAndWorshipRank(Map<Long, List<Student>> studentClassMap, int size, Map<Long, Map<Long, Long>> studentMedalCount, int type) {
        List<StudentRank> rankList = new ArrayList<>(size);

        final int[] rank = {1};
        final Date date = new Date();
        studentClassMap.forEach((classId, studentList) -> {

            // 存放当前班级学生 id 和勋章个数/被膜拜次数
            List<Map<String, Long>> studentMedalCountMap = new ArrayList<>();

            rank[0] = 1;
            studentList.forEach(student -> {
                Map<String, Long> map = new HashMap<>(16);
                if (studentMedalCount.get(student.getId()) != null && studentMedalCount.get(student.getId()).get("count") != null) {
                    map.put("studentId", student.getId());
                    map.put("medalCount", studentMedalCount.get(student.getId()).get("count"));
                    studentMedalCountMap.add(map);
                }
            });

            studentMedalCountMap.sort(Comparator.comparing(studentMedal -> studentMedal.get("medalCount")));
            Collections.reverse(studentMedalCountMap);
            studentMedalCountMap.forEach(map -> {

                StudentRank studentRank = new StudentRank();
                studentRank.setStudentId(map.get("studentId"));
                studentRank.setCreateTime(date);
                studentRank.setType(type);
                studentRank.setMyRank(rank[0]++);
                rankList.add(studentRank);
            });
        });

        try {
            simpleStudentRankMapper.insertList(rankList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新学生班级金币月排行
     *
     * @param studentClassMap
     * @param size            学生人数
     */
    private void updateClassGoldMonthRank(Map<Long, List<Student>> studentClassMap, int size) {
        log.info("定时增加学生班级金币月排行信息开始。。。");
        List<StudentRank> rankList = new ArrayList<>(size);
        final int[] rank = {1};
        final Date date = new Date();
        studentClassMap.forEach((classId, studentList) -> {
            rank[0] = 1;
            if (studentList != null && studentList.size() > 0) {
                studentList.sort(Comparator.comparing(Student::getSystemGold).reversed());
                studentList.forEach(student -> {
                    StudentRank studentRank = new StudentRank();
                    studentRank.setStudentId(student.getId());
                    studentRank.setCreateTime(date);
                    studentRank.setType(1);
                    studentRank.setMyRank(rank[0]++);
                    rankList.add(studentRank);
                });
            }
        });

        try {
            simpleStudentRankMapper.insertList(rankList);
        } catch (Exception e) {
            log.error("定时增加学生班级金币月排行信息失败！", e);
            return;
        }

        log.info("定时增加学生班级金币月排行信息执行完成");
    }


    /**
     * 每天 00:30:00 更新学生全校日排行记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 30 0 * * ?")
    @Override
    public void updateRank() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }
        log.info("定时任务 -> 更新学生全校日排行记录 开始执行...");

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
        // 全国日排行是否发生变化
        boolean countryDayRankIsChange;
        Student student;
        // 校管 id
        Integer schoolAdminId;
        RankList updateRankList;
        RankList insertRankList;
        double currentGold;
        double preGold = 0.0;

        // 全校日排行学校和排名的对应关系
        Map<Integer, Integer> schoolRankMap = new HashMap<>(16);
        // 全校周排行学校和排名的对应关系
        Map<Integer, Integer> schoolWeekRankMap = new HashMap<>(16);
        // 全校月排行学校和排名的对应关系
        Map<Integer, Integer> schoolMonthRankMap = new HashMap<>(16);
        // 同班级上一个学生的金币数
        Map<Integer, Double> preStudentGold = new HashMap<>(16);

        // 判断今天是不是周一
        boolean isMonday = false;
        int firstDayOfMonth = 1;
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            isMonday = true;
        }

        // 判断今天是不是每月的1号
        boolean beginMonth = false;
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == firstDayOfMonth) {
            beginMonth = true;
        }

        // 查询需要进行排行的学生信息
        List<Student> students = simpleStudentMapper.selectStudentList();

        // 将students按照金币总数降序排列
        students.sort((s1, s2) -> (int) ((s1.getSystemGold() + s1.getOfflineGold()) - (s2.getSystemGold() + s2.getOfflineGold())));
        Collections.reverse(students);
        Map<Long, Integer> countryRankMap = new HashMap<>(16);
        int size = students.size();

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                countryRank++;
            } else if ((students.get(i).getSystemGold() + students.get(i).getOfflineGold()) != ((students.get(i - 1).getSystemGold() + students.get(i - 1).getOfflineGold()))) {
                countryRank++;
            }
            countryRankMap.put(students.get(i).getId(), countryRank);
        }

        // 查询所有学生的排行榜信息
        Map<Long, RankList> rankListMap = simpleRankListMapper.selectRankListMap();

        // 每个学生对应的校管 id
        Map<Long, Map<Long, Integer>> studentSchoolAdminMap = simpleStudentMapper.selectStudentSchoolAdminMap(students);

        List<RankList> insertList = new ArrayList<>();
        List<RankList> updateList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            student = students.get(i);
            schoolAdminId = studentSchoolAdminMap.get(student.getId()) == null ? null : studentSchoolAdminMap.get(student.getId()).get("schoolAdminId");

            currentGold = student.getSystemGold() + student.getOfflineGold();
            if (i > 0) {
                if (preStudentGold.containsKey(schoolAdminId)) {
                    preGold = preStudentGold.get(schoolAdminId);
                } else {
                    preGold = 0;
                }
            }
            preStudentGold.put(schoolAdminId, currentGold);

            // 学生排行已存在
            if (rankListMap.containsKey(student.getId())) {
                updateRankList = rankListMap.get(student.getId());
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
                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                } else if (schoolRankMap.containsKey(schoolAdminId)) {
                    // 是同一所学校
                    rank = schoolRankMap.get(schoolAdminId);
                    weekRank = schoolWeekRankMap.get(schoolAdminId);
                    monthRank = schoolMonthRankMap.get(schoolAdminId);
                    // 与上个同学总金币相同，名次相同,不同名次累加
                    if (currentGold != preGold) {
                        rank++;
                        if (isMonday) {
                            // 周一，更新全校周排行
                            weekRank++;
                        }
                        if (beginMonth) {
                            // 每月1号，更新全校月排行
                            monthRank++;
                        }
                    }
                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                } else {
                    // 如果不是同一所学校排名从1开始
                    rank = 1;
                    if (isMonday) {
                        weekRank = 1;
                    }
                    if (beginMonth) {
                        monthRank = 1;
                    }
                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                }

                schoolDayRankIsChange = updateRankList.getSchoolDayRank() != rank;
                weekRankIsChange = isMonday && (!Objects.equals(updateRankList.getSchoolWeekRank(), schoolWeekRankMap.get(schoolAdminId))
                        || !Objects.equals(countryRankMap.get(student.getId()), updateRankList.getCountryWeekRank()));
                monthRankIsChange = beginMonth && !Objects.equals(schoolMonthRankMap.get(schoolAdminId), updateRankList.getSchoolMonthRank());
                countryDayRankIsChange = !Objects.equals(updateRankList.getCountryDayRank(), countryRankMap.get(student.getId()));

                if (schoolDayRankIsChange || weekRankIsChange || monthRankIsChange || countryDayRankIsChange) {
                    // 说明学生排行发生变化
                    updateRankList.setSchoolDayRank(rank);
                    updateRankList.setCountryDayRank(countryRankMap.get(student.getId()));
                    if (isMonday) {
                        updateRankList.setSchoolWeekRank(weekRank);
                        updateRankList.setCountryWeekRank(countryRank);
                    }
                    if (beginMonth) {
                        updateRankList.setSchoolMonthRank(monthRank);
                    }
                    // 如果学校日排行低于学生最低排行，更新学生最低排行信息
                    if (rank > updateRankList.getSchoolLowestRank()) {
                        updateRankList.setSchoolLowestRank(rank);
                    }
                    updateList.add(updateRankList);
                }
            } else {
                // 学生排行不存在需要新增数据
                if (i == 0 || schoolRankMap.containsKey(schoolAdminId)) {
                    rank = schoolRankMap.get(schoolAdminId) == null ? 0 : schoolRankMap.get(schoolAdminId);
                    weekRank = schoolWeekRankMap.get(schoolAdminId) == null ? 0 : schoolWeekRankMap.get(schoolAdminId);
                    monthRank = schoolMonthRankMap.get(schoolAdminId) == null ? 0 : schoolMonthRankMap.get(schoolAdminId);
                    if (currentGold != preGold) {
                        rank++;
                        weekRank++;
                        monthRank++;
                    }

                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                } else {
                    // 跟上个同学不是同一所学校
                    rank = 1;
                    weekRank = 1;
                    monthRank = 1;

                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                }

                insertRankList = new RankList();
                insertRankList.setSchoolDayRank(rank);
                insertRankList.setStudentId(student.getId());
                insertRankList.setCountryWeekRank(countryRankMap.get(student.getId()));
                insertRankList.setSchoolWeekRank(weekRank);
                insertRankList.setSchoolMonthRank(monthRank);
                insertRankList.setSchoolLowestRank(rank);
                insertRankList.setCountryDayRank(countryRankMap.get(student.getId()));
                insertList.add(insertRankList);
            }
        }

        if (insertList.size() > 0) {
            try {
                simpleRankListMapper.insertList(insertList);
            } catch (Exception e) {
                log.error("新增学校日排行出错！", e);
            }

        }

        if (updateList.size() > 0) {
            try {
                simpleRankListMapper.updateList(updateList);
            } catch (Exception e) {
                log.error("更新学校日排行出错！", e);
            }
        }
        log.info("定时任务 -> 更新学生全校日排行记录 执行完成.");

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

    @Override
    @Scheduled(cron = "0 0 2 * * ? ")
    public void deleteSessionMap() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }
        log.info("定时清除 sessionMap 开始");
        Set<Object> sessionMapFields = redisTemplate.opsForHash().keys(RedisKeysConst.SESSION_MAP);
        Set<Object> loginSessionFields = redisTemplate.opsForHash().keys(RedisKeysConst.LOGIN_SESSION);
        if (!sessionMapFields.isEmpty()) {
            redisTemplate.opsForHash().delete(RedisKeysConst.SESSION_MAP, sessionMapFields.toArray());
        }
        if (!loginSessionFields.isEmpty()) {
            redisTemplate.opsForHash().delete(RedisKeysConst.LOGIN_SESSION, loginSessionFields.toArray());
        }
        log.info("定时清除 sessionMap 完成");

        log.info("定时清理在线人数开始");
        Set<Object> members = redisTemplate.opsForZSet().range(RedisKeysConst.ZSET_ONLINE_USER, 0 , -1);
        if (members != null) {
            members.forEach(o -> redisTemplate.opsForZSet().remove(RedisKeysConst.ZSET_ONLINE_USER, o));
        }
        log.info("定时清理在线人数完成");
    }


    @Override
    @Scheduled(cron = "0 0 0 * * ? ")
    public void updateDailyAward() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }
        log.info("定时删除日奖励信息开始");
        simpleAwardMapper.deleteDailyAward();
        log.info("定时删除日奖励信息结束");
    }

    @Override
    @Scheduled(cron = "0 0 10 * * ? ")
    public void deleteDrawRedis() {
        int localPort = ServiceInfoUtil.getPort();
        if (port != localPort) {
            return;
        }
        redisOpt.delDrawRecord();
    }

}
