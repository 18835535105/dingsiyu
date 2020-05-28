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
 *
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LearnExtend extends Model<LearnExtend> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 校长id
     */
    private Long schoolAdminId;
    /**
     * 学习表id
     */
    private Long learnId;
    /**
     * 单词/句型/语法/音标id
     */
    private Long wordId;
    /**
     * 掌握状态：0:生词;1:熟词；
     */
    private Integer status;
    private Date learnTime;
    /**
     * 学习模块
     */
    private String studyModel;
    /**
     * 当前模块学习次数
     */
    private Integer studyCount;
    /**
     * 当前模块首次学习时间
     */
    private Date firstStudyTime;
    /**
     * 首次学习是否是熟词:0:否;1:是
     */
    private Integer firstIsKnow;

    private String flowName;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
