package com.zhidejiaoyu.student.business.service.simple.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.EquipmentMapper;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.WeekUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.simple.studentInfoVo.ChildMedalVo;
import com.zhidejiaoyu.student.business.service.simple.SimpleStudentInfoService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@Service
public class SimpleStudentInfoServiceImpl extends SimpleBaseServiceImpl<SimpleStudentMapper, Student> implements SimpleStudentInfoService {

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleWorshipMapper worshipMapper;

    @Autowired
    private SimpleMedalMapper simpleMedalMapper;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;

    @Override
    public ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword) {

        if (!Objects.equals(nowPassword, oldPassword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.PASSWORD_ERROR.getCode(), ResponseCode.PASSWORD_ERROR.getMsg());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> updateStudentInfo(HttpSession session, Student student) {
        Student currentStudent = getStudent(session);
        if (!Objects.equals(currentStudent.getId(), student.getId())) {
            log.error("学生 {}->{} 试图修改 学生 {}->{} 的个人信息！", currentStudent.getId(), currentStudent.getStudentName(),
                    student.getId(), student.getStudentName());
            return ServerResponse.createByErrorMessage("服务器错误！请稍后重试");
        }
        student.setPartUrl(student.getPartUrl() == null ? student.getPartUrl() : student.getPartUrl().replace(AliyunInfoConst.host, ""));
        student.setHeadUrl(student.getHeadUrl() == null ? student.getHeadUrl() : student.getHeadUrl().replace(AliyunInfoConst.host, ""));
        simpleStudentMapper.updateById(student);
        student = simpleStudentMapper.selectById(currentStudent.getId());
        student.setDiamond(currentStudent.getDiamond());
        student.setEnergy(currentStudent.getEnergy());
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        Equipment equipment = equipmentMapper.selectByName(student.getPetName());
        shipAddEquipmentService.updateUseEqu(student,equipment);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Map<String, Object>> getWorship(HttpSession session, Integer type, Integer pageNum, Integer pageSize) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>(16);
        // 本周我被膜拜的次数
        Date date = new Date();
        Date firstDayOfWeek = WeekUtil.getFirstDayOfWeek(date);
        Date lastDayOfWeek = WeekUtil.getLastDayOfWeek(date);
        int count = worshipMapper.countByWorshipedThisWeed(student, DateUtil.formatYYYYMMDD(firstDayOfWeek), DateUtil.formatYYYYMMDD(lastDayOfWeek));
        map.put("count", count);

        // 膜拜记录
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, String>> mapList = worshipMapper.selectStudentNameAndTime(student, type);
        map.put("list", new PageInfo<>(mapList));

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<ChildMedalVo> getChildMedal(HttpSession session, Long stuId, Long medalId) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = simpleStudentMapper.selectById(stuId);
        }
        ChildMedalVo childMedalInfo = getChildMedalInfo(student, medalId);
        return ServerResponse.createBySuccess(childMedalInfo);
    }

    @Override
    public ServerResponse<Map<String, Object>> getAllMedal(HttpSession session, Long stuId, Integer pageNum, Integer pageSize) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = simpleStudentMapper.selectById(stuId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> medalImgUrlList = simpleMedalMapper.selectMedalImgUrl(student);
        List<Map<String, Object>> medalImgUrlListTemp = getAllMedalImgUrl(student, medalImgUrlList);

        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(medalImgUrlList);
        PageInfo<Map<String, Object>> mapPageInfo1 = new PageInfo<>(medalImgUrlListTemp);
        mapPageInfo1.setTotal(mapPageInfo.getTotal());
        mapPageInfo1.setPages(mapPageInfo.getPages());

        Map<String, Object> map = new HashMap<>(16);
        map.put("petName", GetOssFile.getPublicObjectUrl(student.getPetName()));
        map.put("list", mapPageInfo1);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> optBackMusic(HttpSession session, Integer status) {
        Long studentId = super.getStudentId(session);
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
        if (studentExpansion == null){
            studentExpansion = new StudentExpansion();
            studentExpansion.setAudioStatus(status);
            simpleStudentExpansionMapper.insert(studentExpansion);
        } else {
            studentExpansion.setAudioStatus(status);
            simpleStudentExpansionMapper.updateById(studentExpansion);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Map<String, Integer>> getBackMusicStatus(HttpSession session) {
        Long studentId = super.getStudentId(session);
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
        Map<String, Integer> map = new HashMap<>(16);
        if (studentExpansion == null || studentExpansion.getAudioStatus() == null) {
            map.put("state", 1);
        } else {
            map.put("state", studentExpansion.getAudioStatus());
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object deleteRepeatLogoutLogs() {
        List<RunLog> runLogs = runLogMapper.selectList(new QueryWrapper<RunLog>().eq("type", 1)
                .orderBy(true, true, "operate_user_id", "create_time"));

        List<Long> logIds = new ArrayList<>();
        int size = runLogs.size();
        RunLog runLog;
        for (int i = 0; i < size; i++) {
            runLog = runLogs.get(i);
            if (i > 0 && runLogs.get(i - 1).getLogContent().contains("退出登录") && runLog.getLogContent().contains("退出登录")) {
                logIds.add(runLog.getId());
            }
        }
        runLogMapper.deleteBatchIds(logIds);
        return ServerResponse.createBySuccess();
    }

    private List<Map<String, Object>> getAllMedalImgUrl(Student student, List<Map<String, Object>> medalImgUrlList) {
        List<Map<String, Object>> medalImgUrlListTemp = new ArrayList<>(medalImgUrlList.size());
        int sex = student.getSex() == null ? 1 : student.getSex();
        medalImgUrlList.forEach(map -> {
            Map<String, Object> mapTemp = new HashMap<>(16);
            if (map.get("imgUrl").toString().contains("#")) {
                mapTemp.put("imgUrl", sex == 1 ? AliyunInfoConst.host +  map.get("imgUrl").toString().split("#")[0] : AliyunInfoConst.host +  map.get("imgUrl").toString().split("#")[1]);
            } else {
                mapTemp.put("imgUrl", AliyunInfoConst.host + map.get("imgUrl"));
            }
            mapTemp.put("id", map.get("id"));
            medalImgUrlListTemp.add(mapTemp);
        });

        return medalImgUrlListTemp;
    }

    private ChildMedalVo getChildMedalInfo(Student student, long medalId) {
        List<Map<String, String>> childInfo = simpleMedalMapper.selectChildrenInfo(student, medalId);

        List<String> medalImgUrl = new ArrayList<>(childInfo.size());
        StringBuilder sb = new StringBuilder();
        childInfo.forEach(info -> {
            medalImgUrl.add(info.get("imgUrl"));
            sb.append(info.get("content"));
        });

        ChildMedalVo childMedalVo = new ChildMedalVo();
        childMedalVo.setMedalImgUrl(medalImgUrl);
        childMedalVo.setContent(sb.toString());
        return childMedalVo;
    }
}
