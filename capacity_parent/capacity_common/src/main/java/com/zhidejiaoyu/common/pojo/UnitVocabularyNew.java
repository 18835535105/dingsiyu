package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
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
 * 单元-词汇表 (中间表)
导入词汇关联课程单元方法:
1.查询所有课程表中的课程单元拼接名(key=名 value=课程id);  2.保存完词汇,主建返回, 通过课程单元拼接名去map遍历中找一样的key获取value, 把value(课程主建) 和 返回的主建保存到中间表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UnitVocabularyNew extends Model<UnitVocabularyNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 单元表主建
     */
    private Long unitId;
    /**
     * 词汇表主建
     */
    private Long vocabularyId;
    /**
     * 词汇分类，1：重点词汇；2：课标词汇
     */
    private Integer classify;
    /**
     * 单词释义
     */
    private String wordChinese;
    /**
     * 当前单词在单元中的分组序号
     */
    private Integer group;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
