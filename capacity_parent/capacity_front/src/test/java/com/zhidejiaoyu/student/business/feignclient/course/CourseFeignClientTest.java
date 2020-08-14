package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.TeksNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:21:21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class CourseFeignClientTest {

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Resource
    private UnitFeignClient unitFeignClient;

    @Resource
    private VocabularyFeignClient vocabularyFeignClient;

    @Resource
    private CenterTeksFeignClient centerTeksFeignClient;

    @Test
    public void testGetById() {
        CourseNew courseNew = courseFeignClient.getById(2749L);
        log.info("course={}", courseNew.toString());
    }

    @Test
    public void selTeksByUnitIdAndGroup() {
        List<TeksNew> teksNews = centerTeksFeignClient.selTeksByUnitIdAndGroup(101033L, 1);
        log.info("teksNews={}", teksNews.toString());
    }

    @Test
    public void getPhaseByUnitId() {
        String phaseByUnitId = courseFeignClient.getPhaseByUnitId(101257L);
        Assert.isTrue("小学".equals(phaseByUnitId), "error");
    }

    @Test
    public void getGradeAndLabelByUnitIds() {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(101257L);
        List<GradeAndUnitIdDTO> gradeAndLabelByUnitIds = courseFeignClient.getGradeAndLabelByUnitIds(longs);
        log.info("gradeAndLabelByUnitIds={}", gradeAndLabelByUnitIds.toString());
    }

    @Test
    public void getUnitIdsByCourseNames() {
        ArrayList<String> longs = new ArrayList<>();
        longs.add("人教版(七年级-下册)");
        List<Long> unitIdsByCourseNames = unitFeignClient.getUnitIdsByCourseNames(longs);
        log.info("unitIdsByCourseNames={}", unitIdsByCourseNames.toString());
    }

    @Test
    public void getUnitNewsByIds() {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(101257L);
        List<UnitNew> unitNewsByIds = unitFeignClient.getUnitNewsByIds(longs);
        log.info("unitNewsByIds={}", unitNewsByIds.toString());
    }

    @Test
    public void getUnitIdsByGradeListAndVersionAndGrade() {
        ArrayList<String> longs = new ArrayList<>();
        longs.add("七年级");
        List<Long> list = unitFeignClient.getUnitIdsByGradeListAndVersionAndGrade("人教版", longs);
        log.info("list={}", list.toString());
    }

    @Test
    public void getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId() {
        List<Long> list = unitFeignClient.getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(9489L, 101806L);
        log.info("list={}", list.toString());
    }

    @Test
    public void getSubjectsVOByUnitIds() {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(101257L);
        List<SubjectsVO> subjectsVOByUnitIds = vocabularyFeignClient.getSubjectsVOByUnitIds(longs);
        log.info("subjectsVOByUnitIds={}", subjectsVOByUnitIds.toString());
    }
}
