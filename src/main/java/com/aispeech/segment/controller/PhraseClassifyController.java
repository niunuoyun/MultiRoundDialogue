package com.aispeech.segment.controller;

import com.aispeech.segment.output.ResponseUtil;
import com.aispeech.segment.service.PhraseClassifyService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.aispeech.segment.enumerate.ResponseStatus.FILEDS_MISSING_OR_FORMAT_WRONG;

/**
 * @author  Huihua Niu
 * on 2019/7/19 15:16
 */
@RestController
@RequestMapping(value = "phrase/type")
public class PhraseClassifyController {
    @Autowired
    PhraseClassifyService phraseClassifyService;
    @GetMapping(value = "query")
    public JSONObject query(){
        return ResponseUtil.okWithData(phraseClassifyService.findAll());
    }

    @PostMapping(value = "insert")
    public JSONObject insert(@RequestBody JSONObject jsonObject){
        if (StringUtils.isEmpty(jsonObject.getString("phraseType")) ||
                StringUtils.isEmpty(jsonObject.getString("label"))) return ResponseUtil.status(FILEDS_MISSING_OR_FORMAT_WRONG);
        return ResponseUtil.okWithData(phraseClassifyService.insert(jsonObject));
    }

    @PostMapping(value = "delete")
    public JSONObject delete(@RequestBody JSONObject jsonObject){
        String id = null;
        if (jsonObject.containsKey("id")){
            id = jsonObject.getString("id");
        }
        if (StringUtils.isEmpty(id)) return  ResponseUtil.error();
        return ResponseUtil.okWithData(phraseClassifyService.delete(id));
    }
}
