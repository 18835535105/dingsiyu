package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 单元-例句(中间表)
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UnitSentenceNew extends Model<UnitSentenceNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 单元表主建
     */
    private Long unitId;
    /**
     * 例句表主建
     */
    private Long sentenceId;
    /**
     * 例句翻译
     */
    private String chinese;
    /**
     * 英文干扰项
     */
    private String englishDisturb;
    /**
     * 中文干扰项
     */
    private String chineseDisturb;
    /**
     * 当前单词在单元中的分组序号
     */
    @TableField("`group`")
    private Integer group;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
