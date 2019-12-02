package com.aispeech.segment.service.base;

import com.aispeech.segment.entity.EntityRelationMapping;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Created by Huihua Niu
 * on 2019/11/20 12:17
 */
@Service
public abstract class AbstractBaseService {
    @Autowired
    public MongoTemplate mongoTemplate;
    public abstract Object insert(JSONObject jsonObject);

    public abstract DeleteResult delete(String id);
}
