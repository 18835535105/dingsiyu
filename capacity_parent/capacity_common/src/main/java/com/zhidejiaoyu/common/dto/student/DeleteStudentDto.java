package com.zhidejiaoyu.common.dto.student;

import lombok.Data;

@Data
public class DeleteStudentDto {

    private String studentUuid;
    private String openId;
    private String userUuid;
}
