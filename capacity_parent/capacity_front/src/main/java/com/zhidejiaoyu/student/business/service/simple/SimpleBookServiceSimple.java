package com.zhidejiaoyu.student.business.service.simple;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.vo.bookVo.BookVo;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.bookVo.BookInfoVo;
import com.zhidejiaoyu.common.vo.bookVo.PlayerVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * 单词本和句子本相关信息获取及保存
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午4:21:01
 */
public interface SimpleBookServiceSimple extends SimpleBaseService<Vocabulary> {

    /**
     * 获取单词本/句子本列表信息
     *  @param session
     * @param courseId
     * @param type
     * @param condition 查询条件 1：总单词（默认）；2：生词；3：熟词；4：剩余
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<BookVo>> getWordBookList(HttpSession session, Long courseId,
                                                     Integer type, Integer condition, Integer pageNum, Integer pageSize);

    /**
     * 获取句子本、单词本顶部信息摘要
     *
     * @param session
     * @param courseId
     * @param type 模块类型
     * @return
     */
    ServerResponse<BookInfoVo> getBookInfo(HttpSession session, Long courseId, Integer type);

    /**
     * 获取单词播放机、句子播放机播放内容和答案列表
     *
     * @param session session
     * @param unitId 当前单元id
     * @param type    1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     *      * @param order 单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     *                @param order 单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     * @return
     */
    ServerResponse<PlayerVo> getPlayer(HttpSession session, Long unitId, Integer type, Integer order);

    /**
     * 再学一遍
     *
     * @param session
     * @param courseId 课程id
     * @return
     */
    ServerResponse<String> studyAgain(HttpSession session, Long courseId);

    /**
     * 选中指定单词重新学习，将选中的单词置为生词
     *
     * @param session    session信息
     * @param courseId   课程id
     * @param unitId     单元id
     * @param wordIds    所选单词数组
     * @param studyModel 学习模块 （0=单词图鉴，1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写）
     * @return ServerResponse<String>
     */
    ServerResponse<String> restudy(HttpSession session, Long courseId, Long unitId, Long[] wordIds, Integer studyModel);

    /**
     * 获取学生可选择的学习模块
     *
     * @param session
     * @return
     */
    ServerResponse getModel(HttpSession session);
}