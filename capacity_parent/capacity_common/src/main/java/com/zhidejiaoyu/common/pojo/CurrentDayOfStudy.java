package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zdjy
 * @since 2020-06-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CurrentDayOfStudy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 二维码的序号
     */
    private Integer qrCodeNum;

    /**
     * 老师拍照上传的图片路径
     */
    private String imgUrl;

    /**
     * 当天获取的总金币数
     */
    private Integer gold;

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
    private String studyModel;

    /**
     * 易错单词，多个单词间用##隔开
     */
    private String word;

    /**
     * 易错句型；多个句型间用##隔开
     */
    private String sentence;

    /**
     * 易错课文，多个课文间用##隔开
     */
    private String text;

    /**
     * 错误语法，多个语法间用##隔开
     */
    private String syntax;

    /**
     * 错误的考题，多个考题间用##隔开，只统计选择和填空的答错试题
     */
    private String test;

    /**
     * 创建时间
     */
    private Date createTime;

}
