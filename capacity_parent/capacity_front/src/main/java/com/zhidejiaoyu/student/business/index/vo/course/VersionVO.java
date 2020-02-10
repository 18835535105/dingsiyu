package com.zhidejiaoyu.student.business.index.vo.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wuchenxi
 * @date: 2020/2/10 09:11:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VersionVO implements Serializable {
    /**
     * 版本名称
     */
    private String version;

    /**
     * 是否被选中
     */
    private Boolean selected;

}
