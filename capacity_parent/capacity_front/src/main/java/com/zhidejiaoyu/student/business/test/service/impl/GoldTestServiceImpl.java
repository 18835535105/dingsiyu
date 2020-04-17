package com.zhidejiaoyu.student.business.test.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.TestStoreMapper;
import com.zhidejiaoyu.common.mapper.UnitTestStoreMapper;
import com.zhidejiaoyu.common.pojo.TestStore;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.goldtestvo.GoldTestSubjectsVO;
import com.zhidejiaoyu.common.vo.goldtestvo.GoldTestVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.test.dto.SaveGoldTestDTO;
import com.zhidejiaoyu.student.business.test.service.GoldTestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/4/17 09:10:10
 */
@Service
public class GoldTestServiceImpl extends BaseServiceImpl<TestStoreMapper, TestStore> implements GoldTestService {

    @Resource
    private UnitTestStoreMapper unitTestStoreMapper;

    @Resource
    private TestStoreMapper testStoreMapper;

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
    public ServerResponse<Object> saveTest(SaveGoldTestDTO dto) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString("123 $&$ 41323".split("\\$&\\$")));

        System.out.println(Arrays.toString("args \\n\n 123".split("\n")));
    }
}
