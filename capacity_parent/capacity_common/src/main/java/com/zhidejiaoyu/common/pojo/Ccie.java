package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Ccie extends Model<Ccie> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long unitId;

    private String studentName;

    private Date getTime;

    /**
     * 测试名称：1:单元闯关，2:复习测试，3:已学测试，4:生词测试，5:熟词测试，6：五维测试
     */
    private Integer testType;

    /**
     * 学习模块，0 : 单词图鉴 ; 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写；7: 五维测试
     */
    private Integer studyModel;

    /**
     * 规则：
     * N+年号+月+序列号（四位）<br>
     * 例如n201808281234<br>
     * 首字母取证书名称首个拼音首字母
     */
    private String ccieNo;

    /**
     * 鼓励语：名列前茅
     */
    private String encourageWord;

    /**
     * 学生是否查看过该证书 1：已查看；0：未查看
     */
    private Integer readFlag;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}