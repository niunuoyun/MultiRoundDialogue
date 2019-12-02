package com.aispeech.segment.controller;

import com.aispeech.segment.output.ResponseUtil;
import com.aispeech.segment.service.EntityRelationMappingService;
import com.aispeech.segment.service.PhraseService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Huihua Niu
 * on 2019/7/19 15:16
 */
@RestController
@RequestMapping(value = "phrase")
public class PhraseController {
    @Autowired
    PhraseService phraseService;
    @GetMapping(value = "query")
    public JSONObject query(){
        return ResponseUtil.okWithData(phraseService.findAll());
    }
    @PostMapping(value = "insert")
    public JSONObject insert(@RequestBody JSONObject jsonObject){
        return ResponseUtil.okWithData(phraseService.insert(jsonObject));
    }

    @PostMapping(value = "delete")
    public JSONObject delete(@RequestBody JSONObject jsonObject){
        String id = null;
        if (jsonObject.containsKey("id")){
            id = jsonObject.getString("id");
        }
        if (StringUtils.isEmpty(id)) return  ResponseUtil.error();
        return ResponseUtil.okWithData(phraseService.delete(id));
    }
}
