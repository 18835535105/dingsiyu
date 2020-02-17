package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.AdsenseMapper;
import com.zhidejiaoyu.common.mapper.ClockInMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Adsense;
import com.zhidejiaoyu.common.pojo.ClockIn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.serivce.IndexService;
import com.zhidejiaoyu.student.business.smallapp.vo.TotalDataVO;
import com.zhidejiaoyu.student.business.smallapp.vo.index.AdsensesVO;
import com.zhidejiaoyu.student.business.smallapp.vo.index.IndexVO;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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


        return ServerResponse.createBySuccess(IndexVO.builder()
                .adsenses(adsensesVos)
                .totalData(totalDataVO)
                .build());
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
        System.out.println(new DateTime().getHourOfDay());
    }
}
