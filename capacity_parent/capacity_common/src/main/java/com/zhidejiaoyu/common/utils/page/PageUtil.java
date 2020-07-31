package com.zhidejiaoyu.common.utils.page;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

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
        HttpServletRequest request;
        try{
            request = HttpUtil.getHttpServletRequest();
        } catch (Exception e) {
            return 1;
        }
        String pageNum = "pageNum";
        if (StringUtils.isNotEmpty(request.getParameter(pageNum))) {
            return Integer.parseInt(request.getParameter(pageNum));
        }
        return 1;
    }

    /**
     * 获取当前页需要展示的数据量
     *
     * @return
     */
    public static int getPageSize() {
        HttpServletRequest request;
        try {
            request = HttpUtil.getHttpServletRequest();
        } catch (Exception e) {
            return 20;
        }
        String pageSize = "pageSize";
        if (StringUtils.isNotEmpty(request.getParameter(pageSize))) {
            return Integer.parseInt(request.getParameter(pageSize));
        }
        return 20;
    }

    /**
     * 封装响应的分页数据
     *
     * @param list  数据列表
     * @param total 数据总量
     * @return
     */
    public static <T> PageVo<T> packagePage(List<T> list, Long total) {
        if (list == null) {
            list = Collections.emptyList();
        }
        return new PageVo<>(list, (int) Math.ceil(total * 1.0 / PageUtil.getPageSize()), total);
    }

    /**
     * 封装响应的分页数据
     *
     * @param list  数据列表
     * @param total 数据总量
     * @return
     */
    public static <T> PageVo<T> packagePage(List<T> list, int total) {
        return packagePage(list, Long.valueOf(String.valueOf(total)));
    }

    /**
     * 获取页面结束偏移量
     * 比如每页显示10条数据，第2页偏移量就是第20条数据结束
     *
     * @return
     */
    public static int getEndOffset() {
        return (getPageNum() - 1) * getPageSize() + getPageSize();
    }

    /**
     * 获取页面开始偏移量
     * 比如每页显示10条数据，第2页偏移量就是从第11条数据开始
     *
     * @return
     */
    public static int getStartOffset() {
        return getEndOffset() - getPageSize() + 1;
    }


}
