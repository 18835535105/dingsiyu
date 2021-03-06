package com.zhidejiaoyu.common.utils.page;

import lombok.Data;

import java.util.List;

/**
 * 分页数据
 *
 * @author wuchenxi
 * @date 2019-07-25
 */
@Data
public class PageVo<T> {

    /**
     * 数据
     */
    private List<T> rows;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 总数据量
     */
    private Long total;

    public PageVo () {}

    public PageVo (List<T> rows, Integer pages, Long total) {
        this.rows = rows;
        this.pages = pages;
        this.total = total;
    }
}
