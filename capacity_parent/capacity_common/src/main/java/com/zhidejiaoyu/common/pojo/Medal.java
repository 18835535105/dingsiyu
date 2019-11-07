package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class Medal extends Model<Medal> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String parentName;

    private Long nextParent;

    private String childName;

    private Long nextChild;

    /**
     * 任务奖励中勋章进度提示语
     */
    private String markedWords;

    /**
     * 勋章说明
     */
    private String explain;

    private String childImgUrl;

    private String parentImgUrl;

    /**
     * 勋章中金色图片，学生查看等级和勋章页面显示
     */
    private String goldImgUrl;

    /**
     * 勋章中灰色图片，学生查看等级和勋章页面显示
     */
    private String grayImgUrl;

    /**
     * 总进度
     */
    private Integer totalPlan;

    /**
     * 领取勋章时gif图路径
     */
    private String getGifImgUrl;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
