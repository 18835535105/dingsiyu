package com.zhidejiaoyu.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TeksTestResultVo extends TestResultVo {

    private String petName;

    private String text;

    private String imgUrl;
}
