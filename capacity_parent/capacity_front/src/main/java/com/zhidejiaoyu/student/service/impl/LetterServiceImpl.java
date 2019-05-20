package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.CapacityStudentUnitMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.pojo.CapacityStudentUnit;
import com.zhidejiaoyu.common.pojo.Letter;
import com.zhidejiaoyu.common.mapper.LetterMapper;
import com.zhidejiaoyu.student.service.LetterService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Service
public class LetterServiceImpl extends BaseServiceImpl<LetterMapper, Letter> implements LetterService {

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;
    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;


    @Override
    public Object getLetterUnit(HttpSession session) {
        Long studentId = getStudentId(session);
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selLetterByStudentId(studentId);
        if(capacityStudentUnit!=null){

        }else{

        }
        return null;
    }
}
