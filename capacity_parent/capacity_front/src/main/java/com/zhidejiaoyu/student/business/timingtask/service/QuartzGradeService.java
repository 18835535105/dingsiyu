package com.zhidejiaoyu.student.business.timingtask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Student;

/**
 * 年级定时任务
 *
 * @author wuchenxi
 * @date 2018/5/22 16:25
 */
public interface QuartzGradeService extends IService<Student> {

    /**
     * 每年 8月31日学生升级年级
     */
    void updateGrade();
}
