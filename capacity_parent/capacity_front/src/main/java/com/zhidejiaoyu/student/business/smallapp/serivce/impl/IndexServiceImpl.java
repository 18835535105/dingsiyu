package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.PrizeExchangeListMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.duration.DurationStudyModelUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.smallapp.studyinfo.DurationInfoVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.dto.PrizeDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.IndexService;
import com.zhidejiaoyu.student.business.smallapp.vo.PrizeVO;
import com.zhidejiaoyu.student.business.smallapp.vo.StateVO;
import com.zhidejiaoyu.student.business.smallapp.vo.TotalDataVO;
import com.zhidejiaoyu.student.business.smallapp.vo.index.AdsensesVO;
import com.zhidejiaoyu.student.business.smallapp.vo.index.CardVO;
import com.zhidejiaoyu.student.business.smallapp.vo.index.IndexVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
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
    private GoldLogMapper goldLogMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private PrizeExchangeListMapper prizeExchangeListMapper;

    @Override
    public ServerResponse<Object> index() {
        Student student = super.getStudent(HttpUtil.getHttpSession());
        if (student == null) {
            return ServerResponse.createByError(400, "用户还未绑定学生账号！");
        }

        // 头部公有数据
        TotalDataVO totalDataVO = TotalDataVO.builder()
                .headImg(GetOssFile.getPublicObjectUrl(student.getHeadUrl()))
                .say(this.getSay())
                .studentName(student.getStudentName())
                .systemGold(String.valueOf(Math.round(student.getSystemGold())))
                .build();

        // 广告位
        List<AdsensesVO> adsensesVos = this.getAdsensesVOList(student);

        // 签到信息
        String currentMonth = DateUtil.getCurrentDay(DateUtil.YYYYMM);
        List<ClockIn> clockIns = clockInMapper.selectByStudentIdWithCurrentMonth(student.getId(), currentMonth);

        Integer cardDays = clockInMapper.selectLaseCardDays(student.getId());

        return ServerResponse.createBySuccess(IndexVO.builder()
                .adsenses(adsensesVos)
                .totalData(totalDataVO)
                .card(CardVO.builder()
                        .cardDays(cardDays == null ? 0 : cardDays)
                        .build())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> replenish(String date) {

        // 补签需要消耗的金币数
        int reduceGold = 50;
        HttpSession httpSession = HttpUtil.getHttpSession();
        Student student = super.getStudent(httpSession);

        if (student.getSystemGold() < reduceGold) {
            return ServerResponse.createByError(400, "金币不足！");
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

            goldLogMapper.insert(GoldLog.builder()
                    .createTime(now)
                    .goldAdd(0)
                    .goldReduce(reduceGold)
                    .operatorId(student.getId().intValue())
                    .readFlag(2)
                    .reason("补签")
                    .studentId(student.getId())
                    .build());

            student.setSystemGold(BigDecimalUtil.sub(student.getSystemGold(), reduceGold));
            student.setOfflineGold(BigDecimalUtil.add(student.getOfflineGold(), reduceGold));
            studentMapper.updateById(student);
            httpSession.setAttribute(UserConstant.CURRENT_STUDENT, student);

            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError(400, "选择的日期已打卡，无需再次打卡！");

    }

    @Override
    public ServerResponse<Object> record() {

        Long studentId = super.getStudentId();

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
    public ServerResponse<Object> myState() {

        Long studentId = super.getStudentId();

        String today = DateUtil.formatYYYYMMDD(new Date());

        String beforeSevenDays = DateUtil.getBeforeDayDateStr(new Date(), 7, DateUtil.YYYYMMDD) + " 00:00:00";

        // 耐久度
        Integer onlineTime = durationMapper.selectOnlineTime(studentId, beforeSevenDays, today);
        String onlineTimeStr = this.getOnlineTime(onlineTime);

        // 复习命中率
        int count = testRecordMapper.countByGenreWithBeginTimeAndEndTime(studentId, GenreConstant.SMALLAPP_GENRE, beforeSevenDays, today);

        // 机动力
        Double efficiency = durationMapper.selectLastStudyEfficiency(studentId);

        // 成绩平均值
        Double avg = testRecordMapper.selectScoreAvg(studentId, 100);

        // 单词数
        int wordCount = learnNewMapper.countLearnedWordCount(studentId);

        double score = Double.parseDouble(String.format("%.2f", avg));
        return ServerResponse.createBySuccess(StateVO.builder()
                .onlineTime(onlineTimeStr)
                .reviewCount(Math.min(7, count))
                .efficiency(efficiency > 1 ? 100 + "%" : Math.floor(efficiency * 100) + "%")
                .score(Math.min(100, score))
                .wordLearnedCount(Math.min(wordCount, 6000))
                .build());
    }

    @Override
    public ServerResponse<Object> prize(PrizeDTO dto) {

        Student student = super.getStudent();
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

    private String getOnlineTime(Integer onlineTime) {
        String onlineTimeStr;
        if (onlineTime == null) {
            onlineTimeStr = "0秒";
        } else {
            if (onlineTime < 60) {
                onlineTimeStr = onlineTime + "秒";
            } else if (onlineTime < 3600) {
                onlineTimeStr = (onlineTime / 60) + "分" + (onlineTime % 60) + "秒";
            } else {
                int hours = onlineTime / 3600;
                int remainSeconds = onlineTime - hours * 3600;

                if (hours > 10) {
                    onlineTimeStr = 10 + "小时";
                } else {
                    onlineTimeStr = (Math.min(hours, 10)) + "小时" + (remainSeconds / 60) + "分" + (remainSeconds % 60) + "秒";
                }
            }
        }
        return onlineTimeStr;
    }

    /**
     * @param durationInfoVos 需要展示的学习日期及各个日期的在线时长
     * @param durationInfos   各个日期具体的学习时长
     * @return
     */
    public List<DurationInfoVO> packageResultList(List<DurationInfoVO> durationInfoVos,
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

            result.add(DurationInfoVO.builder()
                    .learnDate(k)
                    .onlineTime(stringListMap.get(k).get(0).getOnlineTime() / 60)
                    .studyModelStr(sb.length() > 1 ? sb.toString().substring(0, sb.length() - 1) : "单词")
                    .build());
        });

        return result;
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
