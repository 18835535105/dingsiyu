package com.zhidejiaoyu.student.business.test.service.impl;

import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.annotation.TestChangeAnnotation;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.TestRecordMapper;
import com.zhidejiaoyu.common.mapper.TestStoreMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.pojo.TestStore;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.goldUtil.StudentGoldAdditionUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.TestResultVo;
import com.zhidejiaoyu.common.vo.goldtestvo.GoldTestSubjectsVO;
import com.zhidejiaoyu.common.vo.goldtestvo.GoldTestVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.test.dto.SaveGoldTestDTO;
import com.zhidejiaoyu.student.business.test.service.GoldTestService;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/4/17 09:10:10
 */
@Service
public class GoldTestServiceImpl extends BaseServiceImpl<TestStoreMapper, TestStore> implements GoldTestService {

    @Resource
    private TestStoreMapper testStoreMapper;

    @Resource
    private PetSayUtil petSayUtil;

    @Resource
    private RedisOpt redisOpt;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private StudentMapper studentMapper;

    @Override
    public ServerResponse<Object> getTest(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<GoldTestVO> goldTestVoS = testStoreMapper.selectSubjectsByUnitId(unitId);
        LinkedHashMap<String, ArrayList<GoldTestVO>> collect = goldTestVoS.stream()
                .collect(Collectors.groupingBy(GoldTestVO::getType, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

        List<GoldTestSubjectsVO> vos = new ArrayList<>(goldTestVoS.size());
        collect.forEach((type, list) -> {
            GoldTestVO goldTestVO = list.get(0);
            List<GoldTestSubjectsVO.Subjects> subjects = new ArrayList<>(list.size());
            if (StringUtils.isEmpty(goldTestVO.getContent())) {
                // 说明不是英语文章
                packageSubjects(list, subjects);
                vos.add(GoldTestSubjectsVO.builder()
                        .type(goldTestVO.getType())
                        .subjects(subjects)
                        .build());
            } else {
                // 说明是英语文章
                // 说明不是英语文章
                packageSubjects(list, subjects);
                vos.add(GoldTestSubjectsVO.builder()
                        .type(goldTestVO.getType())
                        .subjects(subjects)
                        .content(goldTestVO.getContent().split("\n"))
                        .build());
            }
        });
        return ServerResponse.createBySuccess(vos);
    }

    /**
     * 封装选项
     *
     * @param list
     * @param subjects
     */
    public void packageSubjects(ArrayList<GoldTestVO> list, List<GoldTestSubjectsVO.Subjects> subjects) {
        list.forEach(vo -> subjects.add(GoldTestSubjectsVO.Subjects.builder()
                .title(vo.getTitle().contains("$&$") ? vo.getTitle() : vo.getTitle() + "$&$")
                .selects(StringUtils.isEmpty(vo.getSelect()) ? new String[0] : vo.getSelect().split("\\$&\\$"))
                .analysis(vo.getAnalysis())
                .answer(vo.getAnswer())
                .build()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @GoldChangeAnnotation
    @TestChangeAnnotation
    public ServerResponse<Object> saveTest(SaveGoldTestDTO dto) {
        HttpSession session = HttpUtil.getHttpSession();
        Date date = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        Student student = super.getStudent();

        TestResultVo testResultVo = new TestResultVo();
        TestRecord testRecord = new TestRecord();

        String testMessage = TestServiceImpl.getTestMessage(student, testResultVo, testRecord, PointConstant.EIGHTY, petSayUtil);
        double goldAddition = 0;
        if (dto.getPoint() >= PointConstant.EIGHTY) {
            goldAddition = StudentGoldAdditionUtil.getGoldAddition(student, 10);
        }

        testResultVo.setMsg(testMessage);
        testResultVo.setGold(BigDecimalUtil.convertsToInt(goldAddition));
        testResultVo.setEnergy(0);
        // 重复提交
        if (redisOpt.isRepeatSubmit(student.getId(), date)) {
            return ServerResponse.createBySuccess(testResultVo);
        }

        testRecord.setStudentId(student.getId());
        testRecord.setPass(dto.getPoint() >= PointConstant.EIGHTY ? 1 : 2);
        testRecord.setCourseId(dto.getCourseId());
        testRecord.setErrorCount(dto.getErrorCount());
        testRecord.setGenre(GenreConstant.GOLD_TEST);
        testRecord.setPoint(dto.getPoint());
        testRecord.setQuantity(dto.getErrorCount() + dto.getRightCount());
        testRecord.setRightCount(dto.getRightCount());
        testRecord.setStudentId(student.getId());
        testRecord.setStudyModel(GenreConstant.GOLD_TEST);
        saveTestRecordTime(testRecord, session, date);
        testRecord.setUnitId(dto.getUnitId());
        testRecord.setAwardGold(BigDecimalUtil.convertsToInt(goldAddition));
        testRecord.setExplain(testMessage);
        testRecordMapper.insert(testRecord);

        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldAddition));
        studentMapper.updateById(student);

        GoldLogUtil.saveStudyGoldLog(student.getId(), GenreConstant.GOLD_TEST, BigDecimalUtil.convertsToInt(goldAddition));

        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        return ServerResponse.createBySuccess(testResultVo);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString("123 $&$ 41323".split("\\$&\\$")));

        System.out.println(Arrays.toString("args \\n\n 123".split("\n")));
    }
}
