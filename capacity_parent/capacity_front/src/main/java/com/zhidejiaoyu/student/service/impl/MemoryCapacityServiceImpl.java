package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.common.mapper.MemoryCapacityMapper;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.MemoryCapacity;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.constant.PetMP3Constant;
import com.zhidejiaoyu.student.service.MemoryCapacityService;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
@Slf4j
@Service
public class MemoryCapacityServiceImpl extends BaseServiceImpl<MemoryCapacityMapper, MemoryCapacity> implements MemoryCapacityService {

    @Autowired
    private MemoryCapacityMapper memoryCapacityMapper;
    @Autowired
    private PetSayUtil petSayUtil;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private VocabularyMapper vocabularyMapper;
    @Autowired
    private BaiduSpeak baiduSpeak;

    @Override
    public ServerResponse<Object> getEnterMemoryCapacity(HttpSession session, Integer type) {
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), type);
        Map<String, Object> map = new HashMap<>();
        if (count == null || count.equals(0) || student.getRole().equals(4)) {
            map.put("isEnter", true);
        } else {
            map.put("isEnter", false);
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    @Transactional
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, Integer grade, Integer fraction) {
        //判断是否为当日第一次保存
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 1);
        Map<String, Object> map = new HashMap<>();
        Integer gold = 0;
        String url;
        if (fraction == null) {
            fraction = 0;
        }
        Integer enger = 0;
        if (count == null || count.equals(0) || student.getRole().equals(4)) {
            //当前为第一次测试 添加金币
            if (fraction >= 80) {
                enger = 2;
                //根据等级添加金币
                switch (grade) {
                    case 1:
                        gold = 1;
                        break;
                    case 2:
                        gold = 2;
                        break;
                    case 3:
                        gold = 3;
                        break;
                    case 4:
                        gold = 4;
                        break;
                    case 5:
                        gold = 5;
                        break;
                    default:
                        gold = 0;
                        break;
                }
            }
            try {
                Date date = new Date();
                MemoryCapacity memoryCapacity = new MemoryCapacity();
                memoryCapacity.setCreateTime(date);
                memoryCapacity.setGold(gold);
                memoryCapacity.setGrade(grade);
                memoryCapacity.setStudentId(student.getId());
                memoryCapacity.setType(1);
                memoryCapacityMapper.insert(memoryCapacity);

                super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在记忆容量《" + grade + "》中奖励#" + gold + "#枚金币");
                student.setSystemGold(student.getSystemGold() + gold);
                student.setEnergy(student.getEnergy() + enger);
                studentMapper.updateById(student);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getReturn(fraction, student, gold, map, enger);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> saveTrain(HttpSession session, Integer point) {
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 2);
        Map<String, Object> map = new HashMap<>();
        getGoldeAndEnteger(count,student,point,2,"眼脑训练",map);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 获取返回数据
     * @param count
     * @param student
     * @param point
     * @param type
     * @param model
     * @param map
     */
    private void getGoldeAndEnteger(Integer count,Student student,Integer point,Integer type,String model,Map<String,Object> map) {
        Integer gold=0;
        Integer enger=0;
        if (point == null) {
            point = 0;
        }
        if (count == null || count.equals(0) || student.getRole().equals(4)) {
            //当前为第一次测试 添加金币
            //根据等级添加金币
            if (point > 20 && point <= 36) {
                gold = 1;
                enger = 1;
            } else if (point > 36 && point <= 72) {
                gold = 2;
                enger = 1;
            } else if (point > 72 && point <= 85) {
                gold = 3;
                enger = 2;
            } else if (point > 85 && point <= 99) {
                gold = 4;
                enger = 2;
            } else if (point == 100) {
                gold = 6;
                enger = 3;
            }
            try {
                this.saveMemory(student, gold, enger, type, model);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getReturn(point, student, gold, map, enger);
    }

    @Override
    public ServerResponse<Object> saveBrain(HttpSession session, Integer point) {
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 2);
        Map<String, Object> map = new HashMap<>();
        getGoldeAndEnteger(count,student,point,3,"最强大脑",map);
        return ServerResponse.createBySuccess(map);
    }

    private void saveMemory(Student student, Integer gold, Integer energy, Integer type, String model) {
        Date date = new Date();
        MemoryCapacity memoryCapacity = new MemoryCapacity();
        memoryCapacity.setCreateTime(date);
        memoryCapacity.setGold(gold);
        memoryCapacity.setStudentId(student.getId());
        memoryCapacity.setType(type);
        memoryCapacityMapper.insert(memoryCapacity);

        student.setSystemGold(student.getSystemGold() + gold);
        student.setEnergy(student.getEnergy() + energy);
        studentMapper.updateById(student);

        super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在" + model + "中奖励#" + gold + "#枚金币");
    }

    @Override
    public ServerResponse<Object> savePinkeye(HttpSession session, Integer point) {
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 3);
        Map<String, Object> map = new HashMap<>();
        Integer gold = 0;
        if (point == null) {
            point = 0;
        }
        Integer enger = 0;
        if (count == null || count.equals(0) || student.getRole().equals(4)) {
            try {
                this.saveMemory(student, gold, enger, 2, "火眼金睛");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getReturn(point, student, gold, map, enger);
        return ServerResponse.createBySuccess(map);
    }


    @Override
    public ServerResponse<Object> getTrainTest(HttpSession session) {
        Student student = getStudent(session);
        //获取熟词中的单词
        List<String> strings = vocabularyMapper.selByStudentIdLimitTen(student.getId(), 1);
        if (strings.size() < 3) {
            //如果不够三个单词查询全部单词
            strings = vocabularyMapper.selByStudentIdLimitTen(student.getId(), 2);
        }
        List<String> returnList = new ArrayList<>();
        //获取展示数据
        for (int i = 0; i < 10; i++) {
            Integer number = (int) (Math.random() * strings.size());
            returnList.add(strings.get(number));
        }
        //获取正确答案
        String answer = returnList.get(2);
        //获取答案出现次数
        int frequency = 0;
        for (String string : returnList) {
            if (string.equals(answer)) {
                frequency++;
            }
        }
        Map<String, Object> answerMap = new HashMap<>();
        answerMap.put("word", answer);
        answerMap.put("readUrl", baiduSpeak.getLanguagePath(answer));
        //获取选项集合
        List<Integer> number = new ArrayList<>();
        number.add(frequency);
        //获取选项错误答案
        wrongAnswer(number);
        //将选项放入集合中
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer integer : number) {
            Map<String, Object> map = new HashMap<>();
            map.put("answer", integer);
            if (integer.equals(frequency)) {
                map.put("falg", true);
            } else {
                map.put("falg", false);
            }
            list.add(map);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", returnList);
        map.put("answer", answerMap);
        Collections.shuffle(list);
        map.put("options", list);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> getPinkeye() {
        //获取第一个单词
        List<String> wordString = vocabularyMapper.selRandWord(1, 0, 1);
        //返回的数据list集合
        List<Map<String, Object>> arrayList = new ArrayList<>();
        for (String wordOne : wordString) {
            //第二个单词
            String wordTwo = null;
            //随机数判断  小于5一单词 赋值 二单词 大于5重新查找
            Random random = new Random();
            Integer rand = random.nextInt(10) + 1;
            if (rand <= 5) {
                wordTwo = wordOne;
            } else {
                wordTwo = vocabularyMapper.selRandWord(2, wordOne.length(), 2).get(0);
            }
            //返回集合
            Map<String, Object> map = new HashMap<>();
            //判断是否相同
            if (wordOne.equals(wordTwo)) {
                map.put("isTrue", "0");
            } else {
                map.put("isTrue", "1");
            }
            map.put("isTrue", "1");
            map.put("wordOne", wordOne);
            map.put("wordTwo", wordTwo);
            arrayList.add(map);
        }
        return ServerResponse.createBySuccess(arrayList);
    }


    private void wrongAnswer(List<Integer> numbers) {
        while (numbers.size() < 4) {
            getNumber(numbers);
        }
    }

    private void getNumber(List<Integer> numbers) {
        Integer number = (int) (Math.random() * 10) + 1;
        boolean isFalg = true;
        for (Integer option : numbers) {
            if (option.equals(number)) {
                isFalg = false;
            }
        }
        if (!isFalg) {
            getNumber(numbers);
        } else {
            numbers.add(number);
        }
    }

    private void getReturn(Integer point, Student student, Integer gold, Map<String, Object> map, Integer enger) {
        String url;
        if (point <= 40) {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY);
        } else if (point <= 80) {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED);
        } else {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED);
        }
        map.put("energy", enger);
        map.put("gold", gold);
        map.put("listen", url);
        map.put("petUrl", AliyunInfoConst.host + student.getPartUrl());
    }
}
