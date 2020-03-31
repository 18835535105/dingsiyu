package com.zhidejiaoyu.student.business.index.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.DurationUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.IndexService;
import com.zhidejiaoyu.student.business.index.vo.ClickPictureVO;
import com.zhidejiaoyu.student.business.index.vo.NeedReviewCountVO;
import com.zhidejiaoyu.student.business.index.vo.WordIndexVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2019/12/21 14:26:26
 */
@Slf4j
@Service
public class IndexServiceImpl extends BaseServiceImpl<VocabularyMapper, Vocabulary> implements IndexService {

    @Resource
    private RedisOpt redisOpt;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private StudentMapper studentMapper;

    @Override
    public ServerResponse<Object> index(HttpSession session) {
        Student student = super.getStudent(session);

        // 封装返回数据
        WordIndexVO.WordIndexVOBuilder voBuilder = WordIndexVO.builder();

        // 封装学生相关信息
        this.packageStudentInfo(student, voBuilder);

        // 封装时长信息
        this.getIndexTime(session, student, voBuilder);

        return ServerResponse.createBySuccess(voBuilder.build());
    }

    @Override
    public ServerResponse<Object> clickPortrait(HttpSession session) {

        Student student = getStudent(session);
        Long studentId = student.getId();

        // 获取我的总金币
        int myGold = (int) BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());

        ClickPictureVO.ClickPictureVOBuilder clickPictureVoBuilder = ClickPictureVO.builder()
                .sex(student.getSex())
                .myGold(myGold)
                .myThisGold(this.getTodayGold(studentId));


        this.getMyLevelInfo(clickPictureVoBuilder, myGold, redisOpt);

        return ServerResponse.createBySuccess(clickPictureVoBuilder.build());
    }

    @Override
    public Object getNeedReviewCount(HttpSession session) {
        Student student = getStudent(session);
        Integer count = studentMapper.getVocabularyCountByStudent(student.getId());
        Integer sentenceCount = studentMapper.getSentenceCountByStudent(student.getId());

        return ServerResponse.createBySuccess(NeedReviewCountVO.builder()
                .partUrl(GetOssFile.getPublicObjectUrl(student.getPartUrl()))
                .vocabularyCount(count)
                .sentenceCount(sentenceCount)
                .build());
    }

    /**
     * 封装今日得到的金币数
     *
     * @param studentId
     */
    private double getTodayGold(Long studentId) {
        Integer todayGold = goldLogMapper.sumTodayAddGold(studentId);
        return todayGold == null ? 0.0 : todayGold;
    }

    /**
     * 封装我的等级信息
     *
     * @param builder
     * @param myGold
     */
    private void getMyLevelInfo(ClickPictureVO.ClickPictureVOBuilder builder, int myGold, RedisOpt redisOpt) {
        // 获取等级规则
        List<Map<String, Object>> levels = redisOpt.getAllLevel();

        int myrecord = 0;
        // 下一等级索引
        int j = 1;
        int size = levels.size();
        for (int i = 0; i < size; i++) {
            // 循环的当前等级分数
            int levelGold = (int) levels.get(i).get("gold");
            // 下一等级分数
            int nextLevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");
            // 下一等级索引
            int si = (i + 1) < size ? (i + 1) : i;
            boolean flag = (myGold >= myrecord && myGold < nextLevelGold) || j == size;
            if (flag) {
                builder.childName(String.valueOf(levels.get(i).get("child_name")))
                        .jap(nextLevelGold - myGold)
                        .imgUrl(AliyunInfoConst.host + levels.get(i).get("img_url"))
                        .childNameBelow(String.valueOf(levels.get(si).get("child_name")))
                        .japBelow(nextLevelGold)
                        .imgUrlBelow(AliyunInfoConst.host + levels.get(si).get("img_url"));
                break;
            }
            myrecord = levelGold;
            j++;
        }
    }

    private void getIndexTime(HttpSession session, Student student, WordIndexVO.WordIndexVOBuilder builder) {
        // 有效时长
        int valid = (int) DurationUtil.getTodayValidTime(session);
        // 在线时长
        int online = (int) DurationUtil.getTodayOnlineTime(session);
        String efficiency;
        // 今日学习效率
        if (valid >= online) {
            log.warn("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            valid = online - 1;
            efficiency = "99%";
        } else {
            efficiency = LearnTimeUtil.efficiency(valid, online);
        }
        builder.efficiency(efficiency)
                .online(online)
                .valid(valid);
    }

    /**
     * 封装学生相关信息
     *
     * @param student
     * @param builder
     */
    private void packageStudentInfo(Student student, WordIndexVO.WordIndexVOBuilder builder) {
        // 学生id
        builder.studentId(student.getId())
                .role(1)
                .account(student.getAccount())
                .StudentName(student.getStudentName())
                .headUrl(GetOssFile.getPublicObjectUrl(student.getHeadUrl()))
                .partUrl(GetOssFile.getPublicObjectUrl(student.getPartUrl()))
                .petName(student.getPetName())
                .schoolName(student.getSchoolName());
    }
}
