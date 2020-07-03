package com.zhidejiaoyu.common.vo.student.studentinfowithschool;

import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardCountModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学生充课信息汇总
 *
 * @author: wuchenxi
 * @date: 2020/7/3 18:25:25
 */
@Data
public class ExportRechargePayResultVO implements Serializable {

    private List<ExportRechargePayCardModel> exportRechargePayCardModelList;

    private List<ExportRechargePayCardCountModel> exportRechargePayCardCountModelList;
}
