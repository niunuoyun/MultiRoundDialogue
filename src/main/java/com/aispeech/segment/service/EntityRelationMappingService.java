package com.aispeech.segment.service;

import com.aispeech.segment.entity.EntityRelationMapping;
import com.aispeech.segment.service.base.AbstractBaseService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.DeleteResult;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public class EntityRelationMappingService extends AbstractBaseService {
    public List<EntityRelationMapping> findAll() {
        return mongoTemplate.findAll(EntityRelationMapping.class);
    }

    @Override
    public Object insert(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.containsKey("subjectType") && jsonObject.containsKey("predicateType")) {
            EntityRelationMapping entityRelationMapping = JSONObject.parseObject(jsonObject.toJSONString(), EntityRelationMapping.class);
            return mongoTemplate.insert(entityRelationMapping);
        }
        return null;
    }

    @Override
    public DeleteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.remove(query,EntityRelationMapping.class);
    }
    public List<EntityRelationMapping> findByPredicateType(Set<String> typeSet) {
        Query query = new Query();
        query.addCriteria(Criteria.where("subjectType").nin(typeSet).orOperator(Criteria.where("predicateType").nin(typeSet)));
        return mongoTemplate.find(query,EntityRelationMapping.class);
    }
}
