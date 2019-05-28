package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 播放机学习记录表
 * </p>
 *
 * @author zdjy
 * @since 2018-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Player extends Model<Player> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long unitId;
    /**
     * 单词或例句id
     */
    private Long wordId;
    /**
     * 1:单词流程中播放机学习记录；2：单词本中播放机学习记录；3：句型本中播放机学习记录；4：字母播放器
     */
    private Integer type;
    private Date updateTime;

    private Integer learnCount;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
