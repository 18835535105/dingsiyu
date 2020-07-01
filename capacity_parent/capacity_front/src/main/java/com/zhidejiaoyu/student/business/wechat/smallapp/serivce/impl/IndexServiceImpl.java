package com.zhidejiaoyu.student.business.wechat.smallapp.serivce.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Adsense;
import com.zhidejiaoyu.common.pojo.ClockIn;
import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.duration.DurationStudyModelUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.DurationInfoVO;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.StudyOverviewVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.impl.ShipIndexServiceImpl;
import com.zhidejiaoyu.student.business.wechat.smallapp.dto.PrizeDTO;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.IndexService;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.PrizeVO;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.TotalDataVO;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.index.AdsensesVO;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.index.CardVO;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.index.IndexVO;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Service("smallAppIndexService")
public class IndexServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements IndexService {

    @Resource
    private AdsenseMapper adsenseMapper;

    @Resource
    private ClockInMapper clockInMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private PrizeExchangeListMapper prizeExchangeListMapper;

    @Resource
    private RunLogMapper runLogMapper;

    @Resource
    private KnownWordsMapper knownWordsMapper;

    @Override
    public ServerResponse<Object> index(String openId) {
        Student student = ShipIndexServiceImpl.getStudentByOpenId(openId, studentMapper);

        // 头部公有数据
        TotalDataVO totalDataVO = this.getTotalVo(student);

        // 广告位
        List<AdsensesVO> adsensesVos = this.getAdsensesVOList(student);

        return ServerResponse.createBySuccess(IndexVO.builder()
                .adsenses(adsensesVos)
                .totalData(totalDataVO)
                .build());
    }

    public TotalDataVO getTotalVo(Student student) {
        return TotalDataVO.builder()
                .headImg(GetOssFile.getPublicObjectUrl(student.getHeadUrl()))
                .say(this.getSay())
                .studentName(student.getStudentName())
                .systemGold(String.valueOf(Math.round(student.getSystemGold())))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> replenish(String date, String openId) {

        Student student = ShipIndexServiceImpl.getStudentByOpenId(openId, studentMapper);

        String today = DateUtil.formatYYYYMMDD(new Date());
        if (Objects.equals(today, date)) {
            // 如果补打日期是今天，不让补卡
            return ServerResponse.createByErrorMessage("请前往执行飞行任务！");
        }

        // 补签需要消耗的金币数
        int reduceGold = 50;

        if (student.getSystemGold() < reduceGold) {
            return ServerResponse.createByError(402, "金币不足！");
        }

        Date now = new Date();
        int count = clockInMapper.countByStudentIdAndCardTime(student.getId(), date);
        if (count == 0) {
            clockInMapper.insert(ClockIn.builder()
                    .cardDays(0)
                    .cardTime(DateUtil.parse(date, DateUtil.YYYYMMDD))
                    .createTime(now)
                    .studentId(student.getId())
                    .type(2)
                    .build());

            student.setSystemGold(BigDecimalUtil.sub(student.getSystemGold(), reduceGold));
            student.setOfflineGold(BigDecimalUtil.add(student.getOfflineGold(), reduceGold));
            studentMapper.updateById(student);

            GoldLogUtil.saveReplenishGoldLog(student.getId(), "补签", reduceGold);

            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError(400, "选择的日期已打卡，无需再次打卡！");

    }

    @Override
    public ServerResponse<Object> record(String openId) {
        Student student = ShipIndexServiceImpl.getStudentByOpenId(openId, studentMapper);
        Long studentId = student.getId();

        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        // 查询有学习记录的在线时长与学习日期
        List<DurationInfoVO> durationInfoVos = durationMapper.selectLearnDateAndOnlineTime(studentId);


        List<String> learnDateList;
        if (CollectionUtils.isNotEmpty(durationInfoVos)) {
            learnDateList = durationInfoVos.stream().map(DurationInfoVO::getLearnDate).collect(Collectors.toList());
        } else {
            return ServerResponse.createBySuccess(new DurationInfoVO());
        }

        PageInfo<DurationInfoVO> pageInfo = new PageInfo<>(durationInfoVos);

        // 查询指定学习日期学习的模块
        List<DurationInfoVO> durationInfos = durationMapper.selectDurationInfos(studentId, learnDateList);

        List<DurationInfoVO> result = this.packageResultList(durationInfoVos, durationInfos);

        PageInfo<DurationInfoVO> resultPage = new PageInfo<>();
        resultPage.setTotal(pageInfo.getTotal());
        resultPage.setPages(pageInfo.getPages());
        resultPage.setList(result);

        PageVo pageVo = PageUtil.packagePage(resultPage);
        return ServerResponse.createBySuccess(pageVo);
    }

    @Override
    public ServerResponse<Object> prize(PrizeDTO dto) {

        Student student = ShipIndexServiceImpl.getStudentByOpenId(dto.getOpenId(), studentMapper);
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.selectBySchoolId(schoolAdminId, dto.getOrderField(), dto.getOrderBy());

        PageInfo<PrizeExchangeList> pageInfo = new PageInfo<>(prizeExchangeLists);

        List<PrizeVO> prizeVos = prizeExchangeLists.stream().map(prizeExchangeList -> PrizeVO.builder()
                .gold(prizeExchangeList.getExchangePrize())
                .imgUrl(GetOssFile.getPublicObjectUrl(prizeExchangeList.getPrizeUrl()))
                .name(prizeExchangeList.getPrize())
                .build())
                .collect(Collectors.toList());

        PageInfo<PrizeVO> returnPageInfo = new PageInfo<>();
        returnPageInfo.setTotal(pageInfo.getTotal());
        returnPageInfo.setList(prizeVos);
        returnPageInfo.setPages(pageInfo.getPages());

        PageVo pageVo = PageUtil.packagePage(returnPageInfo);
        return ServerResponse.createBySuccess(pageVo);
    }

    @Override
    public ServerResponse<Object> cardInfo(String openId) {
        Student student = studentMapper.selectByOpenId(openId);

        // 10天前的日期，10天内学生没有登陆过系统不允许打卡
        Date date = new Date();
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(date, 10);
        Long studentId = student.getId();
        int loginCount = runLogMapper.countLoginByLastDays(studentId, beforeDaysDate);

        // 查询学生今天是否已经打过卡，如果打过卡不允许再次打卡
        int todayCardCount = clockInMapper.countTodayInfoByStudentId(studentId);

        boolean canCard = true;
        String msg = "";
        if (loginCount == 0 || todayCardCount > 0) {
            canCard = false;
            msg = todayCardCount == 0 ? "您已经10天未进行学习，请登陆夺分系统学习才可以继续打卡。" : "您今天已经打卡，无需再次打卡！";
        }

        List<ClockIn> clockIns = clockInMapper.selectByStudentId(studentId);
        Integer cardDays = clockInMapper.selectLaseCardDays(studentId);

        return ServerResponse.createBySuccess(CardVO.builder()
                .cardDays(cardDays == null ? 0 : cardDays)
                .infos(clockIns.stream()
                        .map(clockIn -> DateUtil.formatDate(clockIn.getCardTime(), DateUtil.YYYYMMDD))
                        .collect(Collectors.toList()))
                .canCard(canCard)
                .msg(msg)
                .build());
    }

    @Override
    public ServerResponse<Object> recordOverview(String openId) {
        val student = studentMapper.selectByOpenId(openId);
        Long studentId = student.getId();

        Long validTime = durationMapper.selectTotalValidTimeByStudentId(studentId);
        if (validTime == null) {
            validTime = 0L;
        }

        Long onlineTime = durationMapper.selectTotalOnlineByStudentId(studentId);
        if (onlineTime == null) {
            onlineTime = 0L;
        }
        if (validTime >= onlineTime) {
            validTime = Math.round(onlineTime * 0.9);
        }

        Integer loginCount = runLogMapper.countLoginCountByStudentId(studentId);
        Integer count = knownWordsMapper.countByStudentId(studentId);

        return ServerResponse.createBySuccess(StudyOverviewVO.builder()
                .studyCount(loginCount)
                .totalOnlineTime(onlineTime)
                .totalValidTime(validTime)
                .wordCount(count)
                .build());
    }

    /**
     * @param durationInfoVos 需要展示的学习日期及各个日期的在线时长
     * @param durationInfos   各个日期具体的学习时长
     * @return
     */
    private List<DurationInfoVO> packageResultList(List<DurationInfoVO> durationInfoVos,
                                                   List<DurationInfoVO> durationInfos) {
        // 各个学习日期所有的具体学习时长记录
        Map<String, List<DurationInfoVO>> collect = durationInfos
                .stream()
                .collect(Collectors.groupingBy(DurationInfoVO::getLearnDate, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

        // 各个学习日期对应的总在线时长
        Map<String, List<DurationInfoVO>> stringListMap = durationInfoVos
                .stream()
                .collect(Collectors.groupingBy(DurationInfoVO::getLearnDate, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

        List<DurationInfoVO> result = new ArrayList<>(durationInfoVos.size());
        // 拼接学习模块
        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();
        collect.forEach((k, v) -> {
            sb.setLength(0);
            set.clear();
            v.forEach(durationInfoVO -> {
                String studyModelStr = DurationStudyModelUtil.getStudyModelStr(durationInfoVO.getStudyModel());
                if (StringUtils.isNotEmpty(studyModelStr)) {
                    set.add(studyModelStr);
                }
            });

            set.forEach(s -> sb.append(s).append(","));

            if (stringListMap.get(k) == null || stringListMap.get(k).get(0) == null || stringListMap.get(k).get(0).getOnlineTime() == null) {
                return;
            }

            String learnDate = getLearnDate(k);
            result.add(DurationInfoVO.builder()
                    .learnDate(learnDate)
                    .onlineTime(stringListMap.get(k).get(0).getOnlineTime() / 60)
                    .studyModelStr(sb.length() > 1 ? sb.toString().substring(0, sb.length() - 1) : "单词")
                    .build());
        });

        return result;
    }

    public static String getLearnDate(String k) {
        String[] split = k.split("-");

        return split[0] + "年" + split[1] + "月" + split[2] + "日";
    }

    /**
     * 获取广告位信息
     *
     * @param student
     * @return
     */
    public List<AdsensesVO> getAdsensesVOList(Student student) {
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        List<Adsense> adsenses = adsenseMapper.selectOrderByUpdateTimeLimitFive(schoolAdminId);

        List<AdsensesVO> adsensesVos = new ArrayList<>(adsenses.size());
        if (CollectionUtils.isNotEmpty(adsenses)) {
            adsensesVos = adsenses.stream().map(adsense -> AdsensesVO.builder()
                    .imgUrl(adsense.getImgUrl())
                    .toUrl(adsense.getToUrl())
                    .type(adsense.getType())
                    .build()).collect(Collectors.toList());
        }
        return adsensesVos;
    }

    /**
     * 问候语
     */
    private String getSay() {
        int hourOfDay = new DateTime().getHourOfDay();
        if (hourOfDay <= 12 && hourOfDay >= 6) {
            return "上午好";
        }
        if (hourOfDay < 6 || hourOfDay > 18) {
            return "晚上好";
        }
        return "下午好";
    }

    public static void main(String[] args) {

        System.out.println(String.format("%.2f", 99.991));
        System.out.println(String.format("%.2f", 100.019));
    }
}
