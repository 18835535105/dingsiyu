package com.zhidejiaoyu.student.BaseUtil.SaveModel;

import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.TeksNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.pojo.TeksNew;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SaveTeksData extends BaseServiceImpl<LearnNewMapper, LearnNew> {

    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private TeksNewMapper teksNewMapper;

    public Object getSudyModel(HttpSession session, Long unitId,
                               Student student, Long studentId,
                               String studyModel, Integer easyOrHard,
                               Integer type) {
        //查看课文数据是否保存过
        List<Long> longs = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, studentId, studyModel);
        if (longs.size() > 0) {
            return super.toUnitTest();
        }
        //获取单元下需要学习的group
        //获取当前单元下的learnId
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitId(studentId, unitId, easyOrHard);
        if (type.equals(11)) {
            return getTeksAuditionData(unitId, learnNews.getGroup());
        }


        return null;

    }

    private Object getTeksAuditionData(Long unitId, Integer group) {

        List<TeksNew> teks = teksNewMapper.selTeksByUnitId(unitId, group);
        if (teks.size() > 0) {
            List<TeksNew> resultTeks = new ArrayList<>();
            int i = 0;
            for (TeksNew teks1 : teks) {
                teks1.setPronunciation(baiduSpeak.getSentencePath(teks1.getSentence().replace("#", " ").replace("$", "")));
                i++;
                teks1.setSentence(teks1.getSentence().replace("#", " ").replace("$", ""));
                resultTeks.add(teks1);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }

        return null;
    }
}
