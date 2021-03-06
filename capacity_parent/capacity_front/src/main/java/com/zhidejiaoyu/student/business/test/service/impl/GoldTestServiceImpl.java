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
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.goldUtil.GoldUtil;
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

    /**
     * 图片标识符
     */
    private static final String PICTURE_SPLIT = "&&TP";

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

    @Resource
    private WeekActivityRankOpt weekActivityRankOpt;

    @Override
    public ServerResponse<Object> getTest(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<GoldTestVO> goldTestVoS = testStoreMapper.selectSubjectsByUnitId(unitId);
        LinkedHashMap<String, ArrayList<GoldTestVO>> collect = goldTestVoS.stream()
                .collect(Collectors.groupingBy(GoldTestVO::getType, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

        List<GoldTestSubjectsVO> vos = new ArrayList<>(goldTestVoS.size());
        collect.forEach((type, list) -> {
            // 根据测试内容分组，比如阅读理解类型，有可能一个类型有多篇阅读文章，需要通过内容再次分组后进行处理
            LinkedHashMap<String, ArrayList<GoldTestVO>> collect1 = list.stream()
                    .filter(l -> StringUtil.isNotEmpty(l.getContent()))
                    .collect(Collectors.groupingBy(GoldTestVO::getContent, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            if (collect1.size() > 1) {
                collect1.forEach((key, value) -> packageGoldTestSubjectsVO(vos, value));
            } else {
                packageGoldTestSubjectsVO(vos, list);
            }

        });
        return ServerResponse.createBySuccess(vos);
    }

    public void packageGoldTestSubjectsVO(List<GoldTestSubjectsVO> vos, ArrayList<GoldTestVO> value) {
        GoldTestVO goldTestVO = value.get(0);
        List<GoldTestSubjectsVO.Subjects> subjects = new ArrayList<>(value.size());
        packageSubjects(value, subjects);
        vos.add(GoldTestSubjectsVO.builder()
                .type(goldTestVO.getType())
                .subjects(subjects)
                .content(getContent(goldTestVO))
                .build());
    }

    public String[] getContent(GoldTestVO goldTestVO) {
        String content = goldTestVO.getContent();
        if (StringUtils.isEmpty(content)) {
            return new String[0];
        }
        if (content.contains(PICTURE_SPLIT)) {
            content = getImgUrl(content);
        }
        return replaceArrayStr(content.split("\n"));
    }

    /**
     * 封装选项
     *
     * @param list
     * @param subjects
     */
    public void packageSubjects(ArrayList<GoldTestVO> list, List<GoldTestSubjectsVO.Subjects> subjects) {
        list.forEach(vo -> {
            String select = vo.getSelect();
            if (StringUtils.isNotEmpty(select) && select.contains(PICTURE_SPLIT)) {
                select = getImgUrl(select);
            }

            String[] answer = StringUtils.isNotEmpty(vo.getAnswer()) ? replaceArrayStr(replaceN(vo.getAnswer()).split("#")) : new String[0];

            StringBuilder title = new StringBuilder(vo.getTitle());
            String[] titleArr = new String[0];
            if (StringUtils.isNotEmpty(title.toString())) {
                if (Objects.equals(vo.getType(), "连词成句")) {
                    title = new StringBuilder(replaceStr(title.toString()));
                }
                if (title.toString().contains(PICTURE_SPLIT)) {
                    title = new StringBuilder(getImgUrl(title.toString()));
                }
                String spaceSplit = "$&$";
                int count = StringUtils.countMatches(title.toString(), spaceSplit);

                int length = answer.length;
                if (count != length) {
                    for (int i = 0; i < length - count; i++) {
                        title.append(spaceSplit);
                    }
                }
                titleArr = replaceN(title.toString()).split("\n");
            }

            subjects.add(GoldTestSubjectsVO.Subjects.builder()
                    .id(vo.getId())
                    .title(replaceArrayStr(titleArr))
                    .selects(StringUtils.isEmpty(select) ? Collections.emptyList() : Arrays.asList(replaceArrayStr(select.split("\\$&\\$"))))
                    .analysis(StringUtils.isNotEmpty(vo.getAnalysis()) ? replaceArrayStr(replaceN(vo.getAnalysis()).split("\n")) : new String[0])
                    .answer(answer)
                    .build());
        });
    }

    /**
     * 替换掉数据中手动换行符
     *
     * @param str
     * @return
     */
    private String replaceN(String str) {
        return str.replace("\\n", "##").replace("\n", "").replace("##", "\n");
    }

    /**
     * 将数组中的 \n,\t 符号替换为空字符
     *
     * @param string
     * @return
     */
    public String[] replaceArrayStr(String[] string) {
        return Arrays.stream(string).map(this::replaceStr).filter(StringUtils::isNotEmpty).toArray(String[]::new);
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
        if (str.startsWith(",")) {
            str = str.substring(1);
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
        String[] split = StringUtil.trim(str).split("&&");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if (s.contains("TP")) {
                sb.append("&&").append(GetOssFile.getPublicObjectUrl(FileConstant.TEST_STORE_IMG)).append(StringUtil.trim(s).substring(2)).append(".png").append("&&");
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

        goldAddition = GoldUtil.addStudentGold(student, (int) goldAddition);

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

        GoldLogUtil.saveStudyGoldLog(student.getId(), GenreConstant.GOLD_TEST, BigDecimalUtil.convertsToInt(goldAddition));

        weekActivityRankOpt.updateWeekActivitySchoolRank(student);

        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        return ServerResponse.createBySuccess(testResultVo);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString("123 $&$ 41323".split("\\$&\\$")));
        System.out.println("arer \n".replace("\\n", ""));
        System.out.println(Arrays.toString("args \\n\n 123".split("\n")));


        String s = "1. It's on the first floor.  $&$\\n\n 2. No, it isn't. It's cold today.  $&$\\n\n 3. It's 3:30. It's time for PE class.  $&$\\n\n 4. It's hot and sunny. $&$\\n\n 5. Yes, it's a beautiful garden. $&$\n \n";
        System.out.println(s.replace("\\n", "##").replace("\n", "").replace("##", "\\n"));
    }
}
