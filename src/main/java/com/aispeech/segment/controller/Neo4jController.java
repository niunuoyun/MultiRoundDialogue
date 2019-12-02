package com.aispeech.segment.controller;

import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.neo4j.neo4jservicce.Neo4jService;
import com.aispeech.segment.segment.seg.Tokenizer;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/10/31 10:57
 */
@RestController
@RequestMapping(value = "answer")
public class Neo4jController {
    @Autowired
    private Neo4jService neo4jService;
    @Autowired
    Tokenizer tokenizer;
    @RequestMapping(value = "/search")
    public JSONObject searchNode(@RequestBody JSONObject query)throws Exception{
        String queryContent = query.get("query").toString();
        List<Phrase> p = tokenizer.segment(queryContent,true);
        List<Phrase> handlerResult = new LinkedList<>();
        for (Phrase phrase:p){
            if (phrase.getTypeSet().contains("n")||phrase.getTypeSet().contains("vn")||phrase.getTypeSet().contains("cn")||phrase.getTypeSet().contains("nz")){
                handlerResult.add(phrase);
            }
        }
        JSONObject jsonObject = neo4jService.searchNode(handlerResult);
        return jsonObject;
    }
}