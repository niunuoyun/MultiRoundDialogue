package com.aispeech.segment.controller;

import com.aispeech.segment.output.ResponseUtil;
import com.aispeech.segment.service.ConverStandardService;
import com.aispeech.segment.service.EntityRelationMappingService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author  Huihua Niu
 * on 2019/7/19 15:16
 */
@RestController
@RequestMapping(value = "convert/standard")
public class ConvertStandardController {
    @Autowired
    ConverStandardService converStandardService;
    @GetMapping(value = "query")
    public JSONObject query(){
        return ResponseUtil.okWithData(converStandardService.findAll());
    }

    @PostMapping(value = "insert")
    public JSONObject insert(@RequestBody JSONObject jsonObject){
        return ResponseUtil.okWithData(converStandardService.insert(jsonObject));
    }

    @PostMapping(value = "delete")
    public JSONObject delete(@RequestBody JSONObject jsonObject){
        String id = null;
        if (jsonObject.containsKey("id")){
            id = jsonObject.getString("id");
        }
        if (StringUtils.isEmpty(id)) return  ResponseUtil.error();
        return ResponseUtil.okWithData(converStandardService.delete(id));
    }
}
