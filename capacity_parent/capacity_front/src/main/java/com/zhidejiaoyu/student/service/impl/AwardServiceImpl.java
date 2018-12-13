package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.AwardService;
import com.zhidejiaoyu.student.utils.CountMyGoldUtil;
import com.zhidejiaoyu.student.vo.AwardVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * @author wuchenxi
 * @date 2018/6/9 18:22
 */
@Service
public class AwardServiceImpl implements AwardService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 当日完成日奖励个数，用于判断当日日奖励是否全部完成
     */
    private int completeDayAwardCount;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private RankListMapper rankListMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private AwardContentTypeMapper awardContentTypeMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    @Autowired
    private GameScoreMapper gameScoreMapper;

    @Autowired
    private OpenUnitLogMapper openUnitLogMapper;

    @Autowired
    private CountMyGoldUtil countMyGoldUtil;

    /**
     * 获取学生任务奖励信息
     *
     * @param session
     * @param type 1：日奖励；2：任务奖励；3：勋章
     * @return
     */
    @Override
    public ServerResponse<List<AwardVo>> getAwareInfo(HttpSession session, Integer type) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        List<AwardVo> awardVos = new ArrayList<>();
        switch (type) {
            case 1:
                getDailyAward(student, awardVos);
                break;
            case 2:
                getTaskAward(student, awardVos);
                break;
            case 3:
                getMedalAward(student, awardVos);
                break;
            default:
        }
        sortAwardVos(awardVos);
        return ServerResponse.createBySuccess(awardVos);
    }

    /**
     * 对奖励信息进行排序<br>
     * 将可领取的奖励排在头部，紧接着是已领取的奖励，然后是不可领取的奖励
     *
     * @param awardVos
     */
    private void sortAwardVos(List<AwardVo> awardVos) {
        List<AwardVo> canGet = new ArrayList<>();
        List<AwardVo> got = new ArrayList<>();
        List<AwardVo> noGet = new ArrayList<>();

        awardVos.forEach(awardVo -> {
            if (awardVo.getId() != null) {
                if (awardVo.getGetFlag()) {
                    got.add(awardVo);
                } else if (awardVo.getCanGet()) {
                    canGet.add(awardVo);
                } else {
                    noGet.add(awardVo);
                }
            }
        });

        awardVos.clear();
        awardVos.addAll(canGet);
        awardVos.addAll(got);
        awardVos.addAll(noGet);
    }

    /**
     * 获取勋章奖励信息
     *
     * @param student
     * @param awardVos
     */
    private void getMedalAward(Student student, List<AwardVo> awardVos) {
        //（一）初出茅庐: 任意课程单元下，首次完成‘慧记忆、慧听写、慧默写、例句听力、例句翻译’五个模块，依次点亮L1、L2、L3、L4、L5。
        this.inexperienced(student, awardVos);
        // （二）辉煌荣耀:当天学习效率首次达到50%，点亮L1。连续两次达到80%，点亮L2。连续十次达到90%，点亮L3。连续二十次达到90%，点亮L4。连续三十次达到90%，点亮L5。
        this.honour(student, awardVos);
        // (三）拔得头筹; 学生充值或兑换金币的的时候判断他的总金币数在学校内是不是最多的，如果是，将当前时间记录到 student 中的 schoolGoldFirstTime，并计算上个第一名距当前时间，根据时间给与相应的勋章奖励；
        this.manyGold(student, awardVos);

        //（四）天道酬勤:本校今日排行榜至少前进一名，点亮LV1。本校本周排行榜前进至少二十名，点亮LV2。本校本月排行榜前进至少三十名，点亮LV3。本校总排行榜今日排名比历史最低排名至少前进五十名，点亮LV4。全国周排行榜至少前进十名，点亮LV5。
        this.upLevel(student, awardVos);

        // （五）学霸崛起：所有测试首次满分，点亮LV1。连续五次，点亮LV2。连续十次，点亮LV3。连续十五次，点亮LV4。连续二十次，点亮LV5。
        this.superStudent(student, awardVos);

        // （六）如同儿戏(暂不做)
        likeGame(student, awardVos);

        //（七）百变星君：任一课程下所有单元‘慧记忆’历史闯关测试最高分较最低分相差20分，点亮LV1。其他‘慧听写、慧默写、例句听力、例句翻译’历史最高分较最低分相差30分，依次点亮LV2、LV3、LV4、LV5。
        this.changeMan(student, awardVos);

                /*
                    （八）小试牛刀：全部课程‘慧记忆’熟词累计10个，点亮LV1。累计50个，点亮LV2。累计100个，点亮LV3。累计300个，点亮LV4。累计1000个，点亮LV5。
                    （九）学习狂人：全部课程‘慧听写’熟词累计50个，点亮LV1。累计200个，点亮LV2。累计500个，点亮LV3。累计1000个，点亮LV4。累计2000个，点亮LV5。
                    （十）数一数二：全部课程‘慧默写’熟词累计50个，点亮LV1。累计200个，点亮LV2。累计500个，点亮LV3。累计1000个，点亮LV4。累计2000个，点亮LV5。
                    （十一）金榜题名：全部课程‘例句听力’熟句累计50个，点亮LV1。累计200个，点亮LV2。累计500个，点亮LV3。累计1000个，点亮LV4。累计2000个，点亮LV5。
                    （十二）出类拔萃：翻译句子累计50句，点亮LV1。累计200句，点亮LV2。累计500句，点亮LV3。累计1000句，点亮LV4。累计2000句，点亮LV5。
                    （十三）功成名就：默写句子累计50句，点亮LV1。累计200句，点亮LV2。累计500句，点亮LV3。累计1000句，点亮LV4。累计2000句，点亮LV5。
                */
        this.tryHand(student, awardVos, 1);
        this.tryHand(student, awardVos, 2);
        this.tryHand(student, awardVos, 3);
        this.tryHand(student, awardVos, 4);
        this.tryHand(student, awardVos, 5);
        this.tryHand(student, awardVos, 6);

        // （十四）勋章达人：点亮其他所有勋章方可获得。
        this.expertMan(student, awardVos);

        // （十五）女神勋章（男神勋章）：达到‘名列前茅’级后点亮
        this.godMan(student, awardVos);

        // 众望所归：被膜拜一次，点亮LV1。被膜拜十次，点亮LV2。被膜拜三十次，点亮LV3。被膜拜五十次，点亮LV4。被膜拜一百次，点亮LV5。
        this.enjoyPopularConfidence(student, awardVos);

        // （十七）至尊学霸:总排行榜首次第一名，点亮LV1。首次保持一周，点亮LV2。首次保持两周，点亮LV3。首次保持三周，点亮LV4。首次保持一个月，点亮LV5。
        // todo:学生充值或兑换金币的的时候判断他的总金币数在全国是不是最多的，如果是，将当前时间记录到 student 中的 countryGoldFirstTime，并计算上个第一名距当前时间，根据时间给与相应的勋章奖励；

        // （十八）值得元老：在线登录时长达一个月，点亮LV1。达五个月，点亮LV2。达一年，点亮LV3。达两年，点亮LV4。达三年，点亮LV5。
        this.oldMan(student, awardVos);

        // （十九）问鼎天下：被人膜拜总次数首次占据第一名，点亮LV1。连续一周，点亮LV2。连续两周，点亮LV3。连续三周，点亮LV4。连续一个月，点亮LV5。
        this.medalFirst(student, awardVos);

        // 最有潜力：所有测试成绩未及格总次数达到5次，点亮LV1。达到10次，点亮LV2。达到20次，点亮LV3。达到50次，点亮LV4。达到100次，点亮LV5。
        this.potentialMan(student, awardVos);
    }

    /**
     * 如同儿戏 第一次参与游戏互动，点亮LV1。胜利2次，点亮LV2。胜利5次，点亮LV3。胜利10次，点亮LV4。胜利50次，点亮LV5。（游戏专区 中的游戏）
     *
     * @param student
     * @param awardVos
     */
    private void likeGame(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(27);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        int[] totalPlan = {1, 2, 5, 10, 50};
        int canGetCount = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (canGetCount == children.size()) {
            // 该勋章可领取
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }
        int[] complete = new int[children.size()];
        List<GameScore> gameScores = gameScoreMapper.selectByExample(new GameScoreExample());
        int passCount = (int)gameScores.parallelStream().filter(gameScore -> gameScore.getPassFlag() == 1).count();
        int childrenSize = children.size();
        int gameScoreSize = gameScores.size();
        for (int i = 0; i < childrenSize; i++) {
            if (gameScoreSize > 1) {
                complete[0] = 1;
            } else if(passCount >= totalPlan[i]){
                complete[i] = totalPlan[i];
            } else if (gameScoreSize == 0) {
                complete[i] = 0;
            }
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 获取任务奖励信息
     *
     * @param student
     * @param awardVos
     */
    private void getTaskAward(Student student, List<AwardVo> awardVos) {
        // 获取任务奖励信息
        // 学生首次完善信息奖励
        this.completeInfoAward(student, awardVos);

        // 学生首次修改密码
        this.editPasswordAward(student, awardVos);

        // 游戏测试、课程测试全部一次性通过
        this.completeFrontTestAward(student, awardVos);

        // 凡是首次登陆正式账号的学生，在‘任务奖励’下都可领取200个金币。
        this.completeFirstLoginAward(student, awardVos);

        // 学生总有效学习时长奖励
        this.validTimeAward(student, awardVos);

        // 学生所有课程下单元闯关成功奖励
        this.unitSuccessAward(student, awardVos);

        // 六大模块下全部为熟词奖励
        this.knownWordAward(student, awardVos);
    }

    /**
     * 获取日奖励信息
     * @param student
     * @param awardVos
     */
    private void getDailyAward(Student student, List<AwardVo> awardVos) {
        completeDayAwardCount = 0;
        // 学生今日首次登录奖励
        todayFirstLogin(student, awardVos);

        // 学生当天已学单元数达到2个，奖励金币20个
        this.isTwoUnits(student, awardVos);

        // 学生当天完成10个单元闯关测试
        this.isTenUnitTest(student, awardVos);

        // 学生当天在‘测试中心’完成并通过10个测试（测试成绩大于等级80分），奖励金币10个。
        this.completeTestCenter(student, awardVos);

        // 学生当天分别在六大智能学习模块下新学50个熟词，奖励金币30个
        this.isFiftyKnownWord(student, awardVos);

        // 今日复习30个生词且记忆强度达50%
        this.isReviewThirtyUnknownWord(student, awardVos);

        // 学生当天全校排行榜上升10名以上，则奖励金币5个。（未上榜的同学也算）
        this.upTenRank(student, awardVos);

        // 学生当天所有日奖励全部完成，奖励金币50个
        this.completeDayAward(student, awardVos);
    }

    /**
     * 学生当日首次登录奖励信息
     *
     * @param student
     * @param awardVos
     */
    private void todayFirstLogin(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();
        Long studentId = student.getId();

        // 查看奖励表中是否有当前任务，如果没有，初始化该任务
        Award award = awardMapper.selectByAwardContentTypeAndType(studentId, 1, 1);
        award = packageAward(student, award, 1, 1);

        completeDayAwardCount++;
        packageAwardVo(awardVo, award, 1.0, true);
        awardVos.add(awardVo);
    }

    /**
     * 问鼎天下：
     * 被人膜拜总次数首次占据第一名，点亮LV1。
     * 连续一周，点亮LV2。
     * 连续两周，点亮LV3。
     * 连续三周，点亮LV4。
     * 连续一个月，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void medalFirst(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(92);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        int[] totalPlan = {1, 7, 14, 21, 30};
        int[] complete = new int[children.size()];
        if (awards.size() > 0) {
            for (int i = 0; i < awards.size(); i++) {
                if (awards.get(i).getCanGet() == 1) {
                    complete[i] = totalPlan[i];
                } else {
                    if (i > 0) {
                        complete[i] = complete[i - 1];
                    }
                }
            }
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 最有潜力：
     * 所有测试成绩未及格总次数达到5次，点亮LV1。
     * 达到10次，点亮LV2。
     * 达到20次，点亮LV3。
     * 达到50次，点亮LV4。
     * 达到100次，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void potentialMan(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(97);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        int[] totalPlan = {5, 10, 20, 50, 100};
        int canGetCount = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (canGetCount == children.size()) {
            // 该勋章可领取
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        int[] complete = new int[children.size()];

        // 测试不及格总次数
        int totalTestField = testRecordMapper.countTestFailByStudent(student);
        for (int i = 0; i < children.size(); i++) {
            if (totalTestField < totalPlan[i]) {
                complete[i] = totalTestField;
            } else {
                complete[i] = totalPlan[i];
            }
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 值得元老：
     * 在线登录时长达一个月，点亮LV1。
     * 达五个月，点亮LV2。
     * 达一年，点亮LV3。
     * 达两年，点亮LV4。
     * 达三年，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void oldMan(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(87);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        int[] totalPlan = {1, 5, 12, 24, 36};
        int canGetCount = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (canGetCount == children.size()) {
            // 该勋章可领取
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        int[] complete = new int[children.size()];

        // 在线总时长
        Long totalOnlineTime = durationMapper.countTotalOnlineTime(student);
        int totalMonth = totalOnlineTime == null ? 0 : (int) (totalOnlineTime / 60 / 60 / 24 / 30);

        for (int i = 0; i < children.size(); i++) {
            if (totalMonth < totalPlan[i]) {
                complete[i] = totalMonth;
            } else {
                complete[i] = totalPlan[i];
            }
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 众望所归：
     * 被膜拜一次，点亮LV1。
     * 被膜拜十次，点亮LV2。
     * 被膜拜三十次，点亮LV3。
     * 被膜拜五十次，点亮LV4。
     * 被膜拜一百次，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void enjoyPopularConfidence(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(77);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        int[] totalPlan = {1, 10, 30, 50, 100};
        int canGetCount = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (canGetCount == children.size()) {
            // 该勋章可领取
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        int[] complete = new int[children.size()];

        // 被膜拜次数
        WorshipExample worshipExample = new WorshipExample();
        worshipExample.createCriteria().andStudentIdByWorshipEqualTo(student.getId());
        int byWorshipCount = worshipMapper.countByExample(worshipExample);

        for (int i = 0; i < children.size(); i++) {
            if (byWorshipCount >= totalPlan[i]) {
                complete[i] = totalPlan[i];
            } else {
                complete[i] = byWorshipCount;
            }
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 女神勋章（男神勋章）：
     * 达到‘名列前茅’级后点亮
     *
     * @param student
     * @param awardVos
     */
    private void godMan(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(72);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        int[] totalPlan = {1};

        if (awards.size() > 0 && awards.get(0).getCanGet() == 1) {
            // 该勋章可领取
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        int[] complete = new int[1];
        double totalGold = student.getSystemGold() + student.getOfflineGold();
        Level level = levelMapper.selectByPrimaryKey(13L);
        if (totalGold >= level.getGold()) {
            // 可领取勋章
            complete[0] = 1;
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 勋章达人：
     * 点亮其他所有勋章方可获得。
     *
     * @param student
     * @param awardVos
     */
    private void expertMan(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(67);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        int[] totalPlan = {1};

        if (awards.size() > 0 && awards.get(0).getCanGet() == 1) {
            // 该勋章可领取
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        // 已领取的勋章的个数
        AwardExample awardExample = new AwardExample();
        awardExample.createCriteria().andStudentIdEqualTo(student.getId()).andTypeEqualTo(3).andGetFlagEqualTo(1)
                .andGetTimeIsNotNull();
        int getCount = awardMapper.countByExample(awardExample);
        // 勋章总个数
        int totalMedalCount = medalMapper.countByExample(new MedalExample());
        int[] complete = new int[1];
        if (getCount == totalMedalCount - 1) {
            // 可以领取
            complete[0] = 1;
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 小试牛刀：
     * 全部课程‘慧记忆’熟词累计100个，点亮LV1。
     * 累计500个，点亮LV2。
     * 累计1000个，点亮LV3。
     * 累计5000个，点亮LV4。
     * 累计10000个，点亮LV5。
     * <p>
     * 学习狂人：
     * 全部课程‘慧听写’熟词累计50个，点亮LV1。
     * 累计200个，点亮LV2。
     * 累计500个，点亮LV3。
     * 累计1000个，点亮LV4。
     * 累计2000个，点亮LV5。
     * <p>
     * 数一数二：
     * 全部课程‘慧默写’熟词累计50个，点亮LV1。
     * 累计200个，点亮LV2。
     * 累计500个，点亮LV3。
     * 累计1000个，点亮LV4。
     * 累计2000个，点亮LV5。
     * <p>
     * 金榜题名：
     * 全部课程‘例句听力’熟句累计50个，点亮LV1。
     * 累计200个，点亮LV2。
     * 累计500个，点亮LV3。
     * 累计1000个，点亮LV4。
     * 累计2000个，点亮LV5。
     * <p>
     * 出类拔萃：
     * 翻译句子累计50句，点亮LV1。
     * 累计200句，点亮LV2。
     * 累计500句，点亮LV3。
     * 累计1000句，点亮LV4。
     * 累计2000句，点亮LV5。
     * <p>
     * 功成名就：
     * 默写句子累计50句，点亮LV1。
     * 累计200句，点亮LV2。
     * 累计500句，点亮LV3。
     * 累计1000句，点亮LV4。
     * 累计2000句，点亮LV5。
     *
     * @param student
     * @param awardVos
     * @param type     1:小试牛刀；2：学习狂人；3：数一数二；4：金榜题名；5：出类拔萃；6：功成名就
     */
    private void tryHand(Student student, List<AwardVo> awardVos, int type) {
        // 查看当前学生所有课程“慧记忆”模块的熟词个数，相同单词算一个，所有课程下该单词是
        int knownCount = learnMapper.countKnownCountByStudentId(student, type);
        int[] totalPlan = new int[0];
        List<Medal> children = new ArrayList<>();
        switch (type) {
            case 1:
                totalPlan = new int[]{10, 50, 100, 300, 1000};
                children = medalMapper.selectChildrenIdByParentId(37);
                break;
            case 2:
                totalPlan = new int[]{5, 50, 100, 300, 1000};
                children = medalMapper.selectChildrenIdByParentId(42);
                break;
            case 3:
                totalPlan = new int[]{5, 50, 100, 300, 1000};
                children = medalMapper.selectChildrenIdByParentId(47);
                break;
            case 4:
                totalPlan = new int[]{10, 50, 100, 500, 1000};
                children = medalMapper.selectChildrenIdByParentId(52);
                break;
            case 5:
                totalPlan = new int[]{10, 50, 100, 500, 1000};
                children = medalMapper.selectChildrenIdByParentId(57);
                break;
            case 6:
                totalPlan = new int[]{5, 10, 20, 50, 100};
                children = medalMapper.selectChildrenIdByParentId(62);
                break;
            default:
        }

        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        // 奖励表中当前勋章可获取的个数
        int count = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (count == children.size()) {
            // 当前勋章都已经点亮
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        int[] complete = new int[children.size()];

        for (int i = 0; i < children.size(); i++) {
            if (knownCount < totalPlan[i]) {
                complete[i] = knownCount;
            } else {
                complete[i] = totalPlan[i];
            }
        }

        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 百变星君：
     * 任一课程下所有单元‘慧记忆’历史闯关测试最高分较最低分相差20分，点亮LV1。
     * 其他‘慧听写、慧默写、例句听力、例句翻译’历史最高分较最低分相差30分，依次点亮LV2、LV3、LV4、LV5。
     *
     * @param student
     * @param awardVos
     */
    private void changeMan(Student student, List<AwardVo> awardVos) {

        // 查看当前学生所有单元的个数
        int unitCount = studentUnitMapper.countUnitCountByStudentId(student, null);
        int[] totalPlan = {unitCount, unitCount, unitCount, unitCount, unitCount};

        List<Medal> children = medalMapper.selectChildrenIdByParentId(32);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        // 奖励表中当前勋章可获取的个数
        int count = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();

        if (count == children.size()) {
            // 当前勋章都已经点亮
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }


        // 学生各个模块完成的单元数
        Map<Long, Map<Long, Object>> completeMap = testRecordMapper.countCompleteByStudentId(student);


        int[] complete = new int[children.size()];

        Map<Long, Object> map = completeMap.get(student.getId());

        complete[0] = parseInt(map.get("memory").toString());
        complete[1] = parseInt(map.get("listen").toString());
        complete[2] = parseInt(map.get("write").toString());
        complete[3] = parseInt(map.get("sentenceListen").toString());
        complete[4] = parseInt(map.get("sentenceTranslate").toString());

        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }


    /**
     * 学霸崛起：包括
     * 所有测试首次满分，点亮LV1。
     * 连续五次，点亮LV2。
     * 连续十次，点亮LV3。
     * 连续十五次，点亮LV4。
     * 连续二十次，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void superStudent(Student student, List<AwardVo> awardVos) {
        List<Medal> children = medalMapper.selectChildrenIdByParentId(23);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        // 根据测试时间查找学生测试记录
        TestRecordExample testRecordExample = new TestRecordExample();
        testRecordExample.createCriteria().andStudentIdEqualTo(student.getId());
        testRecordExample.setOrderByClause("test_end_time ASC");
        List<TestRecord> testRecords = testRecordMapper.selectByExample(testRecordExample);

        // 各个勋章点亮需要的总进度
        int[] totalPlan = {1, 5, 10, 15, 20};
        // 完成进度
        int[] complete = new int[children.size()];

        // 奖励表中当前勋章可获取的个数
        int count = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();

        if (count == children.size()) {
            // 当前勋章都已经点亮
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        TestRecord testRecord;
        if (testRecords.size() > 0) {
            // 连续测试满分个数
            int continuousCount = 0;
            for (int i = 0; i < testRecords.size(); i++) {
                testRecord = testRecords.get(i);

                // 是否是首次获得满分
                boolean firstFull = (i == 0 || continuousCount == 0) && testRecord.getPoint() == 100;
                // 是否是连续获得满分
                boolean successionFull = false;
                if (i > 0) {
                    successionFull = testRecord.getPoint() == 100 && testRecords.get(i - 1).getPoint() == 100;
                }
                if (firstFull) {
                    continuousCount++;
                } else if (successionFull) {
                    continuousCount++;
                } else {
                    continuousCount = 0;
                }
                if (continuousCount == 1) {
                    complete[0] = 1;
                }
                if (continuousCount >= 1 && continuousCount <= 5) {
                    complete[1] = continuousCount;
                }
                if (continuousCount >= 1 && continuousCount <= 10) {
                    complete[2] = continuousCount;
                }
                if (continuousCount >= 1 && continuousCount <= 15) {
                    complete[3] = continuousCount;
                }
                if (continuousCount >= 1 && continuousCount <= 20) {
                    complete[4] = continuousCount;
                }
            }
        }
        this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
    }

    /**
     * 封装awardVos
     *
     * @param awards
     * @param children
     * @param awardVos
     * @param totalPlan
     */
    private void packageOrderAwardVo(List<Award> awards, List<Medal> children, List<AwardVo> awardVos, int[] totalPlan) {
        AwardVo awardVo;
        Award award;
        Medal medal;
        for (int i = 0; i < awards.size(); i++) {
            award = awards.get(i);
            medal = children.get(i);
            awardVo = new AwardVo();
            awardVo.setImgUrl(medal.getChildImgUrl());
            awardVo.setGetFlag(award.getGetFlag() == 1);
            awardVo.setId(award.getId());
            awardVo.setTotalPlan(totalPlan[i]);
            awardVo.setCanGet(award.getCanGet() == 1);
            awardVo.setContent(medal.getMarkedWords());
            awardVo.setCurrentPlan(totalPlan[i] * 1.0);
            awardVos.add(awardVo);
        }
    }

    /**
     * 天道酬勤:本校今日排行榜至少前进一名，点亮LV1。
     * 本校本周排行榜前进至少二十名，点亮LV2。
     * 本校本月排行榜前进至少三十名，点亮LV3。
     * 本校总排行榜今日排名比历史最低排名至少前进五十名，点亮LV4。
     * 全国周排行榜至少前进十名，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void upLevel(Student student, List<AwardVo> awardVos) {
        RankListExample rankListExample = new RankListExample();
        rankListExample.createCriteria().andStudentIdEqualTo(student.getId());
        List<RankList> rankLists = rankListMapper.selectByExample(rankListExample);

        List<Medal> children = medalMapper.selectChildrenIdByParentId(17);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        // 用于存储某些具体完成的任务
        int[] complete = new int[children.size()];
        // 某些任务的总进度
        int[] totalPlan = {1, 20, 30, 50, 10};

        // 用于存储当前奖励是否已经可领取，如果可领取不再重新计算当前奖励
        Map<Integer, Boolean> map = new HashMap<>(16);
        if (awards.size() == 0) {
            for (int i = 0; i < 5; i++) {
                map.put(i, false);
            }
        } else {
            for (int i = 0; i < awards.size(); i++) {
                Award award = awards.get(i);
                if (award.getCanGet() == 1) {
                    map.put(i, true);
                } else {
                    map.put(i, false);
                }
            }
        }

        long count = awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (count == children.size()) {
            // 当前勋章所有任务都已完成
            this.packageOrderAwardVo(awards, children, awardVos, totalPlan);
            return;
        }

        if (rankLists.size() > 0) {
            RankList rankList = rankLists.get(0);
            Integer schoolDayRank = rankList.getSchoolDayRank();
            Integer schoolWeekRank = rankList.getSchoolWeekRank();
            Integer schoolMonthRank = rankList.getSchoolMonthRank();
            Integer countryWeekRank = rankList.getCountryWeekRank();
            Integer schoolLowestRank = rankList.getSchoolLowestRank();

            // 学生当前全校排名
            Map<Long, Map<String, Object>> currentSchoolRank = studentMapper.selectLevelByStuId(student, 2);
            // 学生当前全国排名
            Map<Long, Map<String, Object>> currentCountryRank = studentMapper.selectLevelByStuId(student, 3);

            double schoolRank = 0;
            if (currentSchoolRank != null && currentSchoolRank.get(student.getId()) != null && currentSchoolRank.get(student.getId()).get("rank") != null) {
                schoolRank = Double.parseDouble(currentSchoolRank.get(student.getId()).get("rank").toString());
            }

            double countryRank = 0;
            if (currentCountryRank != null && currentCountryRank.get(student.getId()) != null && currentCountryRank.get(student.getId()).get("rank") != null) {
                countryRank = Double.parseDouble(currentCountryRank.get(student.getId()).get("rank").toString());
            }

            if (schoolDayRank - schoolRank >= 1) {
                complete[0] = 1;
            }
            if (schoolWeekRank - schoolRank >= 20) {
                complete[1] = 20;
            } else {
                complete[1] = (int) (schoolWeekRank - schoolRank);
            }
            if (schoolMonthRank - schoolRank >= 30) {
                complete[2] = 30;
            } else {
                complete[2] = (int) (schoolMonthRank - schoolRank);
            }
            if (schoolRank - schoolLowestRank >= 50) {
                complete[3] = 50;
            } else {
                complete[4] = (int) (schoolRank - schoolLowestRank);
            }
            if (countryWeekRank - countryRank >= 10) {
                complete[4] = 10;
            } else {
                complete[4] = (int) (countryWeekRank - countryRank);
            }

            this.packageOrderMedal(student, awardVos, complete, children, awards, totalPlan);
        }

    }

    /**
     * 封装 awardVos，适用于勋章获取顺序是无序的，前一个勋章未获取也不影响后一个勋章的获取
     *
     * @param student   学生信息
     * @param awardVos  勋章列表展示对象
     * @param complete  已完成进度
     * @param children  任务总进度
     * @param awards    奖励对象
     * @param totalPlan 获取每个勋章需要的总进度
     */
    private void packageOrderMedal(Student student, List<AwardVo> awardVos, int[] complete, List<Medal> children, List<Award> awards, int[] totalPlan) {
        Award award;
        Medal medal;
        AwardVo awardVo;

        if (awards.size() == 0) {
            for (int i = 0; i < children.size(); i++) {
                award = new Award();
                awardVo = new AwardVo();
                medal = children.get(i);

                award.setType(3);
                award.setStudentId(student.getId());

                StudentInfoServiceImpl.setAwardState(complete, totalPlan, award, i);
                award.setMedalType(medal.getId());
                // 保存award获取其id
                awardMapper.insert(award);

                awardVo.setContent(medal.getMarkedWords());
                awardVo.setCurrentPlan(complete[i] > 0 ? complete[i] * 1.0 : 0.0);
                awardVo.setTotalPlan(totalPlan[i]);
                awardVo.setImgUrl(medal.getChildImgUrl());
                awardVo.setGetFlag(false);
                awardVo.setCanGet(award.getCanGet() == 1);
                awardVo.setId(award.getId());

                awardVos.add(awardVo);
            }
        } else {
            // 不是第一次查看勋章只需更新勋章是否可领取状态即可
            for (int i = 0; i < children.size(); i++) {
                awardVo = new AwardVo();
                medal = children.get(i);
                // 有需要更新的奖励数据
                award = awards.get(i);
                if (award.getCanGet() != 1 && complete[i] == totalPlan[i]) {
                    award.setCanGet(1);
                    awards.add(award);
                }

                awardVo.setContent(medal.getMarkedWords());
                awardVo.setCurrentPlan(complete[i] * 1.0);
                awardVo.setTotalPlan(totalPlan[i]);
                awardVo.setImgUrl(medal.getChildImgUrl());
                awardVo.setGetFlag(award.getGetFlag() == 1);
                awardVo.setCanGet(award.getCanGet() == 1);
                awardVo.setId(award.getId());

                awardVos.add(awardVo);
            }
            awards.forEach(item -> awardMapper.updateByPrimaryKeySelective(item));
        }
    }

    /**
     * 拔得头筹：本校金币总数第一名保持一分钟，点亮LV1。保持一小时，点亮LV2。保持两小时，点亮LV3。保持一天，点亮LV4。保持一周，点亮LV5。
     *
     * @param student
     * @param awardVos
     */
    private void manyGold(Student student, List<AwardVo> awardVos) {
        final int[] totalCount = {1, 60, 120, 1440, 10080};
        List<Medal> children = medalMapper.selectChildrenIdByParentId(11);
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        this.packageMedal(student, awardVos, awards.size(), totalCount, children, awards);
    }

    /**
     * 初出茅庐: 任意课程单元下，首次完成‘慧记忆、慧听写、慧默写、例句听力、例句翻译’五个模块，依次点亮L1、L2、L3、L4、L5。
     *
     * @param student
     * @param awardVos
     */
    private void inexperienced(Student student, List<AwardVo> awardVos) {

        final int[] total = {1, 2, 3, 4, 5};

        // 辉煌荣耀子勋章id
        List<Medal> children = medalMapper.selectChildrenIdByParentId(1);

        // 获取勋章奖励信息
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);
        // 学生所有课程下学习的模块
        List<String> studyModels = learnMapper.selectLearnedModelByStudent(student);

        // 第一次查看勋章
        packageMedal(student, awardVos, studyModels.size(), total, children, awards);
    }

    /**
     * 辉煌荣耀:当天学习效率首次达到50%，点亮L1。连续两次达到80%，点亮L2。连续十次达到90%，点亮L3。连续二十次达到90%，点亮L4。连续三十次达到90%，点亮L5。
     *
     * @param student
     * @param awardVos
     */
    private void honour(Student student, List<AwardVo> awardVos) {
        // 辉煌荣耀子勋章id
        List<Medal> children = medalMapper.selectChildrenIdByParentId(6);

        // 获取勋章奖励信息
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        // 查询学生当日的有效时长和在线时长
        List<Map<String, Object>> durationList = durationMapper.selectValidTimeAndOnlineTime(student);
        // 学习效率集合
        List<Double> efficiencyList = new ArrayList<>();

        durationList.forEach(map -> efficiencyList.add(BigDecimalUtil.div(Double.parseDouble(map.get("validTime").toString()),
                Double.parseDouble(map.get("onlineTime").toString()), 2)));

        // 连续的达到指定学习效率的次数;counts[0]:当天学习效率首次达到50%；counts[1]:连续两次达到80%；counts[2]:连续十次达到90%；counts[3]:连续二十次达到90%；counts[4]:连续三十次达到90%；
        final int[] counts = new int[children.size()];
        // 每个勋章需要的总进度
        final int[] totalPlan = {1, 2, 10, 20, 30};
        efficiencyList.forEach(efficiency -> {
            // 如果首次学习效率达到50%，counts[0] + 1；效率再次达到50%,counts[0]数值不再变化
            if (efficiency >= 0.2 && counts[0] < 1) {
                counts[0]++;
                if (counts[0] >= totalPlan[0]) {
                    counts[0] = totalPlan[0];
                }
            }
            // 学习效率每次达到80%并且连续次数小于2时，counts[1]+1，如果在连续2次达到80%之前其中有一次效率小于80%，将counts[1]置为0，重新计数；其余情况类推
            if (efficiency >= 0.3 && counts[1] < 2) {
                counts[1]++;
                if (counts[1] >= totalPlan[1]) {
                    counts[1] = totalPlan[1];
                }
            } else if (counts[1] < 2) {
                counts[1] = 0;
            }
            if (efficiency >= 0.4 && counts[2] < 10) {
                counts[2]++;
                if (counts[2] >= totalPlan[2]) {
                    counts[2] = totalPlan[2];
                }
            } else if (counts[2] < 10) {
                counts[2] = 0;
            }
            if (efficiency >= 0.5 && counts[3] < 20) {
                counts[3]++;
                if (counts[3] >= totalPlan[3]) {
                    counts[3] = totalPlan[3];
                }
            } else if (counts[3] < 20) {
                counts[3] = 0;
            }
            if (efficiency >= 0.6 && counts[4] < 30) {
                counts[4]++;
                if (counts[4] >= totalPlan[4]) {
                    counts[4] = totalPlan[4];
                }
            } else if (counts[4] < 30) {
                counts[4] = 0;
            }
        });

        this.packageOrderMedal(student, awardVos, counts, children, awards, totalPlan);
    }

    /**
     * 封装勋章信息，用于勋章是顺序获取的，前一个勋章没有获取后一个勋章获取条件不可能达到；
     * 或对勋章顺序没有要求，比如“初出茅庐”的提示语，无论先完成那个模块该提示语都适用
     *
     * @param student       当前学生信息
     * @param awardVos      需要展示的奖励vo
     * @param completeCount 学生完成的任务的个数
     * @param children
     * @param awards
     */
    private void packageMedal(Student student, List<AwardVo> awardVos, int completeCount, int[] totalCount, List<Medal> children, List<Award> awards) {
        CountMyGoldUtil.packageMedal(student, awardVos, completeCount, totalCount, children, awards, awardMapper);
    }

    /**
     * 学生当天在‘测试中心’完成并通过10个测试（测试成绩大于等级80分），奖励金币10个
     *
     * @param student
     * @param awardVos
     */
    private void completeTestCenter(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 5);
        award = this.packageAward(student, award, 1, 5);

        // 查看当天学生通过的测试中心测试个数
        int completeCount = testRecordMapper.selectTodayCompleteTestCenter(student);
        int ten = 10;
        if (completeCount >= ten) {
            // 完成
            completeDayAwardCount++;
            packageAwardVo(awardVo, award, 10.0, true);
            award.setCanGet(1);
        } else {
            packageAwardVo(awardVo, award, completeCount * 1.0, false);
            award.setCanGet(2);
        }
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 学生当天学校排名上升10个名次，奖励5金币
     *
     * @param student
     * @param awardVos
     */
    private void upTenRank(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 8);
        award = this.packageAward(student, award, 1, 8);

        // 查询学生昨天的学校排名
        RankListExample rankListExample = new RankListExample();
        rankListExample.createCriteria().andStudentIdEqualTo(student.getId());
        List<RankList> rankLists = rankListMapper.selectByExample(rankListExample);
        if (rankLists.size() > 0) {
            RankList rankList = rankLists.get(0);
            // 查询学生当前学校排名
            Map<Long, Map<String, Object>> schoolLevelMap = studentMapper.selectLevelByStuId(student, 2);
            if (schoolLevelMap.get(student.getId()) == null || schoolLevelMap.get(student.getId()).get("rank") == null) {
                log.error("学生[{}]-[{}]暂无学校排行数据！", student.getId(), student.getStudentName());
                return;
            }
            int nowLevel = parseInt(schoolLevelMap.get(student.getId()).get("rank").toString().split("\\.")[0]);
            int upLevel = rankList.getSchoolDayRank() - nowLevel;
            int ten = 10;
            if (upLevel >= ten) {
                // 完成任务
                completeDayAwardCount++;
                packageAwardVo(awardVo, award, 10.0, true);
                award.setCanGet(1);
            } else {
                if (upLevel <= 0) {
                    packageAwardVo(awardVo, award, 0.0, false);
                } else {
                    packageAwardVo(awardVo, award, upLevel * 1.0, false);
                }
                award.setCanGet(2);
            }
        }

        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 六大模块下全部为熟词奖励
     *
     * @param student
     * @param awardVos
     */
    private void knownWordAward(Student student, List<AwardVo> awardVos) {
        int[] counts = {50, 100, 200, 400, 800, 1600, 3200, 4000};
        // 起始id
        int startCount = 195;

        // 查看学生在所有课程下熟词数量
        int count = learnMapper.countKnownWordByStudentId(student.getId());
        packageAwardVos(student, awardVos, counts, startCount, count);
    }

    /**
     * 学生所有课程下单元闯关成功奖励
     *
     * @param student
     * @param awardVos
     */
    private void unitSuccessAward(Student student, List<AwardVo> awardVos) {
        int[] counts = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};

        //课程下单元闯关成功一类的奖励起始id
        int startCount = 105;

        // 查看学生所有课程下单元闯关成功个数
        int count = testRecordMapper.countUnitTestSuccessByStudentId(student.getId());
        packageAwardVos(student, awardVos, counts, startCount, count);
    }

    private void packageAwardVos(Student student, List<AwardVo> awardVos, int[] counts, int startCount, int count) {
        AwardVo awardVo;
        Award award;
        for (int count1 : counts) {
            awardVo = new AwardVo();
            award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, startCount);
            award = this.packageAward(student, award, 2, startCount);

            if (count < count1) {
                packageAwardVo(awardVo, award, count * 1.0, false);
                award.setCanGet(2);
            } else {
                packageAwardVo(awardVo, award, count1 * 1.0, true);
                award.setCanGet(1);
            }
            awardMapper.updateByPrimaryKeySelective(award);
            awardVos.add(awardVo);
            startCount++;
        }
    }

    /**
     * 学生有效时长奖励
     *
     * @param student
     * @param awardVos
     */
    private void validTimeAward(Student student, List<AwardVo> awardVos) {
        int[] counts = {60, 120, 240, 480, 960, 1920, 3840, 7680, 15360, 30720};
        // 有效时长一类的奖励起始id
        int startCount = 15;

        // 学生总有效时长
        Long totalSecond = durationMapper.countTotalValidTime(student.getId());
        int count = 0;
        if (totalSecond != null) {
           count = (int) (totalSecond / 60);
        }

        AwardVo awardVo;
        Award award;
        for (int count1 : counts) {
            awardVo = new AwardVo();
            award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, startCount);
            award = this.packageAward(student, award, 2, startCount);

            if (count >= count1) {
                packageAwardVo(awardVo, award, count1 * 1.0, true);
                award.setCanGet(1);
            } else {
                packageAwardVo(awardVo, award, count * 1.0, false);
                award.setCanGet(2);
            }

            awardMapper.updateByPrimaryKeySelective(award);
            awardVos.add(awardVo);

            startCount++;
        }
    }

    /**
     * 有效期是365天的学生能够领取该奖励
     *
     * @param student
     * @param awardVos
     */
    private void completeFirstLoginAward(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, 14);
        award = this.packageAward(student, award, 2, 14);

        if (student.getRank() == 365) {
            packageAwardVo(awardVo, award, 1.0, true);
            award.setCanGet(1);
        } else {
            packageAwardVo(awardVo, award, 0.0, false);
            award.setCanGet(2);
        }
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 游戏测试、课程测试全部一次性通过
     *
     * @param student
     * @param awardVos
     */
    private void completeFrontTestAward(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        int complete = 0;

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, 13);
        award = this.packageAward(student, award, 2, 13);

        // 查询游戏测试通过次数
        TestRecord gameTest = testRecordMapper.selectByGenre(student.getId(), "学前游戏测试");
        if (gameTest != null && gameTest.getPoint() >= 80) {
            complete++;
        }

        // 查询一次性通过的课程测试,如果pointList中有大于或等于80分的数据，说明有一次通过的课程测试
        List<Integer> pointList = testRecordMapper.selectCourseTestMinPoint();
        for (Integer integer : pointList) {
            if (integer >= 80) {
                complete++;
                break;
            }
        }

        Double currentPlan = complete * 1.0 / 2;

        if (currentPlan == 1) {
            packageAwardVo(awardVo, award, currentPlan, true);
            award.setCanGet(1);
        } else {
            packageAwardVo(awardVo, award, currentPlan, false);
            award.setCanGet(2);
        }
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 学生首次修改密码奖励
     *
     * @param student
     * @param awardVos
     */
    private void editPasswordAward(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();
        double currentPlan = 1.0;
        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, 12);
        packageAwardVo(awardVo, award, currentPlan, true);
        awardVos.add(awardVo);
    }

    /**
     * 学生首次完善信息奖励
     *
     * @param student
     * @param awardVos
     */
    private void completeInfoAward(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, 10);
        if (award == null) {
            award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 2, 11);
        }
        packageAwardVo(awardVo, award, 1.0, true);
        award.setCanGet(1);
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 学生当天所有日奖励全部完成，奖励金币50个
     *
     * @param student
     * @param awardVos
     */
    private void completeDayAward(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 9);
        award = this.packageAward(student, award, 1, 9);

        AwardContentType awardContentType = awardContentTypeMapper.selectByPrimaryKey(9);
        // 学生当天完成的奖励个数
        int totalDailyAward = awardContentType.getTotalPlan();
        if (completeDayAwardCount == totalDailyAward) {
            packageAwardVo(awardVo, award, 8.0, true);
            award.setCanGet(1);
        } else {
            packageAwardVo(awardVo, award, completeDayAwardCount * 1.0, false);
            award.setCanGet(2);
        }
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 今日复习30个生词且记忆强度达50%
     *
     * @param student
     * @param awardVos
     */
    private void isReviewThirtyUnknownWord(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 7);
        award = this.packageAward(student, award, 1, 7);

        // 查询当日学生在慧记忆，慧听写，慧默写三个模块复习的生词总数
        int count = learnMapper.countTodayLearnedUnknownWord();
        if (count >= 30) {
            completeDayAwardCount++;
            packageAwardVo(awardVo, award, 3.0, true);
            award.setCanGet(1);
        } else {
            packageAwardVo(awardVo, award, count * 1.0, false);
            award.setCanGet(2);
        }
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 学生当天分别在六大智能学习模块下新学50个熟词
     *
     * @param student
     * @param awardVos
     */
    private void isFiftyKnownWord(Student student, List<AwardVo> awardVos) {

        AwardVo awardVo = new AwardVo();

        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 6);
        award = this.packageAward(student, award, 1, 6);

        // 查询当日学生在慧记忆，慧听写，慧默写三个模块学习的熟词总数
        int count = learnMapper.countTodayLearnedKnownWord();
        if (count >= 50) {
            completeDayAwardCount++;
            packageAwardVo(awardVo, award, 50.0, true);
            award.setCanGet(1);
        } else {
            packageAwardVo(awardVo, award, count * 1.0, false);
            award.setCanGet(2);
        }
        awardMapper.updateByPrimaryKeySelective(award);
        awardVos.add(awardVo);
    }

    /**
     * 领取奖励
     *
     * @param session
     * @param awareId 奖励id
     * @param getType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> getAware(HttpSession session, Long awareId, Integer getType) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Award award = awardMapper.selectByIdAndStuId(awareId, student.getId());
        Medal medal = null;
        String msg = null;
        String awardType = null;
        String awardContent = null;
        if (award != null && award.getGetFlag() == 2) {
            if (getType == 1) {
                // 领取金币奖励
                AwardContentType awardContentType = awardContentTypeMapper.selectByPrimaryKey(award.getAwardContentType());
                if (awardContentType != null) {
                    awardContent = awardContentType.getAwardContent();
                    Integer awardGold = awardContentType.getAwardGold();
                    // 金币奖励
                    awardType = award.getType() == 1 ? "日奖励" : "任务奖励";
                    msg = "id 为" + student.getId() + " 的学生 " + student.getStudentName() + " 在 "
                            + DateUtil.DateTime(new Date()) + " 领取了 " + awardType + " 下 " + awardContent + " 的 #" + awardGold + "# 个金币";
                    // 更新学生金币信息
                    student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
                    try {
                        studentMapper.updateByPrimaryKeySelective(student);
                    } catch (Exception e) {
                        log.error("id为 {} 的学生在领取 {} 中 {}  奖励时更新学生金币信息出错", student.getId(), awardType, awardContent, e);
                        return ServerResponse.createByErrorMessage("更新学生金币信息出错!");
                    }
                    // 保存领取奖励日志
                    RunLog runLog = new RunLog(student.getId(), 4, msg, new Date());
                    runLog.setUnitId(student.getUnitId());
                    runLog.setCourseId(student.getCourseId());
                    try {
                        runLogMapper.insert(runLog);
                        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                    } catch (Exception e) {
                        log.error("id为 {} 的学生在领取 {} 中 {} 奖励时保存日志出错！", student.getId(), awardType, awardContent, e);
                    }
                }
            } else {
                // 领取勋章奖励
                medal = medalMapper.selectByPrimaryKey(award.getMedalType());
                awardType = "勋章";
                awardContent = medal.getMarkedWords();
                msg = "id 为" + student.getId() + " 的学生 " + student.getStudentName() + " 在 "
                        + DateUtil.DateTime(new Date()) + " 领取了勋章 \""+medal.getParentName() +"-" + medal.getChildName()+"\" #" + medal.getChildImgUrl() + "# ";
                RunLog runLog = new RunLog(student.getId(), 7, msg, new Date());
                runLog.setCourseId(student.getCourseId());
                runLog.setUnitId(student.getUnitId());
                try {
                    runLogMapper.insert(runLog);
                } catch (Exception e) {
                    log.error("id为 {} 的学生在领取勋章 {}-{} 时保存日志出错！", student.getId(), medal.getParentName(), medal.getChildName(), e);
                }
            }
            log.info(msg);
            // 更新奖励领取状态
            award.setGetFlag(1);
            award.setGetTime(new Date());
            try {
                awardMapper.updateByPrimaryKeySelective(award);
            } catch (Exception e) {
                log.error("id为 {} 的学生在领取 {} 中 {}  奖励时更新奖励领取状态信息出错", student.getId(), awardType, awardContent, e);
                return ServerResponse.createByErrorMessage("更新奖励领取状态信息出错!");
            }
            countMyGoldUtil.countMyGold(student);
            if (getType == 2) {
                return ServerResponse.createBySuccess(medal.getParentName());
            }
            return ServerResponse.createBySuccessMessage("领取成功！");

        }
        return ServerResponse.createByErrorMessage("未查询到当前奖励信息！");
    }

    /**
     * 学生当天完成10个单元闯关测试，奖励金币10个
     * 学生当天闯关成功10个单元闯关测试（测试成绩大于等于80分），奖励金币30个
     *
     * @param student
     * @param awardVos
     */
    private void isTenUnitTest(Student student, List<AwardVo> awardVos) {
        AwardVo voSuccess = new AwardVo();
        AwardVo vo = new AwardVo();
        // 判断学生今日是否闯关成功10个单元闯关测试
        int successCount = testRecordMapper.countUnitTest(student.getId(), 1);

        // 闯关成功10个单元
        Award awardSuccess = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 4);
        awardSuccess = packageAward(student, awardSuccess, 1, 4);

        // 闯关10个单元
        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), 1, 3);
        award = packageAward(student, award, 1, 3);

        if (successCount >= 10) {
            completeDayAwardCount += 2;
            packageAwardVo(voSuccess, awardSuccess, 10.0, true);
            awardSuccess.setCanGet(1);
            packageAwardVo(vo, award, 10.0, true);
            award.setCanGet(1);
            awardMapper.updateByPrimaryKeySelective(awardSuccess);
            awardMapper.updateByPrimaryKeySelective(award);
        } else {
            packageAwardVo(voSuccess, awardSuccess, successCount * 1.0, false);
            // 判断学生今日是否完成10个单元闯关测试
            int completeCount = testRecordMapper.countUnitTest(student.getId(), 2);
            if (completeCount >= 10) {
                completeDayAwardCount++;
                packageAwardVo(vo, award, 10.0, true);
                award.setCanGet(1);
            } else {
                packageAwardVo(vo, award, completeCount * 1.0, false);
                award.setCanGet(2);
            }
            awardMapper.updateByPrimaryKeySelective(award);
        }
        awardVos.add(vo);
        awardVos.add(voSuccess);
    }

    /**
     * 封装AwardVo
     *
     * @param vo
     * @param award       奖励对象
     * @param currentPlan 当前进度
     * @param canGet      是否可领取
     */
    private void packageAwardVo(AwardVo vo, Award award, Double currentPlan, Boolean canGet) {
        AwardContentType awardContentType = awardContentTypeMapper.selectByPrimaryKey(award.getAwardContentType());
        vo.setGetFlag(award.getGetFlag() == 1);
        vo.setId(award.getId());
        vo.setCanGet(canGet);
        vo.setContent(awardContentType.getAwardContent());
        vo.setCurrentPlan(currentPlan);
        vo.setTotalPlan(awardContentType.getTotalPlan());
        vo.setGold(awardContentType.getAwardGold());
        vo.setImgUrl(awardContentType.getImgUrl());

        // 如果当前奖励可获取，将奖励可获取时间置为当前时间
        if (canGet && award.getCreateTime() == null) {
            award.setCreateTime(new Date());
            awardMapper.updateByPrimaryKeySelective(award);
        }
    }

    /**
     * 封装奖励信息
     *
     * @param student          当前学生
     * @param award            奖励信息
     * @param type             奖励类型：1：日奖励，2：任务奖励:3：勋章
     * @param awardContentType 奖励内容id
     */
    private Award packageAward(Student student, Award award, int type, int awardContentType) {
        if (award == null) {
            award = new Award();
            award.setGetFlag(2);
            award.setAwardContentType(awardContentType);
            award.setStudentId(student.getId());
            award.setType(type);
            awardMapper.insert(award);
        }
        return award;
    }

    /**
     * 判断学生当天已学单元数
     *
     * @param student
     * @param awardVos
     */
    private void isTwoUnits(Student student, List<AwardVo> awardVos) {
        AwardVo awardVo = new AwardVo();

        Long studentId = student.getId();

        // 查看奖励表中是否有当前任务，如果没有，初始化该任务
        Award award = awardMapper.selectByAwardContentTypeAndType(studentId, 1, 2);
        award = packageAward(student, award, 1, 2);

        // 查看学生今日开启单元个数
        int openCount = openUnitLogMapper.countTodayOpenCount(student.getId());
        // 学生今日刚好学习完两个单元,否则就是为学习完成两个单元或者完成多于两个单元
        if (openCount >= 2) {
            // 日奖励完成个数 + 1
            completeDayAwardCount++;
            packageAwardVo(awardVo, award, 2 * 1.0, true);
        } else {
            packageAwardVo(awardVo, award, openCount * 1.0, false);
        }
        awardVos.add(awardVo);
    }
}
