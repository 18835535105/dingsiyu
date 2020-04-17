package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.EegRecordingMapper;
import com.zhidejiaoyu.common.mapper.MemoryCapacityMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.EegRecording;
import com.zhidejiaoyu.common.pojo.MemoryCapacity;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.constant.PetMP3Constant;
import com.zhidejiaoyu.student.business.service.MemoryCapacityService;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MemoryCapacityServiceImpl
        extends BaseServiceImpl<MemoryCapacityMapper, MemoryCapacity> implements MemoryCapacityService {

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
    @Autowired
    private EegRecordingMapper eegRecordingMapper;

    @Override
    public ServerResponse<Object> getEnterMemoryCapacity(HttpSession session, Integer type) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap();
        if (student.getRole().equals(4)) {
            EegRecording eegRecording = null;
            if (type != null) {
                eegRecording = eegRecordingMapper.selRoleStudent(type, student.getId());
            } else {
                List<Boolean> list = new ArrayList<>();
                Map<Integer, Map<String, Object>> integerMapMap = eegRecordingMapper.selRoleStudyByStudent(student.getId());
                for (int i = 1; i <= 4; i++) {
                    Map<String, Object> stringObjectMap = integerMapMap.get(i);
                    if (stringObjectMap != null) {
                        list.add(true);
                    } else {
                        list.add(false);
                    }
                }
                map.put("studyList", list);
            }
            map.put("petUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
            map.put("type", 0);
            map.put("role", true);
            if (eegRecording == null) {
                map.put("isStudy", true);
                map.put("level", 1);
            } else {
                map.put("isStudy", true);
                setReturnMap(eegRecording, map);
            }
        } else {
            EegRecording eegRecording = eegRecordingMapper.selNowByStudent(student.getId());
            map.put("role", false);
            if (eegRecording == null) {
                map.put("isStudy", true);
                map.put("type", 0);
                map.put("level", 1);
            } else {
                if (eegRecording.getState().equals(1)) {
                    map.put("isStudy", false);
                } else {
                    map.put("isStudy", true);
                    setReturnMap(eegRecording, map);
                }
                map.put("type", eegRecording.getType());
            }
            map.put("petUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        }
        return ServerResponse.createBySuccess(map);
    }

    private void setReturnMap(EegRecording eegRecording, Map<String, Object> map) {
        map.put("answerNumber", eegRecording.getAnswerNumber());
        map.put("pairNumber", eegRecording.getPairNumber());
        map.put("bigLevel", eegRecording.getBigLevel());
        map.put("smallLevel", eegRecording.getSmallLevel());
        map.put("level", eegRecording.getLevel());
        map.put("frequency", eegRecording.getFrequency());
    }

    /*@Override
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

                student.setSystemGold(student.getSystemGold() + gold);
                student.setEnergy(student.getEnergy() + enger);
                studentMapper.updateById(student);

                if (gold > 0) {
                    super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在记忆容量《" + grade + "》中奖励#" + gold + "#枚金币");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getReturn(fraction, student, gold, map, enger);
        return ServerResponse.createBySuccess(map);
    }*/

    @Override
    public ServerResponse<Object> saveTrain(HttpSession session, Integer point) {
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 2);
        Map<String, Object> map = new HashMap<>();
        getGoldeAndEnteger(count, student, point, 2, "眼脑训练", map);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 获取返回数据
     *
     * @param count
     * @param student
     * @param point
     * @param type
     * @param model
     * @param map
     */
    private void getGoldeAndEnteger(Integer count, Student student, Integer point, Integer type, String model, Map<String, Object> map) {
        int gold = 0;
        int enger = 0;
        if (point == null) {
            point = 0;
        }
        if (count == null || count.equals(0) || student.getRole().equals(4)) {
            //当前为第一次测试 添加金币
            //根据分数添加金币
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
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 4);
        Map<String, Object> map = new HashMap<>();
        getGoldeAndEnteger(count, student, point, 4, "最强大脑", map);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, EegRecording eegRecording) {
        Student student = getStudent(session);
        EegRecording eegRecordings = null;
        if (student.getRole().equals(4)) {
            eegRecordings = eegRecordingMapper.selRoleStudent(eegRecording.getType(), student.getId());
        } else {
            eegRecordings = eegRecordingMapper.selNowByStudent(student.getId());
        }
        if (eegRecording.getType() != 1 && eegRecording.getAnswerNumber() < 0) {
            return ServerResponse.createBySuccess();
        }
        Integer gold = 0;
        if (eegRecordings != null) {
            if (eegRecording.getType().equals(eegRecordings.getType())) {
                //判断是否拥有eegRecording
                if (eegRecordings.getType().equals(eegRecording.getType())) {
                    eegRecording.setStudentId(student.getId().intValue());
                    eegRecording.setId(eegRecordings.getId());
                    eegRecording.setCreateTime(eegRecordings.getCreateTime());
                    //如果当前等级大于最大等级给与金币
                    if (eegRecording.getBigLevel() > (eegRecordings.getLevel())) {
                        Integer laterLevel = eegRecordings.getLevel();
                        Integer noeLevel = eegRecording.getBigLevel();
                        eegRecording.setLevel(eegRecording.getBigLevel());
                        gold = calculationGold(eegRecording.getType(), eegRecording.getLevel(), laterLevel, noeLevel);
                    } else {
                        eegRecording.setLevel(eegRecordings.getLevel());
                    }
                    eegRecordingMapper.updateById(eegRecording);
                }
            } else {
                return ServerResponse.createBySuccess();
            }
        } else {
            gold = calculationGold(eegRecording.getType(), eegRecording.getLevel(), null, eegRecording.getBigLevel());
            if (eegRecording.getType() != 1) {
                eegRecording.setLevel(eegRecording.getBigLevel());
            }
            eegRecording.setCreateTime(new Date());
            eegRecording.setStudentId(student.getId().intValue());
            eegRecordingMapper.insert(eegRecording);

        }
        //保存金币
        if (gold > 0) {
            student.setSystemGold(student.getSystemGold() + gold);
            studentMapper.updateById(student);
            String model = null;
            if (eegRecording.getType() == 1) {
                model = "记忆大挑战";
            }
            if (eegRecording.getType() == 2) {
                model = "乾坤挪移";
            }
            if (eegRecording.getType() == 3) {
                model = "火眼金睛";
            }
            if (eegRecording.getType() == 4) {
                model = "最强大脑";
            }
            GoldLogUtil.saveStudyGoldLog(student.getId(), model, gold);
        }
        Map<String, Object> map = new HashMap<>();
        String url = null;
        if (eegRecording.getType() != 1) {
            if (eegRecording.getBigLevel().equals(1)) {
                url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY);
            } else if (eegRecording.getBigLevel().equals(2)) {
                url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED);
            } else {
                url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED);
            }
        } else {
            url = petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED);
        }
        map.put("gold", gold);
        map.put("listen", url);
        map.put("petUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        return ServerResponse.createBySuccess(map);


    }

    /**
     * 福利账号重新玩脑电波
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getReStartMemoryCapacity(HttpSession session, Integer type) {
        Student student = getStudent(session);
        if (student.getRole().equals(4)) {
            eegRecordingMapper.delByStudentId(student.getId(), type);
        }
        return ServerResponse.createBySuccess();
    }

    private Integer calculationGold(Integer type, Integer level, Integer laterLevel, Integer nowLevel) {
        Integer gold = 0;
        if (laterLevel != null) {
            if (nowLevel.equals(2)) {
                if (nowLevel > laterLevel) {
                    gold = 2;
                }
            }
            if (nowLevel.equals(3)) {
                if (nowLevel > laterLevel) {
                    Integer difference = nowLevel - laterLevel;
                    if (difference.equals(2)) {
                        gold = 7;
                    } else if (difference.equals(1)) {
                        gold = 5;
                    }
                }
            }
        } else {
            if (type.equals(1) && level != null && level.equals(1)) {
                gold = 5;
            }
            if (!type.equals(1)) {
                if (nowLevel.equals(2)) {
                    gold = 2;
                }
                if (nowLevel.equals(3)) {
                    gold = 7;
                }
            }
        }
        return gold;
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
        if (gold > 0) {
            GoldLogUtil.saveStudyGoldLog(student.getId(), model, gold);
        }
    }

    @Override
    public ServerResponse<Object> savePinkeye(HttpSession session, Integer point) {
        Student student = getStudent(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(student.getId(), 3);
        Map<String, Object> map = new HashMap<>();
        getGoldeAndEnteger(count, student, point, 3, "火眼金睛", map);
        return ServerResponse.createBySuccess(map);
    }


    @Override
    public ServerResponse<Object> getTrainTest(HttpSession session) {
        Student student = getStudent(session);
        Integer count = vocabularyMapper.selCountByStudentIdLimitTen(student.getId(), 1);
        Integer start = 0;
        Random random = new Random();
        //获取数据查询位置
        if (count > 10) {
            start = random.nextInt(count - 10);
        }
        //获取熟词中的单词
        List<String> strings = vocabularyMapper.selByStudentIdLimitTen(student.getId(), 1, start);
        if (strings.size() < 3) {
            //如果不够三个单词查询全部单词
            count = vocabularyMapper.selCountByStudentIdLimitTen(student.getId(), 2);
            //获取数据查询位置
            if (count > 10) {
                start = random.nextInt(count - 10);
            }
            strings = vocabularyMapper.selByStudentIdLimitTen(student.getId(), 2, start);
        }
        List<String> returnList = new ArrayList<>();
        //获取展示数据
        for (int i = 0; i < 10; i++) {
            Integer number = random.nextInt(strings.size());
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
        //打乱数据顺序
        Collections.shuffle(returnList);
        map.put("list", returnList);
        map.put("answer", answerMap);
        Collections.shuffle(list);
        map.put("options", list);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> getPinkeye() {
        //获取第一个数组出现位置
        Integer integer = vocabularyMapper.selCountRandWord(1, 0);
        Random random = new Random();
        Integer count = random.nextInt(integer - 15);
        //获取第一个单词数组
        List<String> wordString = vocabularyMapper.selRandWord(1, 0, 1, count);
        //返回的数据list集合
        List<Map<String, Object>> arrayList = new ArrayList<>();
        for (String wordOne : wordString) {
            //第二个单词
            String wordTwo = null;
            //获取第二个单词出现位置
            integer = vocabularyMapper.selCountRandWord(2, wordOne.length());
            count = random.nextInt(integer);
            //随机数判断  小于5一单词 赋值 二单词 大于5重新查找
            Integer rand = random.nextInt(10) + 1;
            if (rand <= 5) {
                wordTwo = wordOne;
            } else {
                wordTwo = vocabularyMapper.selRandWord(2, wordOne.length(), 2, count).get(0);
            }
            //返回集合
            Map<String, Object> map = new HashMap<>();
            //判断是否相同
            if (wordOne.equals(wordTwo)) {
                map.put("isTrue", "0");
            } else {
                map.put("isTrue", "1");
            }
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
        map.put("petUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
    }
}
