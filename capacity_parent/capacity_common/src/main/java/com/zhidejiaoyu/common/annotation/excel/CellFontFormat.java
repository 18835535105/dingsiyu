package com.zhidejiaoyu.common.annotation.excel;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CellFontFormat {

    String fontName() default "宋体";

    short fontHeightInPoints() default 240;

    IndexedColors fontColor() default IndexedColors.BLACK;

    boolean bold() default false;
}
