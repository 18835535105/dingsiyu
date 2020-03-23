package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;

/**
 * 词汇
 *
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Vocabulary extends Model<Vocabulary> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 单词
     */
    private String word;

    /**
     * 公共单词中文意思
     */
    private String wordChinese;

    /**
     * 例句英文
     */
    @TableField("centreExample")
    private String exampleEnglish;

    /**
     * 例句中文
     */
    private String exampleChinese;

    /**
     * 初中例句英文
     */
    @TableField("centreExample")
    private String centreExample;

    /**
     * 初中例句中文
     */
    @TableField("centreTranslate")
    private String centreTranslate;

    /**
     * 高中例句英文
     */
    @TableField("tallExample")
    private String tallExample;

    /**
     * 高中例句中文
     */
    @TableField("tallTranslate")
    private String tallTranslate;

    /**
     * 拓展
     */
    private String explain_;

    /**
     * 用法
     */
    private String upage;

    /**
     * 搭配
     */
    @TableField("`match`")
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
     * 开启状态中文
     */
    @TableField(exist = false)
    private String stat;

    /**
     * 创建时间
     */
    private String createTime;

    private String updateTime;

    private String courseUnit;

    @TableField(exist = false)
    private String course_id;

    @TableField(exist = false)
    private String joint_name;

    @TableField(exist = false)
    private String strClassify;

    @TableField(exist = false)
    private Double memory_strength;

    /**
     * 音节
     */
    private String syllable;

    /**
     * 删除 1 = 开启（默认）， 2 = 关闭
     */
    @TableField("delStatus")
    private Integer delStatus;

    /**
     * 音标
     */
    @TableField("sound_mark")
    private String soundMark;

    private String readUrl;

    /**
     * 小学单词图片地址
     */
    @TableField("small_picture_url")
    private String smallPictureUrl;

    /**
     * 初中单词图片地址
     */
    @TableField("middle_picture_url")
    private String middlePictureUrl;

    /**
     * 高中单词图片地址
     */
    @TableField("high_picture_url")
    private String highPictureUrl;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
