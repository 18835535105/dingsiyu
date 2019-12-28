package com.zhidejiaoyu.common.study;

import com.zhidejiaoyu.common.mapper.StudentRestudyMapper;
import com.zhidejiaoyu.common.pojo.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 学生复习记录工具类
 *
 * @author: wuchenxi
 * @Date: 2019/10/12 10:54
 */
@Component
public class StudentRestudyUtil {

    @Resource
    private StudentRestudyMapper studentRestudyMapper;

    /**
     * 保存单词的复习记录
     *
     * @param learn
     * @param word    单词
     * @param version 1:提分版；2：同步版
     */
    public void saveWordRestudy(Learn learn, Student student, String word, int version) {
        studentRestudyMapper.insert(StudentRestudy.builder()
                .courseId(learn.getCourseId())
                .unitId(learn.getUnitId())
                .studentId(student.getId())
                .type(1)
                .version(version)
                .word(word)
                .vocabularyId(learn.getVocabularyId())
                .updateTime(new Date())
                .build());
    }

    /**
     * 保存单词的复习记录
     *
     * @param learn
     * @param word    单词
     * @param version 1:提分版；2：同步版
     */
    public void saveWordRestudy(LearnNew learn, LearnExtend learnExtend, Student student, String word, int version) {
        studentRestudyMapper.insert(StudentRestudy.builder()
                .courseId(learn.getCourseId())
                .unitId(learn.getUnitId())
                .studentId(student.getId())
                .type(1)
                .version(version)
                .word(word)
                .vocabularyId(learnExtend.getWordId())
                .updateTime(new Date())
                .build());
    }

    /**
     * 保存句型的复习记录
     *
     * @param learn
     * @param sentence 句子
     * @param version  1:提分版；2：同步版
     */
    public void saveSentenceRestudy(LearnNew learn, LearnExtend learnExtend, Student student, String sentence, int version) {
        studentRestudyMapper.insert(StudentRestudy.builder()
                .courseId(learn.getCourseId())
                .unitId(learn.getUnitId())
                .studentId(student.getId())
                .type(2)
                .version(version)
                .word(sentence)
                .vocabularyId(learnExtend.getWordId())
                .updateTime(new Date())
                .build());
    }

    /**
     * 保存句型的复习记录
     *
     * @param learn
     * @param sentence 句子
     * @param version  1:提分版；2：同步版
     */
    public void saveSentenceRestudy(Learn learn, Student student, String sentence, int version) {
        studentRestudyMapper.insert(StudentRestudy.builder()
                .courseId(learn.getCourseId())
                .unitId(learn.getUnitId())
                .studentId(student.getId())
                .type(2)
                .version(version)
                .word(sentence)
                .vocabularyId(learn.getExampleId() == null ? learn.getVocabularyId() : learn.getExampleId())
                .updateTime(new Date())
                .build());
    }
}
