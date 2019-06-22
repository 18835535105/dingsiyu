package com.zhidejiaoyu.student.controller.simple;

import com.zhidejiaoyu.common.dto.rank.RankDto;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimplePersonalCentreServiceSimple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 个人中心
 *
 * @author qizhentao
 * @version 1.0
 */
@Slf4j
@RestController
@Validated
@RequestMapping("/api/personal")
public class SimplePersonalCentreController {

    @Autowired
    private SimplePersonalCentreServiceSimple personalCentreService;

    /**
     * 点击个人中心
     * 1.消息中心红点是否显示
     * 2.学生姓名,等级
     *
     * @return read 消息中心是否有未读消息, true有未读消息  false无未读消息
     */
    @RequestMapping("/personal")
    public ServerResponse<Object> personalIndex(HttpSession session) {
        return personalCentreService.personalIndex(session);
    }

    /**
     * 统计个人中心中需要查看的消息个数
     * <ul>
     * <li>未查看的留言反馈个数</li>
     * <li>可抽奖次数</li>
     * </ul>
     *
     * @param session
     * @return
     */
    @GetMapping("/needViewCount")
    public ServerResponse needViewCount(HttpSession session) {
        return personalCentreService.needViewCount(session);
    }


    /**
     * 消息中心
     * 1.消息通知
     *
     * @return news数据
     */
    @RequestMapping("/news")
    public ServerResponse<Object> newsCentre(HttpSession session) {
        return personalCentreService.newsCentre(session);
    }

    /**
     * 消息中心
     * 2.消息通知里边功能
     * <p>
     * 1.删除
     * 2.根据通知id标记为已读
     * 3.全部标记为已读
     *
     * @param state 类型  1=删除, 2=标记为已读, 3=全部标记为已读
     * @param id    删除[] / 标记为已读[]
     *              <p>
     *              参数说明: state用户选择的是那个选项 (1=删除, 2=标记为已读, 3=全部标记为已读)
     *              id是用户选择列的id(多选放数组中)
     */
    @RequestMapping("/newsupdate")
    public ServerResponse<String> newsUpdate(HttpSession session, Integer state, Integer[] id) {
        return personalCentreService.newsUpdate(session, state, id);
    }


    /**
     * 我的报告 - 无分页
     * 1.每周时长
     */
    @RequestMapping("/weekDuration")
    public ServerResponse<Object> weekDurationIndex(HttpSession session) {
        return personalCentreService.weekDurationIndex(session);
    }

    /**
     * 我的报告 - 分页
     * 1.每周时长
     *
     * @param  year 0=全部, 2018=指定年份
     */
    @RequestMapping("/weekDurationPage")
    public ServerResponse<Object> weekDurationIndexTest(HttpSession session, int page, int rows, Integer year) {
        return personalCentreService.weekDurationIndexPageTwo(session, page, rows, year);
    }

    /**
     * 我的报告 - 无分页
     * 2.每周学习量
     */
    @RequestMapping("/weekQuantity")
    public ServerResponse<Object> weekQuantity(HttpSession session) {
        return personalCentreService.weekQuantity(session);
    }

    /**
     * 我的报告 - 分页
     * 2.每周学习量
     *
     * @param year 0=全部, 2018=指定年份
     */
    @RequestMapping("/weekQuantityPage")
    public ServerResponse<Object> weekQuantityPage(HttpSession session, int page, int rows, Integer year) {
        return personalCentreService.weekQuantityPageTwo(session, page, rows, year);
    }

    /**
     * 我的报告
     * 3.课程统计
     */
    @RequestMapping("/CourseStatistics")
    public ServerResponse<Object> courseStatistics(HttpSession session, int page, int rows) {
        try {
            return personalCentreService.courseStatistics(session, page, rows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 我的报告
     * 3.课程统计
     * 点击某个课程某个模块下的某个单元 显示 已学/单词总量
     *
     * @param session
     * @param model   模块: 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
     * @param unitId  单元id
     * @return
     */
    @RequestMapping("/CourseStatisticsCount")
    public ServerResponse<Object> courseStatisticsCount(HttpSession session, Integer unitId, Integer model) {
        return personalCentreService.courseStatisticsCountTrue(session, unitId, model);
    }

    /**
     * 我的排名
     *
     * @param rankDto
     */
    @RequestMapping(value = "/classSeniority", method = RequestMethod.POST)
    public ServerResponse<Object> classSeniority(HttpSession session, @Valid RankDto rankDto, BindingResult result) {

        if (rankDto.getType() == null) {
            rankDto.setType(1);
        }
        if (rankDto.getModel() == null) {
            rankDto.setModel(1);
        }
        if (rankDto.getPage() == null) {
            rankDto.setPage(1);
        }
        if (rankDto.getRows() == null) {
            rankDto.setRows(12);
        }

        return personalCentreService.rankingSeniority(session, rankDto);
    }

    /**
     * 我的证书
     *
     * @param session
     * @param model   默认0全部显示, 点击的那个模块
     *                1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;10:五维测试(词汇量测试);11:同步单词（同步版所有证书）
     * @return
     */
    @RequestMapping("/ccie")
    public ServerResponse<Object> showCcie(HttpSession session, @RequestParam(required = false, defaultValue = "0") Integer model) {
        return personalCentreService.showCcie(session, model);
    }

    /**
     * 将证书的是否已读状态更新为“已读”状态
     *
     * @param session
     * @return
     */
    @PostMapping("/updateCcie")
    public ServerResponse updateCcie(HttpSession session) {
        return personalCentreService.updateCcie(session);
    }

    /**
     * 进度排行榜
     * 区分版本和初高中！
     *
     * @param session  存放着学生信息
     * @param page     当前页
     * @param rows     显示多少条数据
     * @param model    默认1
     *                 本班排行模块  model = 1
     *                 本校模块 model = 2
     *                 全国模块 model = 3
     * @param haveUnit 已学单元 1=正序 2=倒叙 默认haveUnit=2倒叙
     * @param haveTest 已做测试 1=正序 2=倒叙
     * @param haveTime 学习时长 1=正序 2=倒叙
     * @return
     */
    @RequestMapping("/durationSeniority")
    public ServerResponse<Object> durationSeniority(HttpSession session, @RequestParam(required = false, defaultValue = "1") Integer model, Integer haveUnit, Integer haveTest, Integer haveTime, Integer page, Integer rows) {
        return personalCentreService.durationSeniority(session, model, haveUnit, haveTest, haveTime, page, rows);
    }

    /**
     * 我的报告下拉年份显示
     *
     * @param studentId 学生id
     */
    @PostMapping("getYear")
    public ServerResponse<Object> getYear(long studentId) {
        return personalCentreService.getYear(studentId);
    }

    /**
     * 充值卡首页
     *
     * @param studentId
     * @return 学生信息
     */
    @PostMapping("/payCardIndex")
    public ServerResponse<Object> payCardIndex(long studentId) {
        return personalCentreService.payCardIndex(studentId);
    }

    /**
     * 使用充值卡
     *
     * @param studentId
     * @return
     */
    @PostMapping("/postPayCard")
    public ServerResponse<Object> postPayCard(long studentId, String card) throws ParseException {
        return personalCentreService.postPayCard(studentId, card);
    }

    /**
     * 充值卡记录
     *
     * @param studentId
     * @return
     */
    @PostMapping("/getPayCard")
    public ServerResponse<Object> getPayCard(long studentId, int page, int rows) {
        return personalCentreService.getPayCard(studentId, page, rows);
    }

    /**
     * 获取当前学生所在班级最新获取勋章的学生信息
     *
     * @param session
     * @return
     */
    @GetMapping("/getLatestMedalInClass")
    public ServerResponse<List<Map<String, Object>>> getMedalInClass(HttpSession session) {
        return personalCentreService.getMedalInClass(session);
    }

    /**
     * 更新假数据
     *
     * @return
     */
    @GetMapping("/updateFakeData")
    public ServerResponse<Object> updateFakeData() {
        personalCentreService.updateFakeData();
        return ServerResponse.createBySuccess();
    }
}
