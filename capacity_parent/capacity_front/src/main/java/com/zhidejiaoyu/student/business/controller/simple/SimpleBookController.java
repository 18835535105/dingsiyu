package com.zhidejiaoyu.student.business.controller.simple;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.vo.bookVo.BookInfoVo;
import com.zhidejiaoyu.common.vo.bookVo.BookVo;
import com.zhidejiaoyu.common.vo.bookVo.PlayerVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleBookServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 单词本和句子本相关信息获取及保存
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午3:57:56
 */

@RestController
@RequestMapping("/api/book")
public class SimpleBookController {

    @Autowired
    private SimpleBookServiceSimple bookService;

    /**
     * 获取单词本顶部信息摘要
     *
     * @param session
     * @param type 模块类型
     * @return
     */
    @GetMapping("/getBookInfo")
    public ServerResponse<BookInfoVo> getBookInfo(HttpSession session, Long courseId,
                                                  @RequestParam(required = false, defaultValue = "1") Integer type) {
        if (courseId == null) {
            return ServerResponse.createByError(300, "无");
        }
        return bookService.getBookInfo(session, courseId, type);
    }

    /**
     * 获取单词本/句子本列表信息
     *
     * @param session   session
     * @param courseId 当前课程id
     * @param condition 查询条件 1：总单词（默认）；2：生词；3：熟词；4：剩余
     * @return
     */
    @GetMapping("/getWordBookList")
    public ServerResponse<PageInfo<BookVo>> getWordBookList(HttpSession session, Long courseId, Integer type,
                                                            @RequestParam(name = "condition", defaultValue = "1") Integer condition,
                                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                                            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        if (courseId == null) {
            return ServerResponse.createByErrorMessage("courseId can't be null!");
        }
        if (condition > 4 || condition < 1) {
            return ServerResponse.createByErrorMessage("condition 参数非法！");
        }
        if (pageSize >= 100) {
            return ServerResponse.createByErrorMessage("pageSize 数据过大！");
        }
        return bookService.getWordBookList(session, courseId, type, condition, pageNum, pageSize);
    }

    /**
     * 获取单词播放机、句子播放机播放内容和答案列表
     *
     * @param session session
     * @param unitId 当前单元id
     * @param type    1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @param order 单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     * @return
     */
    @GetMapping("/getPlayer")
    public ServerResponse<PlayerVo> getPlayer(HttpSession session, Long unitId, @RequestParam(defaultValue = "1") Integer type,
                                              @RequestParam(defaultValue = "1") Integer order) {
        if (unitId == null) {
            throw new RuntimeException("unitId = null");
        }
        return bookService.getPlayer(session, unitId, type, order);
    }

    /**
     * 再学一遍
     * 点击再学一遍按钮，将当前学生当前课程的所有单词和例句置为生词和生句，并将学习遍数在原基础上+1
     *
     * @param session
     * @param courseId 课程id
     * @return
     */
    @PostMapping("/studyAgain")
    public ServerResponse<String> studyAgain(HttpSession session, Long courseId) {
        if (courseId == null) {
            return ServerResponse.createByErrorMessage("courseId can't be null!");
        }
        return bookService.studyAgain(session, courseId);
    }

    /**
     * 选中指定单词重新学习，将选中的单词置为生词
     *
     * @param session    session信息
     * @param courseId   课程id
     * @param unitId     单元id
     * @param wordIds    所选单词数组
     * @param classify 学习模块 （0=单词图鉴，1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写）
     * @return ServerResponse<String>
     */
    @PostMapping("/restudy")
    public ServerResponse<String> restudy(HttpSession session, Long courseId, Long unitId, @RequestParam(value = "wordIds[]") Long[] wordIds, Integer classify) {
        Assert.notNull(unitId, "unitId 不能为空！");
        Assert.notNull(courseId, "courseId 不能为空！");
        Assert.notEmpty(wordIds, "wordIds 不能为空！");
        Assert.notNull(classify, "classify 不能为空！");
        return bookService.restudy(session, courseId, unitId, wordIds, classify);
    }

    /**
     * 获取学生可选择的学习模块
     *
     * @param session
     * @return
     */
    @GetMapping("/getModel")
    public ServerResponse getModel(HttpSession session) {
        return this.bookService.getModel(session);
    }
}
