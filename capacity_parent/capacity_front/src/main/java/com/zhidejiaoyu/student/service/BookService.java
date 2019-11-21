package com.zhidejiaoyu.student.service;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.bookVo.BookVo;
import com.zhidejiaoyu.common.pojo.Player;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.Vo.bookVo.BookInfoVo;
import com.zhidejiaoyu.common.Vo.bookVo.PlayerVo;

import javax.servlet.http.HttpSession;

/**
 * 单词本和句子本相关信息获取及保存
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午4:21:01
 */
public interface BookService extends BaseService<Vocabulary> {

    /**
     * 获取单词本/句子本列表信息
     *
     * @param session
     * @param courseId
     * @param unitId    单元id
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
    ServerResponse<BookInfoVo> getBookInfo(HttpSession session, String courseId, String unitId, String studyModel);

    /**
     * 获取单词播放机、句子播放机播放内容和答案列表
     *
     * @param session  session
     * @param courseId 当前课程id
     * @param unitId   当前单元id
     * @param order    单词播放顺序(默认顺序播放) 1：顺序播放；2：随机播放；3：倒序播放
     * @return
     */
    ServerResponse<PlayerVo> getPlayer(HttpSession session, Long courseId, Long unitId, Integer order);

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
     * 保存播放机学习记录
     *
     * @param session
     * @param player
     * @return
     */
    ServerResponse savePlayer(HttpSession session, Player player);

    /**
     * 获取单词本中单词播放机内容
     *
     * @param session
     * @param unitId
     * @param type
     * @param order
     * @return
     */
    ServerResponse<PlayerVo> getBookPlayer(HttpSession session, Long unitId, Integer type, Integer order);
}
