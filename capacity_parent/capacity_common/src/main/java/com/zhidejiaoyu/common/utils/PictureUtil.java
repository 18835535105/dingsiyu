package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.CourseMapper;
import com.zhidejiaoyu.common.mapper.UnitMapper;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.Unit;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 获取单词图片
 *
 * @author: wuchenxi
 * @Date: 2019-08-27 14:36
 */
@Component
public class PictureUtil {

    private static final String SMALL = "小学";
    private static final String HIGH = "高中";

    public static CourseMapper courseMapper;

    public static UnitMapper unitMapper;

    @Autowired
    private CourseMapper courseMapper1;

    @Autowired
    private UnitMapper unitMapper1;


    @PostConstruct
    public void init() {
        courseMapper = this.courseMapper1;
        unitMapper = this.unitMapper1;
    }

    /**
     * 通过传入单元 id 获取单词图片
     *
     * @param vocabulary
     * @param unitId     单元 id
     * @return
     */
    public static String getPictureByUnitId(Vocabulary vocabulary, Long unitId) {
        return getPictureUrl(vocabulary, null, unitId);
    }

    /**
     * 通过传入课程 id 获取单词图片
     *
     * @param vocabulary
     * @param courseId   课程 id
     * @return
     */
    public static String getPictureByCourseId(Vocabulary vocabulary, Long courseId) {
        return getPictureUrl(vocabulary, courseId, null);
    }

    private static String getPictureUrl(Vocabulary vocabulary, Long courseId, Long unitId) {
        if (vocabulary == null) {
            return "";
        }

        String smallPictureUrl = vocabulary.getSmallPictureUrl();
        String middlePictureUrl = vocabulary.getMiddlePictureUrl();
        String highPictureUrl = vocabulary.getHighPictureUrl();

        if (courseId == null && unitId == null) {
            return GetOssFile.getPublicObjectUrl(getUrl(vocabulary, smallPictureUrl));
        }

        Course course = null;
        if (courseId != null) {
            course = courseMapper.selectById(courseId);
        } else {
            Unit unit = unitMapper.selectById(unitId);
            if (unit != null) {
                course = courseMapper.selectById(unit.getCourseId());
            }
        }
        if (course == null) {
            return GetOssFile.getPublicObjectUrl(getUrl(vocabulary, smallPictureUrl));
        }

        String studyParagraph = course.getStudyParagraph();
        if (StringUtils.isEmpty(studyParagraph)) {
            return GetOssFile.getPublicObjectUrl(getUrl(vocabulary, smallPictureUrl));
        }

        if (SMALL.equals(studyParagraph)) {
            // 小学图片
            return GetOssFile.getPublicObjectUrl(getUrl(vocabulary, smallPictureUrl));
        } else if (HIGH.equals(studyParagraph)) {
            // 高中图片
            return GetOssFile.getPublicObjectUrl(getUrl(vocabulary, highPictureUrl));
        } else {
            // 初中图片
            return GetOssFile.getPublicObjectUrl(getUrl(vocabulary, middlePictureUrl));
        }
    }

    /**
     * 获取图片的 url，传入的图片地址为空，则会从小学、初中、高中的图片地址中找出不为空的地址，如果都为空 返回空字符串
     *
     * @param vocabulary
     * @param pictureUrl
     * @return
     */
    private static String getUrl(Vocabulary vocabulary, String pictureUrl) {
        if (StringUtils.isEmpty(pictureUrl)) {
            if (StringUtils.isNotEmpty(vocabulary.getSmallPictureUrl())) {
                return vocabulary.getSmallPictureUrl();
            }
            if (StringUtils.isNotEmpty(vocabulary.getMiddlePictureUrl())) {
                return vocabulary.getMiddlePictureUrl();
            }
            if (StringUtils.isNotEmpty(vocabulary.getHighPictureUrl())) {
                return vocabulary.getHighPictureUrl();
            }
            return "";
        }
        return pictureUrl;
    }
}
