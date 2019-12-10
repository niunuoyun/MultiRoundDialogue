package com.aispeech.segment.tools;

import com.aispeech.segment.entity.ConvertStandard;
import com.aispeech.segment.entity.Phrase;
import com.aispeech.segment.service.ConverStandardService;
import com.google.common.collect.Lists;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Huihua Niu
 * on 2019/12/6 12:16
 */
@Component
public class ConverStandardTool {
    @Autowired
    private ConverStandardService converStandardService;

    /**
     * 分词结果标准化
     * @param phrases
     * @return
     */
    public List<Phrase> getStandardWords(List<Phrase> phrases){
        // 查询转换标准
        if (phrases==null || phrases.size()==0) return phrases;
        List<Phrase> newResult = Lists.newArrayList(phrases);
        // 想着
        for (int i=phrases.size()-1;i>0;i--){
            List<ConvertStandard> convertStandards = converStandardService.findConvertStandard(phrases.get(i).getValue());
            if (convertStandards==null || convertStandards.size()==0) continue;
            for (int cs=0;cs<convertStandards.size();cs++){
                Set<String> preType = convertStandards.get(cs).getPreType();
                List<String> intersection = phrases.get(i-1).getTypeSet().stream().filter(item -> preType.contains(item)).collect(Collectors.toList());
                if (intersection.size()>0){
                    newResult.get(i).setValue(convertStandards.get(cs).getStandardWord());
                    newResult.get(i).setTypeSet(convertStandards.get(cs).getStandardWordType());
                    break;
                }
            }
        }
        return newResult;
    }

}
