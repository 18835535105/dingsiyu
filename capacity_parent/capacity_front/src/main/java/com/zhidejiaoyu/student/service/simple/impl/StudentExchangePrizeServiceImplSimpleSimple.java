package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleIStudentExchangePrizeServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-25
 */
@Service
public class StudentExchangePrizeServiceImplSimpleSimple extends SimpleBaseServiceImpl<StudentExchangePrizeMapper, StudentExchangePrize> implements SimpleIStudentExchangePrizeServiceSimple {

    @Autowired
    private PrizeExchangeListMapper prizeExchangeListMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private StudentExchangePrizeMapper studentExchangePrizeMapper;
    @Autowired
    private OperationLogMapper operationLogMapper;
    @Autowired
    private GoldLogMapper goldLogMapper;
    @Autowired
    private SimpleCampusMapper simpleCampusMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private RunLogMapper runLogMapper;
    @Value("${ftp.prefix}")
    private String ftpPrefix;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private MedalAwardAsync medalAwardAsync;


    @Override
    public ServerResponse<Object> getAllList(HttpSession session) {
        Student student =getStudent(session);
        if(student.getTeacherId()==null){
            return ServerResponse.createBySuccess();
        }
        Long teacherId = student.getTeacherId();
        Integer schoolAdminById = teacherMapper.getSchoolAdminById(teacherId.intValue());
        String string = simpleCampusMapper.selSchoolName(teacherId);
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> oneMap = new HashMap<>();
        Map<String, Object> twoMap = new HashMap<>();
        Map<String, Object> threeMap = new HashMap<>();
        Double systemGold = student.getSystemGold();
        if (systemGold > 1) {
            Long round = Math.round(systemGold);
            returnMap.put("sysGold", round.intValue());
        } else {
            returnMap.put("sysGold", 0);
        }
        if (schoolAdminById == null || string == null || string == "") {
            if(schoolAdminById==null){
                schoolAdminById=teacherId.intValue();
                Integer teacherCount = teacherMapper.getTeacherCountByAdminId(teacherId);
                if (teacherCount == null && teacherCount == 0) {
                    addResultMapByAllList(oneMap, null, 1, false);
                    addResultMapByAllList(twoMap, null, 2, false);
                    addResultMapByAllList(threeMap, null, 3, false);
                    returnMap.put("oneMap", oneMap);
                    returnMap.put("twoMap", twoMap);
                    returnMap.put("threeMap", threeMap);
                    return ServerResponse.createBySuccess(returnMap);
                }
            }
            //一类放置
            List<PrizeExchangeList> oneType = prizeExchangeListMapper.getAllByType(null, schoolAdminById, 1);
            Integer oneCount = prizeExchangeListMapper.getCountByType(null, schoolAdminById, 1);
            getResultMap(oneMap,oneType);
            addResultMapByAll(oneMap,1,oneCount>5?true:false);
            //二类放置
            List<PrizeExchangeList> twoType = prizeExchangeListMapper.getAllByType(null, schoolAdminById, 2);
            Integer twoCount = prizeExchangeListMapper.getCountByType(null, schoolAdminById, 2);
            getResultMap(twoMap,twoType);
            addResultMapByAll(twoMap,2,twoCount>5?true:false);
            //三类放置
            List<PrizeExchangeList> threeType = prizeExchangeListMapper.getAllByType(null, schoolAdminById, 3);
            Integer threeCount = prizeExchangeListMapper.getCountByType(null, schoolAdminById, 3);
            getResultMap(threeMap,threeType);
            addResultMapByAll(twoMap,3,threeCount>5?true:false);
        } else {
            //一类放置
            List<PrizeExchangeList> oneType = prizeExchangeListMapper.getAllByType(teacherId, null, 1);
            Integer oneCount = prizeExchangeListMapper.getCountByType(teacherId, null, 1);
            getResultMap(oneMap,oneType);
            addResultMapByAll(oneMap,1,oneCount>5?true:false);
            //二类放置
            List<PrizeExchangeList> twoType = prizeExchangeListMapper.getAllByType(teacherId, null, 2);
            Integer twoCount = prizeExchangeListMapper.getCountByType(teacherId, null, 2);
            getResultMap(twoMap,twoType);
            addResultMapByAll(twoMap,2,twoCount>5?true:false);
            //三类放置
            List<PrizeExchangeList> threeType = prizeExchangeListMapper.getAllByType(teacherId, null, 3);
            Integer threeCount = prizeExchangeListMapper.getCountByType(teacherId, null, 3);
            getResultMap(threeMap,threeType);
            addResultMapByAll(threeMap,3,threeCount>5?true:false);
        }
        returnMap.put("oneMap", oneMap);
        returnMap.put("twoMap", twoMap);
        returnMap.put("threeMap", threeMap);
        return ServerResponse.createBySuccess(returnMap);
    }


    @Override
    public ServerResponse<Object> getList(int page, int row, HttpSession session, int type) {
        Student student = getStudent(session);
        Long teacherId = student.getTeacherId();
        Integer adminId = teacherMapper.getSchoolAdminById(teacherId.intValue());
        String string = simpleCampusMapper.selSchoolName(teacherId);
        Map<String, Object> map = new HashMap<>();
        Double systemGold = student.getSystemGold();
        if (systemGold > 1) {
            Long round = Math.round(systemGold);
            map.put("sysGold", round.intValue());
        } else {
            map.put("sysGold", 0);
        }
        map.put("page", page);
        map.put("row", row);
        if (adminId == null || string == null || string == ""){
            if(adminId==null){
                adminId=teacherId.intValue();
                Integer teacherCount = teacherMapper.getTeacherCountByAdminId(teacherId);
                if (teacherCount == null && teacherCount == 0) {
                    map.put("total", 0);
                    map.put("list", new ArrayList<>());
                    return ServerResponse.createBySuccess(map);
                }
            }
            Integer count = prizeExchangeListMapper.getCountByType(null, adminId, type);
            map.put("total", count);
            List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.getAll((page - 1) * row, row, adminId.longValue(),null,type);
            getResultMap(map,prizeExchangeLists);
        }else{
            Integer count = prizeExchangeListMapper.getCountByType(teacherId, null, type);
            map.put("total", count);
            List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.getAll((page - 1) * row, row, null,teacherId,type);
            getResultMap(map,prizeExchangeLists);
        }

        return ServerResponse.createBySuccess(map);
    }

    private void getResultMap(Map<String,Object> map,List<PrizeExchangeList> list){
        List<Map<String, Object>> prizeMap = new ArrayList<>();
        for(PrizeExchangeList prize : list){
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", prize.getId());
            dataMap.put("partUrl", ftpPrefix + prize.getPrizeUrl());
            dataMap.put("name", prize.getPrize());
            dataMap.put("gold", prize.getExchangePrize());
            dataMap.put("surplus", prize.getSurplusNumber());
            dataMap.put("describes", prize.getDescribes());
            prizeMap.add(dataMap);
        }
        map.put("list",prizeMap);
    }

    @Override
    public ServerResponse<Object> getExchangePrize(int page, int row, HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<Map<String, Object>> all = studentExchangePrizeMapper.getAll((page - 1) * row, row, studentId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("page", page);
        resultMap.put("row", row);
        Integer total = studentExchangePrizeMapper.getAllNumber(studentId);
        resultMap.put("total", total);
        Integer resultInteger=total-(page-1)*row;
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> map : all) {
            Integer state = (Integer) map.get("state");
            map.put("id",resultInteger);
            resultInteger-=1;
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
            }else if (state == 2) {
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
        Map<String, Object> resultMap = new HashMap<>();
        Student student = getStudent(session);
        Double systemGold = student.getSystemGold();
        PrizeExchangeList prizeExchangeList = prizeExchangeListMapper.selById(prizeId);
        resultMap.put("prizeExchange", prizeExchangeList);
        resultMap.put("sysGold", student.getSystemGold().intValue());
        if (prizeExchangeList.getExchangePrize() < systemGold) {
            if (prizeExchangeList.getSurplusNumber() > 0) {
                int index = addStudentExchangePrize(1, prizeId, student.getId());
                if (index > 0) {
                    prizeExchangeListMapper.updSulpersNumber(prizeId, prizeExchangeList.getSurplusNumber() - 1);
                    addOperationLog(prizeExchangeList.getPrize(), student.getId().intValue());
                    addGoldLog(student.getId(), prizeExchangeList.getExchangePrize(), "兑换奖励");
                    student.setSystemGold(student.getSystemGold() - prizeExchangeList.getExchangePrize());
                    student.setOfflineGold(student.getOfflineGold() + prizeExchangeList.getExchangePrize());
                    studentMapper.updateByPrimaryKey(student);

                    session.setAttribute(UserConstant.CURRENT_STUDENT, student);
                    resultMap.put("sysGold", student.getSystemGold().intValue());
                    resultMap.put("msg", "兑换成功,请联系老师帮助兑换");
                    return ServerResponse.createBySuccess(resultMap);
                } else {
                    resultMap.put("msg", "系统繁忙请稍后再试");
                    return ServerResponse.createBySuccess(resultMap);
                }
            } else {
                addStudentExchangePrize(3, prizeId, student.getId());
                resultMap.put("msg", "奖品数量不足，无法兑换");
                return ServerResponse.createBySuccess(resultMap);
            }
        } else {
            addStudentExchangePrize(6, prizeId, student.getId());
            resultMap.put("msg", "您的金币不足，无法兑换");
            return ServerResponse.createBySuccess(resultMap);
        }
    }


    private int addStudentExchangePrize(int state, Long prizeId, Long studentId) {
        StudentExchangePrize studentExchangePrize = new StudentExchangePrize();
        studentExchangePrize.setState(state);
        studentExchangePrize.setPrizeId(prizeId);
        studentExchangePrize.setStudentId(studentId);
        studentExchangePrize.setCreateTime(new Date());
        return studentExchangePrizeMapper.insert(studentExchangePrize);
    }

    private int addOperationLog(String prize, Integer studentId) {
        RunLog runLog = new RunLog();
        runLog.setCreateTime(new Date());
        runLog.setLogContent(studentId+"：兑奖管理 ："+prize+" 兑换成功");
        runLog.setType(8);
        runLog.setOperateUserId(studentId.longValue());
        return runLogMapper.insert(runLog);
    }

    private int addGoldLog(Long studentId, Integer price, String msg) {
        GoldLog goldLog = new GoldLog();
        goldLog.setCreateTime(new Date());
        goldLog.setGoldReduce(price);
        goldLog.setOperatorId(studentId.intValue());
        goldLog.setStudentId(studentId);
        goldLog.setReason(msg);
        goldLog.setReadFlag(0);
        return goldLogMapper.insert(goldLog);
    }

    private void addResultMapByAllList(Map<String, Object> map, Object data, int type, boolean more) {
        map.put("list", data);
        map.put("type", type);
        map.put("more", more);
    }
    private void addResultMapByAll(Map<String, Object> map,  int type, boolean more) {
        map.put("type", type);
        map.put("more", more);
    }

}
