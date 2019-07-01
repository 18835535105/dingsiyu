package com.zhidejiaoyu.student.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务奖励controller
 *
 * @author wuchenxi
 * @date 2018/6/9 18:19
 */
@Slf4j
@RestController
@RequestMapping("/award")
@Validated
public class AwardController extends BaseController {

}
