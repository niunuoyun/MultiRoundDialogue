package com.aispeech.segment.service;

import com.aispeech.segment.entity.EntityRelationMapping;
import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.entity.Word;
import com.aispeech.segment.service.base.AbstractBaseService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/11/20 11:50
 */
@Service
public class PhraseService extends AbstractBaseService {
    public List<Phrase> findAll() {
        return mongoTemplate.findAll(Phrase.class);
    }

    @Override
    public Object insert(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.containsKey("value") && jsonObject.containsKey("typeSet")) {
            Phrase phrase = JSONObject.parseObject(jsonObject.toJSONString(), Phrase.class);
            return mongoTemplate.insert(phrase);
        }
        return null;
    }

    @Override
    public DeleteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.remove(query,Phrase.class);
    }
}
