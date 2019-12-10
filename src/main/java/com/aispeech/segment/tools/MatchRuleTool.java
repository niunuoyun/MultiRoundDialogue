package com.aispeech.segment.tools;

import com.aispeech.segment.entity.EntityRelationMapping;
import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.service.EntityRelationMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Huihua Niu
 * on 2019/12/6 17:31
 */
@Component
public class MatchRuleTool {
    @Autowired
    private EntityRelationMappingService relationMappingService;

    /**
     * 匹配出符合第一个词的所有内容，也就是主语和谓语类型都含有该词性的规则，在根据第二个词的词性，过滤出来predicateType和resultType符合条件的
     *
     * @param originalData
     * @return
     */
    public List<List<EntityRelationMapping>> getMatchRules(List<Phrase> originalData) {
        List<List<EntityRelationMapping>> matchResult = new LinkedList<>();
        if (originalData==null || originalData.size()==0) return matchResult;
        if (originalData.size()==1){
            List<EntityRelationMapping> relationMappings = relationMappingService.findByType(originalData.get(0).getTypeSet());
            if (relationMappings.size()>0) matchResult.add(relationMappings);
            return matchResult;

        }
        for(int i=0;i<originalData.size()-1;i++){
            List<EntityRelationMapping> relationMappings = relationMappingService.findByType(originalData.get(i).getTypeSet());
            if (relationMappings != null && relationMappings.size() > 0) {
                Set<String> secondWordTypes = originalData.get(i+1).getTypeSet();
                List<EntityRelationMapping> mappingList  = relationMappings.stream().filter(val->secondWordTypes.contains(val.getPredicateType())||secondWordTypes.contains(val.getResultType())).collect(Collectors.toList());
                if (mappingList.size()>0) {
                    matchResult.add( mappingList);
                    i = i + 1;
                    continue;
                }else {
                    if (relationMappings.size()>0) matchResult.add(relationMappings);
                }
            }
        }
        if (matchResult.size()==0){
            List<EntityRelationMapping> relationMappings = relationMappingService.findByType(originalData.get(originalData.size()-1).getTypeSet());
            if (relationMappings.size()>0) matchResult.add(relationMappings);
        }
        if (matchResult.size()<=1) return matchResult;
        for (int i = 0; i < matchResult.size(); i++) {
            List<EntityRelationMapping> mappingNext = matchResult.get(i + 1);
            List<EntityRelationMapping> relationMappings = new ArrayList<>();
            matchResult.get(i).stream().forEach(val -> mappingNext.stream().forEach(data -> {
                        if (val.getResultType()!=null && data.getSubjectType().contains(val.getResultType())) {
                            relationMappings.add(val);
                        }
                    }
            ));
            matchResult.set(i,relationMappings);
            i=i+1;
        }
        return matchResult;
    }
  /*  *//**
     * 匹配规则的查询，根据每个词去查询，如果查询到了存储对应的词和对应的类型
     * @param originalData
     *//*
    public Map<Phrase,List<EntityRelationMapping>> getMatchRules(List<Phrase> originalData) {
        Map<Phrase,List<EntityRelationMapping>> matchResult = new LinkedHashMap<>();
        if (originalData==null || originalData.size()==0) return matchResult;
        if (originalData.size()==1){
            List<EntityRelationMapping> relationMappings = relationMappingService.findByType(originalData.get(0).getTypeSet());
            matchResult.put(originalData.get(0),relationMappings);
            return matchResult;

        }
        for(int i=0;i<originalData.size()-1;i++){
            if (i==0){
                List<EntityRelationMapping> relationMappings = relationMappingService.findByType(originalData.get(i).getTypeSet());
                if (relationMappings != null && relationMappings.size() > 0) {
                    // 第一个词以主语类型为主，其次是谓语
                    Set<String> firstWordTypes = originalData.get(i).getTypeSet();
                    List<EntityRelationMapping> subjectMapping =  relationMappings.stream().filter(val->TypeHandlerTool.hasSameTypeSet(firstWordTypes,val.getSubjectType())).collect(Collectors.toList());
                  // 当主语查询到的时候，根据下一个词确定词语匹配关系
                    if (subjectMapping.size()>0){
                        Set<String> secondWordTypes = originalData.get(i+1).getTypeSet();
                        List<EntityRelationMapping> mappingList  = subjectMapping.stream().filter(val->TypeHandlerTool.hasSameTypeSet(secondWordTypes,val.getPredicateType())).collect(Collectors.toList());
                       if (mappingList.size()>0) {
                           matchResult.put(originalData.get(i), mappingList);
                           i = i + 1;
                           continue;
                       }
                    }else {
                        // 当匹配上谓语的时候，将匹配的谓语和下一个词所需要的主语匹配
                        List<EntityRelationMapping> predicateMapping =  relationMappings.stream().filter(val->TypeHandlerTool.hasSameTypeSet(firstWordTypes,val.getPredicateType())).collect(Collectors.toList());
                        if (predicateMapping.size()>0){
                            Set<String> secondWordTypes = originalData.get(i+1).getTypeSet();
                            List<EntityRelationMapping> secondWordRelation = relationMappingService.findByPredicateType(secondWordTypes);
                            //todo query的匹配规则选定
                        }
                    }
                    matchResult.put(originalData.get(i),relationMappings);
                }
            }else {

            }
        }
        return matchResult;
    }*/
}
