package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 课前测试习题表
 * </p>
 *
 * @author zdjy
 * @since 2019-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExerciseSheetBeforeClass extends Model<ExerciseSheetBeforeClass> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 题目
     */
    private String subject;
    /**
     * 答案
     */
    private String answer;
    /**
     * 选项
     */
    private String option;
    /**
     * 习题类型
     * 小学：
     * 1---》选出每组单词不同类的一项
     * 2---》写出单词的正确形式
     * 3---》选择正确的答案完成对话
     * 4---》根据题目选择正确答案
     * 5---》连词成句
     * <p>
     * 初高中
     * 6---》短语
     * 7---》根据提示填空，单词字数不限
     * 8---》选择填空
     * 9---》句型转换
     * 10---》句子翻译
     */
    private Integer type;
    /**
     * 课程单元名
     */
    private String jointName;
    /**
     * 知识点名称
     */
    private String points;


    /**
     * 解析
     */
    private String analysis;

    private Date createTime;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
