package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.PersonalCentreService;
import com.zhidejiaoyu.student.service.simple.SimpleDrawRecordServiceSimple;
import com.zhidejiaoyu.student.service.simple.SimplePersonalCentreServiceSimple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 个人中心
 *
 * @author qizhentao
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/personal")
public class PersonalCentreController extends BaseController {

    @Autowired
    private PersonalCentreService personalCentreService;

    @Value("${domain}")
    private String domain;

    @Autowired
    private SimpleDrawRecordServiceSimple drawRecordService;

    @Autowired
    private SimplePersonalCentreServiceSimple simplePersonalCentreServiceSimple;


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
    public ServerResponse<Object> needViewCount(HttpSession session) {
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
    public ServerResponse<String> newsupdate(HttpSession session, Integer state, Integer[] id) {
        if (!Objects.equals(state, 3) && (id == null || id.length == 0)) {
            Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
            log.error("学生修改消息失败：原因id=null, studentId=[{}], studentName=[{}], state=[{}]", student.getId(), student.getStudentName(), state);
            return ServerResponse.createBySuccess();
        }
        return personalCentreService.newsupdate(session, state, id);
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
     * @param year 0=全部, 2018=指定年份
     */
    @RequestMapping("/weekDurationPage")
    public ServerResponse<Object> weekDurationIndexTest(HttpSession session, int page, int rows, Integer year) {
        return personalCentreService.weekDurationIndexPage(session, page, rows, year);
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
        return personalCentreService.weekQuantityPage(session, page, rows, year);
    }

    /**
     * 我的报告
     * 3.课程统计
     */
    @RequestMapping("/CourseStatistics")
    public ServerResponse<Object> CourseStatistics(HttpSession session, int page, int rows) {
        return personalCentreService.CourseStatistics(session, page, rows);
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
    public ServerResponse<Object> CourseStatisticsCount(HttpSession session, Integer unitId, Integer model) {
        // return personalCentreService.courseStatisticsCount(session, courseId, model, unitNumber);
        return personalCentreService.courseStatisticsCountTrue(session, unitId, model);
    }

    /**
     * 我的排名
     *
     * @param model       本班排行模块  model = 1
     *                    本校模块 model = 2
     *                    全国模块 model = 3
     * @param queryType   空代表全部查询，1=今日排行 2=本周排行 3=本月排行
     * @param gold        金币 1=正序 2=倒叙  - 默认金币倒叙排行
     * @param badge       勋章 1=正序 2=倒叙
     * @param certificate 证书 1=正序 2=倒叙
     * @param worship     膜拜 1=正序 2=倒叙
     */
    @RequestMapping(value = "/classSeniority", method = RequestMethod.POST)
    public ServerResponse<Object> classSeniority(HttpSession session, Integer page, Integer rows,
                                                 @RequestParam(required = false, defaultValue = "1") String model, String gold,
                                                 String badge, String certificate, String worship, Integer queryType) {
        return personalCentreService.classSeniority(session, page, rows, gold, badge, certificate, worship, model, queryType);
    }

    /**
     * 我的证书
     *
     * @param session
     * @param model   默认0全部显示, 点击的那个模块(7个模块) 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写；7：五维测试;
     * @param type    1:牛人证书；2：课程证书
     * @return
     */
    @RequestMapping("/ccie")
    public ServerResponse<Object> showCcie(HttpSession session, @RequestParam(required = false, defaultValue = "0") Integer model,
                                           Integer type) {
        return personalCentreService.showCcie(session, model, type);
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
    public ServerResponse<Object> durationSeniority(HttpSession session,
                                                    @RequestParam(required = false, defaultValue = "1") Integer model,
                                                    Integer haveUnit, Integer haveTest, Integer haveTime, Integer page, Integer rows) {
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
     * 获取当前学生所在班级最新获取勋章的学生信息
     *
     * @param session
     * @return
     */
    @GetMapping("/getLatestMedalInClass")
    public ServerResponse<List<Map<String,Object>>> getMedalInClass(HttpSession session) {
        return simplePersonalCentreServiceSimple.getMedalInClass(session);
    }

    @GetMapping("/getLucky")
    public Object getLucky(Integer studentId, HttpSession session) {
        return personalCentreService.getLucky(studentId, session);
    }

    /**
     * 查看今天是否为第一次抽奖
     *
     * @param session
     * @return
     */
    @PostMapping("/getRecord")
    public ServerResponse<Object> getRecord(HttpSession session) {
        return drawRecordService.selAwardNow(session);
    }

    /**
     * 添加抽奖
     */
    @PostMapping("/addAward")
    public ServerResponse<Object> addAward(HttpSession session, Integer type, String explain, String imgUrl) {
        int[] i = drawRecordService.addAward(session, type, explain, imgUrl);
        int index = i[0];
        ServerResponse<Object> result;
        if (index == 3) {
            result = ServerResponse.createByError(300, "无能量");
        } else if (index > 0) {
            String str = "0";
            if (i[1] != 0) {
                str = i[1] + "";
            }
            result = ServerResponse.createBySuccess(str);

        } else {
            result = ServerResponse.createBySuccess(601, "失败");
        }
        return result;
    }


}
