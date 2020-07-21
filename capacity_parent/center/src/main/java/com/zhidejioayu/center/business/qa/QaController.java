package com.zhidejioayu.center.business.qa;

import com.zhidejioayu.center.business.qa.service.QaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/qa")
public class QaController {

    @Resource
    private QaService qaService;

    /**
     * 导入校区时间
     * @param file
     * @return
     */
    @RequestMapping("/importQa")
    public Object importQa(MultipartFile file) {
        qaService.importQa(file);
        return 200;
    }





}
