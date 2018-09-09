package com.zhidejiaoyu.student.service;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.vo.BookInfoVo;
import com.zhidejiaoyu.student.vo.BookVo;
import com.zhidejiaoyu.student.vo.PlayerVo;

import javax.servlet.http.HttpSession;

/**
 * 单词本和句子本相关信息获取及保存
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午4:21:01
 */
public interface BookService {

    /**
     * 获取单词本/句子本列表信息
     *
     * @param session
     * @param courseId
     *@param unitId    单元id
     * @param condition 查询条件 1：总单词（默认）；2：生词；3：熟词；4：剩余   @return
     */
    ServerResponse<PageInfo<BookVo>> getWordBookList(HttpSession session, Long courseId, Long unitId, String studyModel,
                                                     Integer condition, Integer pageNum, Integer pageSize);

    /**
     * 获取句子本、单词本顶部信息摘要
     *
     * @param session
     * @param courseId
     * @param unitId
     * @param studyModel
     * @return
     */
    ServerResponse<BookInfoVo> getBookInfo(HttpSession session, Long courseId, Long unitId, String studyModel);

    /**
     * 获取单词播放机、句子播放机播放内容和答案列表
     *
     * @param session session
     * @param courseId 当前课程id
     * @param unitId  当前单元id
     * @param type    播放机类型。	1：单词播放机（默认） 2：句子播放机
     *                @param order 单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     * @return
     */
    ServerResponse<PlayerVo> getPlayer(HttpSession session, Long courseId, Long unitId, Integer type, Integer order);

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
}
