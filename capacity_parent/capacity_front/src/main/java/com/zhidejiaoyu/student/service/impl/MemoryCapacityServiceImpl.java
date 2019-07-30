package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.MemoryCapacityMapper;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.MemoryCapacity;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.constant.PetMP3Constant;
import com.zhidejiaoyu.student.service.MemoryCapacityService;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
@Service
public class MemoryCapacityServiceImpl extends BaseServiceImpl<MemoryCapacityMapper, MemoryCapacity> implements MemoryCapacityService {

    @Autowired
    private MemoryCapacityMapper memoryCapacityMapper;
    @Autowired
    private PetSayUtil petSayUtil;
    @Autowired
    private RunLogMapper runLogMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public ServerResponse<Object> getEnterMemoryCapacity(HttpSession session) {
        Long studentId = getStudentId(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(studentId);
        Map<String, Object> map = new HashMap<>();
        if (count == null || count.equals(0)) {
            map.put("isEnter", true);
        } else {
            map.put("isEnter", false);
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    @Transactional
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, Integer grade, Integer fraction) {
        //判断是否为当日第一次保存
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId());
        Map<String, Object> map = new HashMap<>();
        Integer gold = 0;
        String url;
        if (fraction == null) {
            fraction = 0;
        }
        if (count == null || count.equals(0)) {
            //当前为第一次测试 添加金币
            if (fraction >= 80) {
                //根据等级添加金币
                switch (grade) {
                    case 1:
                        gold = 1;
                        break;
                    case 2:
                        gold = 2;
                        break;
                    case 3:
                        gold = 3;
                        break;
                    case 4:
                        gold = 4;
                        break;
                    case 5:
                        gold = 5;
                        break;
                    default:
                        gold = 0;
                        break;
                }
            }
            try {
                Date date = new Date();
                MemoryCapacity memoryCapacity = new MemoryCapacity();
                memoryCapacity.setCreateTime(date);
                memoryCapacity.setGold(gold);
                memoryCapacity.setGrade(grade);
                memoryCapacity.setStudentId(student.getId());
                memoryCapacityMapper.insert(memoryCapacity);

                RunLog runLog = new RunLog(student.getId(), 4, "学生[" + student.getStudentName() + "]在记忆容量《"
                        + grade + "》中奖励#" + gold + "#枚金币", date);
                runLogMapper.insert(runLog);
                student.setSystemGold(student.getSystemGold() + gold);
                studentMapper.updateById(student);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (fraction <= 40) {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY);
        } else if (fraction <= 80) {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED);
        } else {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED);
        }
        map.put("gold", gold);
        map.put("listen", url);
        return ServerResponse.createBySuccess(map);
    }
}
