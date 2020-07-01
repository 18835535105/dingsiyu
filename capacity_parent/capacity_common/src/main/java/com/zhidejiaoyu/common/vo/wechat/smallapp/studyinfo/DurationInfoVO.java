package com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学习记录详情
 *
 * @author: wuchenxi
 * @date: 2020/2/18 10:46:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DurationInfoVO implements Serializable {

    private Long onlineTime;

    private String learnDate;

    private Integer studyModel;

    private String studyModelStr;

}
