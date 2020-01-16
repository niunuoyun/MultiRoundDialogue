package com.aispeech.segment.tools;

import com.aispeech.segment.constant.PhraseTypeClassify;
import com.aispeech.segment.entity.Phrase;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Huihua Niu
 * on 2020/1/7 15:42
 */
public class PhraseHandle {
    /**
     * 如果第一个是词是实体词，
     * 1：“的”后面名词+名词||动词+名词
     * 2：名词+序数词可作为谓语词
     * 3：序数词+形容词可作为谓语词
     *
     * @param listResult
     * @return
     */
    public static List<Phrase> rinseData(List<Phrase> listResult){
        boolean firstEntity = false;
        List<Phrase> newPhrase = new ArrayList<>();
        for (int i=0;i<listResult.size();i++){
            Phrase phrase = listResult.get(i);
            //先判断是不是实体词
            if (typeIsIncluded(PhraseTypeClassify.entityType,phrase.getTypeSet()) && !firstEntity){
                firstEntity = true;
                phrase.getTypeSet().remove("predicate");
                newPhrase.add(phrase);
            }else if (typeIsIncluded(PhraseTypeClassify.additionalType,phrase.getTypeSet())){
                newPhrase.add(phrase);
            }else{

            }
        }
        return null;
    }

    private static boolean typeIsIncluded(String[] basicType,Collection<String> wordType){
        if(basicType==null || wordType==null) return false;
        List<String> basicTypes = Arrays.asList(basicType);
        List<String> commonDataSet = wordType.stream().filter(val->basicTypes.contains(val)).collect(Collectors.toList());
        if (commonDataSet.size()>0) return true;
        return false;
    }

    public static void main(String[] args) {
        Set<String> sets = new HashSet<>();
        sets.add("a");
        sets.add("b");
       // sets.add("adv");
        System.out.println(typeIsIncluded(PhraseTypeClassify.additionalType,sets));
    }
}
