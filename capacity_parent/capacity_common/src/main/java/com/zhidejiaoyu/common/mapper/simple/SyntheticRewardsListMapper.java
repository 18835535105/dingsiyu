package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SyntheticRewardsList;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 合成奖励表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface SyntheticRewardsListMapper extends BaseMapper<SyntheticRewardsList> {

    /**
     * 搜索全部手套或印记
     * @param studentId
     * @return
     */
    List<SyntheticRewardsList> getGloveOrFlower(Integer studentId);


    /**
     * 搜索手套和印记
     * @return
     */
    List<HashMap<String,Object>> selListMap(Map map);


    /**
     * 查找学生 名称下的所有name数量
     * @param syntheticRewardsList
     * @return
     */
    Integer selCountByStudentIdAndName(SyntheticRewardsList syntheticRewardsList);

    /**
     * 修改所有未使用的数据
     */
    Integer updUse(SyntheticRewardsList syntheticRewardsList);

    /**
     * 根据学生id和名称查询数据
     * @param list
     * @return
     */
    List<SyntheticRewardsList> selGloveOrFlowerByStudentIdAndName(SyntheticRewardsList list);


    @Select("select img_url,name from synthetic_rewards_list where student_id=#{studentId} and model = 1 order by id desc limit 0,4  ")
    List<Map<String,Object>> selSurprised(Integer studentId);


    /**
     * 获取正在使用的手套和印记
     * @return
     */
    Map<String,Object> selGloveOrFlowerByStudentIdAndNameByState(Map map);


    SyntheticRewardsList getUseGloveOrFlower(Integer studentId);

    SyntheticRewardsList getIsUse(Integer studentId, String name);

    List<String> getHaveGloveOrFlower(Integer studentId, Integer sex);
}
