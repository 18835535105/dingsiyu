package com.zhidejiaoyu.student.business.wechat.qy.fly.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStudyPlanListVo {
    private String unitName;
    private String model;
    private String finalLevel;
}
