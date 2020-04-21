package com.zhidejiaoyu.student.business.test.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.annotation.TestChangeAnnotation;
import com.zhidejiaoyu.common.constant.FileConstant;
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
            packageSubjects(list, subjects);
            vos.add(GoldTestSubjectsVO.builder()
                    .type(goldTestVO.getType())
                    .subjects(subjects)
                    .content(StringUtils.isEmpty(goldTestVO.getContent()) ? new String[0] : replaceArrayStr(goldTestVO.getContent().split("\n")))
                    .build());
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
        list.forEach(vo -> {
            // 图片标识符
            String pictureSplit = "&&TP";

            String select = vo.getSelect();
            if (StringUtils.isNotEmpty(select) && select.contains(pictureSplit)) {
                select = getImgUrl(select);
            }

            String title = vo.getTitle();
            String[] titleArr = new String[0];
            if (StringUtils.isNotEmpty(title)) {
                if (Objects.equals(vo.getType(), "连词成句")) {
                    title = replaceStr(title);
                }
                if (title.contains(pictureSplit)) {
                    title = getImgUrl(title);
                }
                String spaceSplit = "$&$";
                if (!title.contains(spaceSplit)) {
                    title += spaceSplit;
                }
                titleArr = title.split("\\n");
            }

            subjects.add(GoldTestSubjectsVO.Subjects.builder()
                    .id(vo.getId())
                    .title(replaceArrayStr(titleArr))
                    .selects(StringUtils.isEmpty(select) ? Collections.emptyList() : Arrays.asList(replaceArrayStr(select.split("\\$&\\$"))))
                    .analysis(StringUtils.isNotEmpty(vo.getAnalysis()) ? replaceArrayStr(vo.getAnalysis().split("\\n")) : new String[0])
                    .answer(StringUtils.isNotEmpty(vo.getAnswer()) ? replaceArrayStr(vo.getAnswer().replace("#", "").split("\\n")) : new String[0])
                    .build());
        });
    }

    /**
     * 将数组中的 \n,\t 符号替换为空字符
     *
     * @param string
     * @return
     */
    public String[] replaceArrayStr(String[] string) {
        return Arrays.stream(string).map(this::replaceStr).toArray(String[]::new);
    }

    /**
     * 将字符串中的 \n，\t 符号替换为空字符
     *
     * @param str
     * @return
     */
    private String replaceStr(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replace("\\n", "").replace("\n", "").replace("\t", "").trim();
    }

    /**
     * 拼接测试题图片url
     *
     * @param str
     * @return
     */
    public String getImgUrl(String str) {
        String[] split = str.split("&&");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if (s.contains("TP")) {
                sb.append("&&").append(GetOssFile.getPublicObjectUrl(FileConstant.TEST_STORE_IMG)).append(s.substring(2)).append(".png").append("&&");
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
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
        testRecord.setPoint(dto.getPoint());

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
        System.out.println("arer \n".replace("\\n", ""));
        System.out.println(Arrays.toString("args \\n\n 123".split("\n")));
    }
}
