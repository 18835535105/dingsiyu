package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.pojo.TeksNew;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "course", path = "/course/centerTeks")
public interface CenterTeksFeignClient extends CourseFeignClient {

    /**
     * 课文获取数据
     */
    /**
     * 根据单元和group获取课文数据
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selTeksByUnitIdAndGroup/{unitId}/{group}")
    List<TeksNew> selTeksByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取20个句型信息
     *
     * @return
     */
    @GetMapping("/getTwentyTeks")
    List<TeksNew> getTwentyTeks();

    /**
     * 去掉指定字符查询数据
     *
     * @param sentence
     * @return
     */
    @RequestMapping(value = "/replaceTeks", method = RequestMethod.GET)
    TeksNew replaceTeks(@RequestParam("sentence") String sentence);
}
