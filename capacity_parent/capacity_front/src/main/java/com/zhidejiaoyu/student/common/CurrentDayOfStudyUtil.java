package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class CurrentDayOfStudyUtil {


    /**
     * 保存学习信息
     *
     * @param attributeStr
     * @param field
     */
    public static void saveSessionCurrent(String attributeStr, Long field) {
        HttpSession session = HttpUtil.getHttpSession();
        session.setAttribute(attributeStr, field);
    }

    /**
     * 查找最后的错误信息
     *
     * @param attributeStr
     * @return
     */
    public static long getSessionCurrent(String attributeStr) {
        Object attribute = HttpUtil.getHttpSession().getAttribute(attributeStr);
        if (attribute == null) {
            return 0L;
        }
        return Long.parseLong(attribute.toString());
    }

}
