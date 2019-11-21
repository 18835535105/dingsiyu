package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 词汇（单词）表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VocabularyNew extends Model<VocabularyNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 单词
     */
    private String word;
    /**
     * 单词中文意思
     */
    private String wordChinese;
    /**
     * 例句英文 , 清学智能版使用该字段
     */
    private String exampleEnglish;
    /**
     * 例句中文 , 清学智能版使用该字段
     */
    private String exampleChinese;
    /**
     * 初中例句英文 , 没用该列
     */
    @TableField("centreExample")
    private String centreExample;
    /**
     * 初中例句翻译 , 没用该列
     */
    @TableField("centreTranslate")
    private String centreTranslate;
    /**
     * 高中例句英文 , 没用该列
     */
    @TableField("tallExample")
    private String tallExample;
    /**
     * 高中例句翻译 , 没用该列
     */
    @TableField("tallTranslate")
    private String tallTranslate;
    /**
     * 扩展
     */
    private String explain;
    private Date createTime;
    private Date updateTime;
    /**
     * 导入数据需要所属课程(课程单元拼接名)这个字段
     */
    private String courseUnit;
    /**
     * 该列在'unit_vocabulary表中使用'   分类  1 = 重点词汇, 2 = 课标词汇(非重点), 0 = 不确定
     */
    private Integer classify;
    /**
     * 用法
     */
    private String upage;
    /**
     * 搭配
     */
    private String match;
    /**
     * 联想
     */
    private String think;
    /**
     * 助记
     */
    private String record;
    /**
     * 助记图片名
     */
    private String recordpicname;
    /**
     * 助记图片url地址
     */
    private String recordpicurl;
    /**
     * 辨析
     */
    private String discriminate;
    /**
     * 单词删除状态 1=开启（默认），2=关闭
     */
    @TableField("delStatus")
    private Integer delStatus;
    /**
     * 音节
     */
    private String syllable;
    /**
     * 单词音标
     */
    private String soundMark;
    /**
     * 单词读音地址
     */
    private String readUrl;
    /**
     * 小学单词图片地址
     */
    private String smallPictureUrl;
    /**
     * 初中单词图片地址
     */
    private String middlePictureUrl;
    /**
     * 高中单词图片地址
     */
    private String highPictureUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
