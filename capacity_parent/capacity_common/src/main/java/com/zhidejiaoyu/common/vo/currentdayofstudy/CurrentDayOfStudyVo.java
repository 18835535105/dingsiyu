package com.zhidejiaoyu.common.vo.currentdayofstudy;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentDayOfStudyVo implements Serializable {

    /**
     * 当天获取的总金币数
     */
    private Integer gold;

    /**
     * 时间
     */
    private String time;

    /**
     * 有效时常
     */
    private Integer validTime;

    /**
     * 在线时常
     */
    private Integer onlineTime;

    /**
     * 当天学习的模块-单元；格式：模块1-单元1##模块2-单元2...不同模块间用##隔开
     */
    private List<String> studyModel;

    /**
     * 易错单词，多个单词间用##隔开
     */
    private List<String> word;

    /**
     * 易错句型；多个句型间用##隔开
     */
    private List<String> sentence;

    /**
     * 易错课文，多个课文间用##隔开
     */
    private List<String> text;

    /**
     * 错误语法，多个语法间用##隔开
     */
    private List<Object> syntax;

    /**
     * 错误的考题，多个考题间用##隔开，只统计选择和填空的答错试题
     */
    private List<String> test;

    /**
     * 老师拍照上传的图片路径
     */
    private String imgUrl;

    /**
     * 老师对学生的评价
     */
    private String evaluate;

    /**
     * 学生表现（1-5颗星）
     */
    @TableField("`show`")
    private Integer show;

    /**
     * 备注
     */
    @TableField("`comment`")
    private String comment;

    /**
     * 座位号（在家填写0）
     */
    private Integer siteNo;

}
