package com.zhidejiaoyu.common.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * 入参校验工具类
 *
 * @author wuchenxi
 * @date 2018/8/3
 */
public class ValidateUtil {

    /**
     * 如果入参非法，返回错误信息，否则返回ok字符串
     *
     * @param result
     * @return
     */
    public static String validate(BindingResult result) {
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            StringBuilder stringBuilder = new StringBuilder();
            for (ObjectError error : list) {
                stringBuilder.append(error.getDefaultMessage()).append("</br>");
            }
            return stringBuilder.toString();
        }
        return "ok";
    }

}
