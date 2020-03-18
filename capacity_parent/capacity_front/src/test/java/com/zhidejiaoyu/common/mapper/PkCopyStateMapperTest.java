package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.PkCopyState;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/3/18 09:39:39
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PkCopyStateMapperTest {

    @Resource
    private PkCopyStateMapper pkCopyStateMapper;

    @Test
    public void selectById() {
        pkCopyStateMapper.selectById(1);
    }

    @Test
    public void insert() {
        pkCopyStateMapper.insert(PkCopyState.builder()
                .createTime(new Date())
                .durability(100)
                .schoolAdminId(1)
                .pkCopyBaseId(1L)
                .type(2)
                .build());
    }

    @Test
    public void selectBySchoolAdminIdAndPkCopyBaseId() {
        PkCopyState pkCopyState = pkCopyStateMapper.selectBySchoolAdminIdAndPkCopyBaseId(1, 1L);
        log.info(pkCopyState.toString());
    }

}
