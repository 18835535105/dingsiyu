package com.zhidejiaoyu.student.controller;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.bookVo.BookVo;
import com.zhidejiaoyu.common.pojo.Player;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.BookService;
import com.zhidejiaoyu.common.Vo.bookVo.BookInfoVo;
import com.zhidejiaoyu.common.Vo.bookVo.PlayerVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 单词本和句子本相关信息获取及保存
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午3:57:56
 */

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 获取句子本、单词本顶部信息摘要
     *
     * @param session
     * @param unitId
     * @param studyModel
     * @return
     */
    @GetMapping("/getBookInfo")
    public ServerResponse<BookInfoVo> getBookInfo(HttpSession session, @RequestParam(required = false) String courseId, String unitId, String studyModel) {
        return bookService.getBookInfo(session, courseId, unitId, studyModel);
    }

    /**
     * 获取单词本/句子本列表信息
     *
     * @param session   session
     * @param courseId 当前课程id
     * @param unitId    单元id
     * @param studyModel 慧记忆，慧听写，慧默写，单词图鉴，例句听力，例句翻译，例句默写
     * @param condition 查询条件 1：总单词（默认）；2：生词；3：熟词；4：剩余
     * @return
     */
    @GetMapping("/getWordBookList")
    public ServerResponse<PageInfo<BookVo>> getWordBookList(HttpSession session, Long courseId, Long unitId, String studyModel,
                                                            @RequestParam(name = "condition", defaultValue = "1") Integer condition, Integer pageNum,
                                                            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        if (unitId == null) {
            return ServerResponse.createByErrorMessage("unitId can't be null!");
        }
        if (condition > 4 || condition < 1) {
            return ServerResponse.createByErrorMessage("condition 参数非法！");
        }
        if (pageSize >= 100) {
            return ServerResponse.createByErrorMessage("pageSize 数据过大！");
        }
        return bookService.getWordBookList(session, courseId, unitId, studyModel, condition, pageNum, pageSize);
    }

    /**
     * 获取单词播放机播放内容和答案列表
     * 该接口的单词播放机适用于单词流程中学习单词播放机节点，不适用于单词本中单词播放机
     *
     * @param session session
     * @param courseId 当前课程id
     * @param unitId  当前单元id
     *                @param order 单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     * @return
     */
    @GetMapping("/getPlayer")
    public ServerResponse<PlayerVo> getPlayer(HttpSession session, Long courseId, Long unitId,
                                              @RequestParam(defaultValue = "1") Integer order) {
        if (unitId == null) {
            return ServerResponse.createByErrorMessage("unitId can't be null!");
        }
        return bookService.getPlayer(session, courseId, unitId, order);
    }

    /**
     * 获取单词本中单词播放机、句型播放机内容
     *
     * @param session
     * @param unitId
     * @param type 播放机类型。	2：单词播放机（默认） 3：句子播放机
     * @param order
     * @return 单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     */
    @GetMapping("/getBookPlayer")
    public ServerResponse<PlayerVo> getBookPlayer(HttpSession session, Long unitId,
                                                  @RequestParam(required = false, defaultValue = "2") Integer type,
                                                  @RequestParam(defaultValue = "1") Integer order) {
        if (unitId == null) {
            return ServerResponse.createByErrorMessage("unitId can't be null!");
        }
        return bookService.getBookPlayer(session, unitId, type, order);
    }

    /**
     * 保存播放机学习记录
     *
     * @param session
     * @param player
     * @return
     */
    @PostMapping("/savePlayer")
    public ServerResponse savePlayer(HttpSession session, Player player) {
        return bookService.savePlayer(session, player);
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

    public static void main(String[] args) {

        final String text = "QmFzZTY0IOa1i+ivlQ==";

        final String encoded = Base64
                .getEncoder()
                .encodeToString( text.getBytes( StandardCharsets.UTF_8 ) );
        System.out.println( encoded );

        final String decoded = new String(
                Base64.getDecoder().decode( encoded ),
                StandardCharsets.UTF_8 );
        System.out.println( decoded );

    }

}
