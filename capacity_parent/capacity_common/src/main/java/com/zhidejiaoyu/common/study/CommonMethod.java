package com.zhidejiaoyu.common.study;

import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.LearnExample;
import com.zhidejiaoyu.common.pojo.SimpleCapacity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 学生用户使用过程中重复使用的功用方法
 *
 * @author wuchenxi
 * @date 2018/5/21 15:34
 */
@Slf4j
@Component
public class CommonMethod implements Serializable {

    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    /**
     * 判断学生是否是第一次学习指定的模块
     * true:第一次学习当前模块，进入引导页
     * false：不是第一次学习当前模块，直接获取题目
     *
     * @param stuId      当前登录学生id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @return
     */
    public boolean isFirst(Long stuId, String studyModel) {
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andStudyModelEqualTo(studyModel).andTypeEqualTo(1);
        List<Learn> learns = learnMapper.selectByExample(learnExample);
        return learns.size() == 0;
    }

    /**
     * 学生第一次学习完成学习引导页内容后，将该学生新信息置为不是第一次学习，可直接进入学习页面
     *
     * @param stuId      当前登录学生id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     */
    public void clearFirst(Long stuId, String studyModel) {
        Learn learn = new Learn();
        learn.setStudentId(stuId);
        learn.setStudyModel(studyModel);
        learnMapper.insert(learn);
    }

    /**
     * 获取当前年级所在的学段
     *
     * @param grade 当前年级
     * @return 初中 高中
     */
    public static String getPhase(String grade) {
        String phase = null;
        switch (grade) {
            case "七年级":
            case "八年级":
            case "九年级":
                phase = "初中";
                break;

            case "高一":
            case "高二":
            case "高三":
            case "高中":
                phase = "高中";
                break;
            default:
        }
        return phase;
    }

    /**
     * 计算记忆追踪中字体大小等级
     *
     * @param object
     * @return -1:数据有误，其他：字体等级
     */
    public int getFontSize(Object object) {
        try {
            double memoryStrength;
            Date push;
            if (object instanceof SimpleCapacity) {
                SimpleCapacity simpleCapacity = (SimpleCapacity) object;
                memoryStrength = simpleCapacity.getMemoryStrength();
                push = simpleCapacity.getPush();
            } else {
                memoryStrength = (Double) memoryDifficultyUtil.getFieldValue(object, object.getClass().getField("memoryStrength"));
                push = (Date) memoryDifficultyUtil.getFieldValue(object, object.getClass().getField("push"));
            }

           if (push == null) {
                return 0;
            }
            double timeLag = this.timeLag(push);
            if (timeLag < 0) {
                return 0;
            } else if ((memoryStrength >= 0 && memoryStrength < 0.1) || timeLag > 10) {
                return 10;
            } else if ((memoryStrength >= 0.1 && memoryStrength < 0.2) || timeLag > 9) {
                return 9;
            } else if ((memoryStrength >= 0.2 && memoryStrength < 0.3) || timeLag > 8) {
                return 8;
            } else if ((memoryStrength >= 0.3 && memoryStrength < 0.4) || timeLag > 7) {
                return 7;
            } else if ((memoryStrength >= 0.4 && memoryStrength < 0.5) || timeLag > 6) {
                return 6;
            } else if ((memoryStrength >= 0.5 && memoryStrength < 0.6) || timeLag > 5) {
                return 5;
            } else if ((memoryStrength >= 0.6 && memoryStrength < 0.7) || timeLag > 4) {
                return 4;
            } else if ((memoryStrength >= 0.7 && memoryStrength < 0.8) || timeLag > 3) {
                return 3;
            } else if ((memoryStrength >= 0.8 && memoryStrength < 0.9) || timeLag > 2) {
                return 2;
            } else if ((memoryStrength >= 0.9 && memoryStrength < 1) || timeLag > 1) {
                return 1;
            }
        } catch (Exception e) {
            log.warn("获取字体大小出错！", e);
        }
        return 0;
    }

    /**
     * 计算当前时间黄金记忆点时间差
     *
     * @param push 黄金记忆点时间
     * @return <0:未到达黄金记忆点； >0:超过黄金记忆点的分钟
     */
    private double timeLag(Date push) {
        long pushTime = push.getTime();
        long nowTime = System.currentTimeMillis();
        return (nowTime * 1.0 - pushTime) / 60000;
    }

    /**
     * 将例句顺序分割，其中的标点单独占用一个下标
     *
     * @param sentence 需要处理的例句
     * @return
     */
    public List<String> getEnglishList(String sentence) {
        for (String s : POINT) {
            if (sentence.contains(s)) {
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", " . ");
                    if (sentence.substring(sentence.length() - 1).equals(".")) {
                        sentence = sentence.substring(0, sentence.length() - 1);
                        sentence = sentence + " .";
                    }
                } else {
                    sentence = sentence.replace(s, " " + s);
                }
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split(" ");
        for (String s : arr) {
            if ("".equals(s)) {
                continue;
            }
            if (s.contains("#") || s.contains("*") || s.contains("$")) {
                list.add(s.replace("#", " ").replace("*", " ").replace("$", ""));
            } else {
                list.add(s.trim());
            }
        }
        return list;
    }

    /**
     * 将例句单词顺序打乱
     *
     * @param sentence
     * @param exampleDisturb 例句英文干扰项  为空时无干扰项
     * @return
     */
    public List<String> getOrderEnglishList(String sentence, String exampleDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", " ");
                    if (sentence.substring(sentence.length() - 1).equals(".")) {
                        sentence = sentence.substring(0, sentence.length() - 1);
                    }
                } else {
                    sentence = sentence.replace(s, "");
                }
            }
        }
        // 将例句按照空格拆分
        String[] words = sentence.split(" ");

        List<String> list = new ArrayList<>();
        // 去除固定搭配中的#
        for (int i = 0; i < words.length; i++) {
            if ("".equals(words[i])) {
                continue;
            }
            if (words[i].contains("#")) {
                words[i] = words[i].replace("#", " ");
            }
            if (words[i].contains("*")) {
                words[i] = words[i].replace("*", " ");
            }
            if(words[i].contains("$")){
                words[i] = words[i].replace("$", "");
            }
            list.add(words[i].trim());
        }

        if (StringUtils.isNotEmpty(exampleDisturb)) {
            list.add(exampleDisturb.replace("#", " ").replace("$", ""));
        }

        Collections.shuffle(list);
        return list;
    }

    public static void main(String[] args) {
        CommonMethod commonMethod = new CommonMethod();
        String str = "Hello! I'm   $Henry$. I come from France.  ";
        System.out.println(commonMethod.getEnglishList(str));
    }

    /**
     * 获取乱序的中文选项
     *
     * @param sentence         例句中文
     * @param translateDisturb 中文干扰项
     * @return
     */
    public List<String> getOrderChineseList(String sentence, String translateDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", " ");
                    if (sentence.substring(sentence.length() - 1).equals(".")) {
                        sentence = sentence.substring(0, sentence.length() - 1);
                    }
                } else {
                    sentence = sentence.replace(s, "*");
                }
            }
        }

        // 拆分并去除*
        String[] arr = sentence.split("\\*");
        List<String> list = new ArrayList<>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        for (String s : arr) {
            if (StringUtils.isNotEmpty(s)) {
                list.add(s);
            }
        }
        if (StringUtils.isNotEmpty(translateDisturb)) {
            list.add(translateDisturb);
        }
        Collections.shuffle(list);
        return list;
    }

    /**
     * 将例句中文翻译顺序分割，其中的标点单独占用一个下标
     *
     * @param sentence 需要处理的例句
     * @return
     */
    public List<String> getChineseList(String sentence) {
        for (String s : POINT) {
            if (sentence.contains(s)) {
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", "*" + "." + "*");
                    if (sentence.substring(sentence.length() - 1).equals(".")){
                        sentence = sentence.substring(0, sentence.length() - 1);
                        sentence = sentence + "*.*";
                    }
                } else {
                    sentence = sentence.replace(s, "*" + s + "*");
                }
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split("\\*");
        for (String s : arr) {
            if (s.contains("*")) {
                list.add(s.replace("*", "").replace("$", ""));
            } else if (StringUtils.isNotEmpty(s.trim())) {
                list.add(s.trim());
            }
        }
        return list;
    }

    /**
     * 将测试类型转换为汉字
     *
     * @param classify 类型 1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写
     * @return 测试类型中文
     */
    public String getTestType(Integer classify) {
        if (classify != null) {
            if (classify == 0) {
                return "单词图鉴";
            } else if (1 == classify) {
                return "慧记忆";
            } else if (2 == classify) {
                return "慧听写";
            } else if (3 == classify) {
                return "慧默写";
            } else if (4 == classify) {
                return "例句听力";
            } else if (5 == classify) {
                return "例句翻译";
            } else if (6 == classify) {
                return "例句默写";
            } else if (7 == classify) {
                return "课文测试";
            } else if (11 == classify) {
                return "音标测试";
            }

        }
        return null;
    }
}
