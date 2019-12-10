package com.aispeech.segment.service;

import com.aispeech.segment.entity.ConvertStandard;
import com.aispeech.segment.entity.EntityRelationMapping;
import com.aispeech.segment.service.base.AbstractBaseService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Huihua Niu
 * on 2019/11/20 11:50
 */
@Service
public class ConverStandardService extends AbstractBaseService {
    public List<ConvertStandard> findAll() {
        return mongoTemplate.findAll(ConvertStandard.class);
    }

    @Override
    public Object insert(JSONObject jsonObject) {
        if (jsonObject != null) {
            ConvertStandard convertStandard = JSONObject.parseObject(jsonObject.toJSONString(), ConvertStandard.class);
            return mongoTemplate.insert(convertStandard);
        }
        return null;
    }

    @Override
    public DeleteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.remove(query,ConvertStandard.class);
    }
    public List<ConvertStandard> findConvertStandard(String nonStandardWord) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nonStandardWord").is(nonStandardWord));
        return mongoTemplate.find(query,ConvertStandard.class);
    }
}
