package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author wuchenxi
 * @date 2018/8/24
 */
public class StudyFlowLabelTest extends BaseTest {

    @Autowired
    private StudyFlowName studyFlowName;

    @Test
    public void getFlowName() {
        System.out.println(studyFlowName.getFlowName(3155L));
    }
}