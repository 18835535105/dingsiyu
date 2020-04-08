package com.zhidejiaoyu.student.business.timingtask.vo;

import com.zhidejiaoyu.common.vo.smallapp.studyinfo.DurationInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: wuchenxi
 * @date: 2020/4/7 18:02:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DailyStateVO extends DurationInfoVO {
    
    private Long studentId;
}
