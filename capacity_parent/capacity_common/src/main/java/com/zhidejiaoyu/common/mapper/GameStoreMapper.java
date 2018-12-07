package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.GameStore;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface GameStoreMapper extends BaseMapper<GameStore> {

    /**
     * 从当前正在学习的课程已学习的单词中随机查找15个单词
     *
     * @param stuId
     * @return key word, String wordChinese
     */
    @Select("SELECT v.word_chinese wordChinese,v.word FROM vocabulary v,learn l WHERE v.delStatus=1 AND v.id=l.vocabulary_id AND l.course_id=(SELECT course_id FROM learn WHERE student_id=#{stuId} ORDER BY id DESC LIMIT 1) GROUP BY v.id ORDER BY rand() LIMIT 15")
    List<Map<String, String>> selectGameOneSubjects(Long stuId);

    /**
     * 获取第一个游戏游戏名称
     *
     * @return
     */
    @Select("select game_name from game_store order by id asc limit 1")
    String selectFirstGameName();
}