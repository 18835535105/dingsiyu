package com.zhidejiaoyu.common.Vo.syntax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 知识点相关数据
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 14:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgePointVO implements Serializable {

    /**
     * 语法名
     */
    private String syntaxName;

    /**
     * 语法内容
     */
    private String content;
}
