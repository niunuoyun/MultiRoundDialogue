package com.aispeech.segment.controller;

import com.aispeech.segment.output.ResponseUtil;
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
@RequestMapping(value = "word")
public class EntityRelationShipMappingController {
    @Autowired
    EntityRelationMappingService relationMappingService;
    @GetMapping(value = "query/type/mapping")
    public JSONObject query(){
        return ResponseUtil.okWithData(relationMappingService.findAll());
    }
    @PostMapping(value = "insert/type/mapping")
    public JSONObject insert(@RequestBody JSONObject jsonObject){
        return ResponseUtil.okWithData(relationMappingService.insert(jsonObject));
    }

    @PostMapping(value = "delete/type/mapping")
    public JSONObject delete(@RequestBody JSONObject jsonObject){
        String id = null;
        if (jsonObject.containsKey("id")){
            id = jsonObject.getString("id");
        }
        if (StringUtils.isEmpty(id)) return  ResponseUtil.error();
        return ResponseUtil.okWithData(relationMappingService.delete(id));
    }
}
