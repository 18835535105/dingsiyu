package com.zhidejiaoyu.student.business.service.simple.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.PrizeExchangeListMapper;
import com.zhidejiaoyu.common.mapper.StudentExchangePrizeTmpMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.prize.GetPrizeVO;
import com.zhidejiaoyu.student.business.service.simple.SimpleIStudentExchangePrizeServiceSimple;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import com.zhidejiaoyu.student.common.redis.PayLogRedisOpt;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-25
 */
@Service
public class StudentExchangePrizeServiceImplSimpleSimple extends SimpleBaseServiceImpl<SimpleStudentExchangePrizeMapper, StudentExchangePrize> implements SimpleIStudentExchangePrizeServiceSimple {

    @Autowired
    private PrizeExchangeListMapper prizeExchangeListMapper;
    @Autowired
    private SimpleStudentMapper simpleStudentMapper;
    @Autowired
    private SimpleStudentExchangePrizeMapper simpleStudentExchangePrizeMapper;

    @Autowired
    private SimpleCampusMapper simpleCampusMapper;
    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;
    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Resource
    private PayLogRedisOpt payLogRedisOpt;

    @Resource
    private StudentExchangePrizeTmpMapper studentExchangePrizeTmpMapper;

    @Resource
    private StudentMapper studentMapper;


    @Override
    public ServerResponse<Object> getAllList(HttpSession session) {
        Student student = getStudent(session);
        if (student.getTeacherId() == null) {
            return ServerResponse.createBySuccess();
        }
        Long teacherId = student.getTeacherId();
        Integer schoolAdminById = simpleTeacherMapper.getSchoolAdminById(teacherId.intValue());
        String schoolName = simpleCampusMapper.selSchoolName(teacherId);
        Map<String, Object> returnMap = new HashMap<>();

        Double systemGold = student.getSystemGold();
        if (systemGold > 1) {
            long round = Math.round(systemGold);
            returnMap.put("sysGold", (int) round);
        } else {
            returnMap.put("sysGold", 0);
        }

        // 如果学生没有充值，兑换按钮置灰
        boolean isPaid = payLogRedisOpt.isPaid(student.getId());
        returnMap.put("isPaid", isPaid);

        if (schoolAdminById == null || StringUtils.isEmpty(schoolName)) {
            if (schoolAdminById == null) {
                schoolAdminById = teacherId.intValue();
                int teacherCount = simpleTeacherMapper.getTeacherCountByAdminId(teacherId);
                if (teacherCount == 0) {
                    Map<String, Object> oneMap = new HashMap<>();
                    Map<String, Object> twoMap = new HashMap<>();
                    Map<String, Object> threeMap = new HashMap<>();
                    addResultMapByAllList(oneMap, 1);
                    addResultMapByAllList(twoMap, 2);
                    addResultMapByAllList(threeMap, 3);
                    returnMap.put("oneMap", oneMap);
                    returnMap.put("twoMap", twoMap);
                    returnMap.put("threeMap", threeMap);
                    return ServerResponse.createBySuccess(returnMap);
                }
            }
            return packagePrize(schoolAdminById, returnMap, null);
        }
        return packagePrize(null, returnMap, teacherId);
    }

    public ServerResponse<Object> packagePrize(Integer schoolAdminById, Map<String, Object> returnMap, Long teacherId) {
        Map<String, Object> oneMap = new HashMap<>(16);
        Map<String, Object> twoMap = new HashMap<>(16);
        Map<String, Object> threeMap = new HashMap<>(16);

        // 一类放置
        List<PrizeExchangeList> oneType = prizeExchangeListMapper.getAllByType(teacherId, schoolAdminById, 1);
        Integer oneCount = prizeExchangeListMapper.getCountByType(teacherId, schoolAdminById, 1);
        getResultMap(oneMap, oneType);
        addResultMapByAll(oneMap, 1, oneCount > 5);

        // 二类放置
        List<PrizeExchangeList> twoType = prizeExchangeListMapper.getAllByType(teacherId, schoolAdminById, 2);
        Integer twoCount = prizeExchangeListMapper.getCountByType(teacherId, schoolAdminById, 2);
        getResultMap(twoMap, twoType);
        addResultMapByAll(twoMap, 2, twoCount > 5);

        // 三类放置
        List<PrizeExchangeList> threeType = prizeExchangeListMapper.getAllByType(teacherId, schoolAdminById, 3);
        Integer threeCount = prizeExchangeListMapper.getCountByType(teacherId, schoolAdminById, 3);
        getResultMap(threeMap, threeType);
        addResultMapByAll(threeMap, 3, threeCount > 5);

        returnMap.put("oneMap", oneMap);
        returnMap.put("twoMap", twoMap);
        returnMap.put("threeMap", threeMap);
        return ServerResponse.createBySuccess(returnMap);
    }


    @Override
    public ServerResponse<Object> getList(int page, int row, HttpSession session, int type) {
        Student student = getStudent(session);
        Long teacherId = student.getTeacherId();
        Integer adminId = simpleTeacherMapper.getSchoolAdminById(teacherId.intValue());
        String string = simpleCampusMapper.selSchoolName(teacherId);
        Map<String, Object> map = new HashMap<>(16);

        List<PrizeExchangeList> prizeExchangeLists;
        if (adminId == null || StringUtils.isEmpty(string)) {
            if (adminId == null) {
                adminId = teacherId.intValue();
                int teacherCount = simpleTeacherMapper.getTeacherCountByAdminId(teacherId);
                if (teacherCount == 0) {
                    map.put("total", 0);
                    map.put("list", new ArrayList<>());
                    return ServerResponse.createBySuccess(map);
                }
            }
            PageHelper.startPage(page, row);
            prizeExchangeLists = prizeExchangeListMapper.getAll(adminId.longValue(), null, type);
        } else {
            PageHelper.startPage(page, row);
            prizeExchangeLists = prizeExchangeListMapper.getAll(null, teacherId, type);
        }
        Double systemGold = student.getSystemGold();
        if (systemGold > 1) {
            long round = Math.round(systemGold);
            map.put("sysGold", (int) round);
        } else {
            map.put("sysGold", 0);
        }
        PageInfo<PrizeExchangeList> pageInfo = new PageInfo<>(prizeExchangeLists);

        getResultMap(map, prizeExchangeLists);
        map.put("total", pageInfo.getTotal());
        map.put("page", page);
        map.put("row", row);

        return ServerResponse.createBySuccess(map);
    }

    private void getResultMap(Map<String, Object> map, List<PrizeExchangeList> list) {
        List<Map<String, Object>> prizeMap = new ArrayList<>();
        for (PrizeExchangeList prize : list) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", prize.getId());
            dataMap.put("partUrl", GetOssFile.getPublicObjectUrl(prize.getPrizeUrl()));
            dataMap.put("name", prize.getPrize());
            dataMap.put("gold", prize.getExchangePrize());
            dataMap.put("surplus", prize.getSurplusNumber());
            dataMap.put("describes", prize.getDescribes());
            prizeMap.add(dataMap);
        }
        map.put("list", prizeMap);
    }

    @Override
    public ServerResponse<Object> getExchangePrize(HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        //List<Map<String, Object>> all = simpleStudentExchangePrizeMapper.getAll((page - 1) * row, row, studentId);
        List<Map<String, Object>> all = simpleStudentExchangePrizeMapper.getAll(null, null, studentId);
        Map<String, Object> resultMap = new HashMap<>();
        Integer resultInteger = simpleStudentExchangePrizeMapper.getAllNumber(studentId);
        resultMap.put("page", 0);
        resultMap.put("row", 0);
        resultMap.put("total", resultInteger);
       /*
        resultMap.put("page", page);
        resultMap.put("row", row);
        resultMap.put("total", total);
        int resultInteger = total - (page - 1) * row;*/
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> map : all) {
            Integer state = (Integer) map.get("state");
            map.put("id", resultInteger);
            resultInteger -= 1;
            if (state == 0) {
                map.put("state", "成功");
                map.put("msg", "已领取");
            } else if (state == 6) {
                map.put("state", "失败");
                map.put("msg", "金币不足");
            } else if (state == 3) {
                map.put("state", "失败");
                map.put("msg", "奖品不足");
            } else if (state == 1) {
                map.put("state", "成功");
                map.put("msg", "兑换成功快去找老师领取奖励吧");
            } else if (state == 2) {
                map.put("state", "失败");
                map.put("msg", "兑换失败");
            }

            resultList.add(map);
        }
        resultMap.put("data", resultList);
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> addExchangePrize(HttpSession session, Long prizeId) {
        Student student = getStudent(session);
        Double systemGold = student.getSystemGold();
        PrizeExchangeList prizeExchangeList = prizeExchangeListMapper.selById(prizeId);

        GetPrizeVO getPrizeVO = new GetPrizeVO();
        prizeExchangeList.setPrizeUrl(GetOssFile.getPublicObjectUrl(prizeExchangeList.getPrizeUrl()));
        getPrizeVO.setPrizeExchange(prizeExchangeList);
        getPrizeVO.setSysGold(student.getSystemGold().intValue());

        boolean isPaid = payLogRedisOpt.isPaid(student.getId());
        if (!isPaid) {
            // 未充值
            getPrizeVO.setMsg("同学你还不是正式学员，请成为正式学员，在藏宝阁兑换奖励吧！");
            return ServerResponse.createBySuccess(getPrizeVO);
        }

        if (prizeExchangeList.getExchangePrize() > systemGold) {
            addStudentExchangePrize(6, prizeId, student.getId());
            getPrizeVO.setMsg("您的金币不足，无法兑换");
            return ServerResponse.createBySuccess(getPrizeVO);
        }

        if (prizeExchangeList.getSurplusNumber() <= 0) {
            addStudentExchangePrize(3, prizeId, student.getId());
            getPrizeVO.setMsg("奖品数量不足，无法兑换");
            return ServerResponse.createBySuccess(getPrizeVO);
        }


        int index = addStudentExchangePrize(1, prizeId, student.getId());
        if (index > 0) {
            prizeExchangeListMapper.updSulpersNumber(prizeId, prizeExchangeList.getSurplusNumber() - 1);
            addOperationLog(prizeExchangeList.getPrize(), student.getId().intValue());
            addGoldLog(student.getId(), prizeExchangeList.getExchangePrize());
            student.setSystemGold(student.getSystemGold() - prizeExchangeList.getExchangePrize());
            student.setOfflineGold(student.getOfflineGold() + prizeExchangeList.getExchangePrize());
            simpleStudentMapper.updateById(student);

            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            getPrizeVO.setSysGold(student.getSystemGold().intValue());
            getPrizeVO.setMsg("兑换成功,请联系老师帮助兑换");
            return ServerResponse.createBySuccess(getPrizeVO);
        }

        getPrizeVO.setMsg("系统繁忙请稍后再试");
        return ServerResponse.createBySuccess(getPrizeVO);
    }


    private int addStudentExchangePrize(int state, Long prizeId, Long studentId) {
        StudentExchangePrize studentExchangePrize = new StudentExchangePrize();
        studentExchangePrize.setState(state);
        studentExchangePrize.setPrizeId(prizeId);
        studentExchangePrize.setStudentId(studentId);
        studentExchangePrize.setCreateTime(new Date());
        return simpleStudentExchangePrizeMapper.insert(studentExchangePrize);
    }

    private void addOperationLog(String prize, Integer studentId) {
        RunLog runLog = new RunLog();
        runLog.setCreateTime(new Date());
        runLog.setLogContent(studentId + "：兑奖管理 ：" + prize + " 兑换成功");
        runLog.setType(8);
        runLog.setOperateUserId(studentId.longValue());
        runLogMapper.insert(runLog);
    }

    private void addGoldLog(Long studentId, Integer price) {
        GoldLogUtil.savePrizeGoldLog(studentId, "兑换奖品", price);
    }

    private void addResultMapByAllList(Map<String, Object> map, int type) {
        map.put("list", null);
        map.put("type", type);
        map.put("more", false);
    }

    private void addResultMapByAll(Map<String, Object> map, int type, boolean more) {
        map.put("type", type);
        map.put("more", more);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportData() {
        List<StudentExchangePrizeTmp> studentExchangePrizeTmps = studentExchangePrizeTmpMapper.selectList(null);
        if (CollectionUtils.isEmpty(studentExchangePrizeTmps)) {
            return;
        }
        List<StudentExchangePrize> studentExchangePrizes = new ArrayList<>();
        studentExchangePrizeTmps.forEach(studentExchangePrizeTmp -> {
            if (studentExchangePrizeTmp.getStudentId() == null) {
                return;
            }
            Student student = studentMapper.selectById(studentExchangePrizeTmp.getStudentId());
            if (student == null || student.getTeacherId() == null) {
                return;
            }
            Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
            if (schoolAdminId == null) {
                return;
            }

            PrizeExchangeList prizeExchangeList = prizeExchangeListMapper.selectBySchoolIdAndPrize(schoolAdminId, studentExchangePrizeTmp.getPrizeName());
            if (prizeExchangeList == null) {
                return;
            }

            StudentExchangePrize studentExchangePrize = new StudentExchangePrize();
            studentExchangePrize.setCreateTime(studentExchangePrizeTmp.getCreateTime());
            studentExchangePrize.setPrizeId(prizeExchangeList.getId());
            studentExchangePrize.setState(studentExchangePrizeTmp.getState());
            studentExchangePrize.setStudentId(studentExchangePrizeTmp.getStudentId());
            studentExchangePrize.setUpdateTime(new Date());
            studentExchangePrizes.add(studentExchangePrize);
        });

        this.saveBatch(studentExchangePrizes);
    }


}
