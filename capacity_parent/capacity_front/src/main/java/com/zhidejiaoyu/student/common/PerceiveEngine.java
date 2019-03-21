package com.zhidejiaoyu.student.common;

/**
 * 认知引擎计算公共类
 *
 * @author wuchenxi
 * @date 2018/10/27
 */
public class PerceiveEngine {

    /**
     * 获取当前单词的认知引擎级别
     *
     * <ul>
     * 设定认知引擎为x
     * <p>
     * X=认知难度*认知强度（不计算百分制，例如100%，只取100）
     *
     * <li>0≤x≤10，认知引擎为1.</li>
     *
     * <li>10&lt;x≤20，认知引擎为2.</li>
     *
     * <li>20&lt;x≤30，认知引擎为3.</li>
     *
     * <li>30&lt;x≤40，认知引擎为4.</li>
     *
     * <li>40&lt;x≤50，认知引擎为5.</li>
     *
     * <li>X>50,认知引擎为6.</li>
     * </ul>
     *
     * @param memoryDifficult 记忆难度
     * @param memoryStrength  记忆强度
     * @return
     */
    public static int getPerceiveEngine(int memoryDifficult, double memoryStrength) {
        double v = memoryDifficult * memoryStrength * 100;

        if (v >= 0 && v <= 10) {
            return 1;
        }
        if (v > 10 && v <= 20) {
            return 2;
        }
        if (v > 20 && v <= 30) {
            return 3;
        }
        if (v > 30 && v <= 40) {
            return 4;
        }
        if (v > 40 && v <= 50) {
            return 5;
        }
        if (v > 50) {
            return 6;
        }
        return 1;
    }
}
