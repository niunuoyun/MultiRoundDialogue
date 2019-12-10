package com.aispeech.segment.service;

import com.aispeech.segment.entity.ConvertStandard;
import com.aispeech.segment.entity.PhraseClassify;
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
public class PhraseClassifyService extends AbstractBaseService {
    public List<PhraseClassify> findAll() {
        return mongoTemplate.findAll(PhraseClassify.class);
    }

    @Override
    public Object insert(JSONObject jsonObject) {
        if (jsonObject != null) {
            PhraseClassify phraseClassify = JSONObject.parseObject(jsonObject.toJSONString(), PhraseClassify.class);
            Query query = new Query();
            query.addCriteria(Criteria.where("phraseType").is(phraseClassify.getPhraseType()));
            PhraseClassify historyPhraseClissify = mongoTemplate.findOne(query,PhraseClassify.class);
            if (historyPhraseClissify!=null){
                historyPhraseClissify.setPhraseType(phraseClassify.getPhraseType());
                historyPhraseClissify.setDescription(phraseClassify.getDescription());
                historyPhraseClissify.setLabel(phraseClassify.getLabel());
                historyPhraseClissify.setTripleElement(phraseClassify.getTripleElement());
                historyPhraseClissify.setKeyType(phraseClassify.isKeyType());
                return mongoTemplate.insert(historyPhraseClissify);
            }else {
                return mongoTemplate.insert(phraseClassify);
            }
        }
        return null;
    }

    @Override
    public DeleteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.remove(query,PhraseClassify.class);
    }
    public List<PhraseClassify> findConvertStandard(String nonStandardWord) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nonStandardWord").is(nonStandardWord));
        return mongoTemplate.find(query,PhraseClassify.class);
    }
}
