package com.zhidejiaoyu.common.utils.page;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuchenxi
 * @date 2019-07-24
 */
public class PageUtil {

    /**
     * 获取当前是第几页
     *
     * @return
     */
    public static int getPageNum() {
        HttpServletRequest request = HttpUtil.getHttpServletRequest();
        if (StringUtils.isNotEmpty(request.getParameter("pageNum"))) {
            return Integer.valueOf(request.getParameter("pageNum"));
        } else {
            return 1;
        }
    }

    /**
     * 获取当前页需要展示的数据量
     *
     * @return
     */
    public static int getPageSize() {
        HttpServletRequest request = HttpUtil.getHttpServletRequest();
        if (StringUtils.isNotEmpty(request.getParameter("pageSize"))) {
            return Integer.valueOf(request.getParameter("pageSize"));
        } else {
            return 20;
        }
    }

    /**
     * 封装响应的分页数据
     *
     * @param info
     * @return
     */
    public static PageVo packagePage(PageInfo info) {
        if (info == null) {
            return new PageVo();
        }

        return new PageVo(info.getList(), info.getPages(), info.getTotal());
    }
}
