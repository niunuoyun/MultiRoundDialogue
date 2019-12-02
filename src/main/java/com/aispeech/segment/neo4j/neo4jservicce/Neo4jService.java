package com.aispeech.segment.neo4j.neo4jservicce;

import com.aispeech.segment.entity.Phrase;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/10/30 20:02
 */
public interface Neo4jService {
    public JSONObject searchNode(List<Phrase> phrases) throws  Exception;
}
