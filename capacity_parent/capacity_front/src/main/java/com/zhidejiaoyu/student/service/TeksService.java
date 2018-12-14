package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import java.util.List;

public interface TeksService {

    ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId);


    ServerResponse<Object> selChooseTeks(Integer unitId);
}
