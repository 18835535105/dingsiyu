package com.zhidejiaoyu.common.utils.page;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
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
        if (request == null) {
            return 1;
        }
        if (StringUtils.isNotEmpty(request.getParameter("pageNum"))) {
            return Integer.parseInt(request.getParameter("pageNum"));
        }
        return 1;
    }

    /**
     * 获取当前页需要展示的数据量
     *
     * @return
     */
    public static int getPageSize() {
        HttpServletRequest request = HttpUtil.getHttpServletRequest();
        if (request == null) {
            return 20;
        }
        if (StringUtils.isNotEmpty(request.getParameter("pageSize"))) {
            return Integer.parseInt(request.getParameter("pageSize"));
        }
        return 20;
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
