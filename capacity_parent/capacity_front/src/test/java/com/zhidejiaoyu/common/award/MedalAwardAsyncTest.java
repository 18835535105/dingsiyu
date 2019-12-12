package com.zhidejiaoyu.common.award;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MedalAwardAsyncTest extends BaseTest {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    private Student student;

    @Before
    public void before() {
        student = studentMapper.selectById(9604L);
    }

    @Test
    public void inexperienced() {
    }

    @Test
    public void honour() {
    }

    @Test
    public void updateHonour() {
    }

    @Test
    public void upLevel() {
    }

    @Test
    public void superStudent() {
        medalAwardAsync.superStudent(student);
    }

    @Test
    public void potentialMan() {
    }

    @Test
    public void oldMan() {
    }

    @Test
    public void tryHand() {
    }

    @Test
    public void expertMan() {
    }

    @Test
    public void godMan() {
    }

    @Test
    public void enjoyPopularConfidence() {
    }

    @Test
    public void theFirst() {
        medalAwardAsync.theFirst(student);
    }

    @Test
    public void resetLevel() {
    }

    @Test
    public void testMonsterMedal() {
        medalAwardAsync.monsterMedal(student, 5319L);
    }
}
