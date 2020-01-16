package com.aispeech.segment.generatequery;

import com.aispeech.segment.constant.PhraseTypeClassify;
import com.aispeech.segment.entity.Phrase;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Huihua Niu
 * on 2020/1/16 11:21
 */
@Component
public class QueryGenerate {
    public  String CombineQuery(List<Phrase> currentResult, List<Phrase> lastResult) {
        List<Phrase> LastStandardWord = cleanData(lastResult,0);
        currentResult = fixData(currentResult);
        if (currentResult.size()==0) return null;
        Phrase currentFirst =  currentResult.get(0);
        String combineQuery ;
        // 牡丹花的花期   开花时间呢   主语一般是问**呢或**的呢 谓语一般会带上询问语句 开花时间是什么时候
        if (currentFirst.getTypeSet().contains("r")||currentFirst.getTypeSet().contains("predicate")||currentResult.get(currentResult.size()-1).getTypeSet().contains("ask")){
            combineQuery = predicateStart(currentResult,LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        combineQuery = getCombineResult(currentResult,LastStandardWord);
        return combineQuery;
    }

    /**
     * 清洗第一轮对话的数据
     * @param phrases
     * @return
     */
    public static List<Phrase> cleanData(List<Phrase> phrases,int fromIndex){
        if (phrases==null || phrases.size()==0) return phrases;
        List<Phrase> newPhrase = new ArrayList<>();
        boolean subjectHas = false;
        //找到连词的位置
        for (int i=fromIndex;i<phrases.size();i++){
            Phrase phrase = phrases.get(i);
            if (!subjectHas){
                if (typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(i).getTypeSet())||phrases.get(i).getTypeSet().contains("r")){
                    phrase.getTypeSet().remove("predicate");
                    newPhrase.add(phrase);
                    subjectHas = true;
                }else {
                    newPhrase.add(phrase);
                    continue;
                }
            }else {
                if (i+1<phrases.size()){
                    if ((phrase.getTypeSet().contains("n") && phrases.get(i+1).getTypeSet().contains("m"))
                            ||(phrase.getTypeSet().contains("m") && phrases.get(i+1).getTypeSet().contains("a"))
                            ||((phrase.getTypeSet().contains("a")||phrase.getTypeSet().contains("predicate"))&&(typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(i+1).getTypeSet())||phrases.get(i+1).getTypeSet().contains("predicate")))){
                        phrase.setValue(phrase.getValue()+phrases.get(i+1).getValue());
                        Set<String> type = new HashSet<>();
                        type.add("predicate");
                        phrase.setTypeSet(type);
                        phrase.setFrequence(500);
                        i=i+1;
                    } else {
                      if (phrase.getTypeSet().contains("a")){
                          phrase.getTypeSet().remove("predicate");
                      }
                    }
                }
                newPhrase.add(phrase);
            }
        }
        return newPhrase;
    }

    /**
     *
     * @param phrases
     * @return
     */
    public static List<Phrase> fixData(List<Phrase> phrases){
        if (phrases==null || phrases.size()==0) return phrases;
        List<Phrase> newPhrase = new ArrayList<>();
        if (typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(0).getTypeSet())) return phrases;
        if (phrases.size()==2){
            if ((phrases.get(0).getTypeSet().contains("n") && phrases.get(1).getTypeSet().contains("m"))
                    ||(phrases.get(0).getTypeSet().contains("m") && phrases.get(1).getTypeSet().contains("a"))
                    ||((phrases.get(0).getTypeSet().contains("a")||phrases.get(0).getTypeSet().contains("predicate"))&&(typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(1).getTypeSet())||phrases.get(1).getTypeSet().contains("predicate")))){
                Phrase  phrase = new Phrase();
                phrase.setValue(phrases.get(0).getValue()+phrases.get(1).getValue());
                Set<String> type = new HashSet<>();
                type.add("predicate");
                phrase.setTypeSet(type);
                phrase.setFrequence(500);
                newPhrase.add(phrase);
                return newPhrase;
            }
        }else if (phrases.size()==1){
            if (phrases.get(0).getTypeSet().contains("a")){
                phrases.get(0).getTypeSet().remove("predicate");
                newPhrase.add( phrases.get(0));
                return newPhrase;
            }
        }
        return phrases;
    }


    public boolean  hasSameTypeSet(Set<String> set1, Set<String> set2) {

        if (set1 == null && set2 == null) {
            return true;
        }
        if (set1 == null || set2 == null ) {
            return false;
        }
        if (set1.size()==0 && set2.size() == 0) {
            return true;
        }
        List<String> intersection = set1.stream().filter(item -> set2.contains(item)).collect(Collectors.toList());

        if ( (intersection.size()==set1.size() && set1.size() == set2.size()) || intersection.size()>=2 ){
            return true;
        }
        intersection.remove("n");
        intersection.remove("a");
        if (intersection.size()>=1) return true;
        return false;
    }

    /**
     * 实体类型匹配
     * @param current
     * @param last
     * @return
     */
    public String getCombineResult(List<Phrase> current, List<Phrase> last) {
        List<Integer> index = new ArrayList<>();
        int currentIndex = -1;
        //找出上一轮词性和当前词性起始位置相同的地方
        for (int j = 0; j < current.size(); j++) {
            for (int i = 0; i < last.size(); i++) {
                if (hasSameTypeSet(current.get(j).getTypeSet(), last.get(i).getTypeSet())) {
                    index.add(i);
                }
            }
            if (index.size() > 0) {
                currentIndex = j;
                break;
            }
        }
        //如果从第二个开始就相同
        if (currentIndex == 1) {
            current.get(currentIndex).setValue(current.get(0).getValue() + current.get(1).getValue());
            current.remove(0);
        }
        if (index.size() == 0) return null;
        boolean isMatch = true;
        int hitIndex = -1;
        for (int i = 0; i < index.size(); i++) {
            hitIndex = index.get(i);
            if (hitIndex + current.size() > last.size()) {
                isMatch = false;
                break;
            }
            for (int j = hitIndex; j < hitIndex + current.size(); j++) {
                if (!hasSameTypeSet(current.get(j - hitIndex).getTypeSet(), last.get(j).getTypeSet())) {
                    isMatch = false;
                    break;
                } else {
                    isMatch = true;
                }
            }
            if (isMatch) break;
        }
        if (isMatch) {
            StringBuilder sb = new StringBuilder();
            if (last.size()>=2){
                Phrase lastPhrase = last.get(last.size()-1);
                Phrase penultimatePhrase = last.get(last.size()-2);
                if (lastPhrase.getTypeSet().contains("num") && penultimatePhrase.getTypeSet().contains("n")){
                    penultimatePhrase.setValue(lastPhrase.getValue()+penultimatePhrase.getValue());
                    penultimatePhrase.getTypeSet().add("a");
                    last.remove(lastPhrase);
                }else if (lastPhrase.getTypeSet().contains("n") && penultimatePhrase.getTypeSet().contains("num")){
                    penultimatePhrase.setValue(lastPhrase.getValue()+penultimatePhrase.getValue());
                    last.remove(lastPhrase);
                }
            }
            for (int i = 0; i < last.size(); i++) {
                if (i >= hitIndex && i < hitIndex + current.size() && i >= 0) {
                    sb.append(current.get(i - hitIndex).getValue());
                } else {
                    sb.append(last.get(i).getValue());
                }
            }
            if (sb.length() > 0) return sb.toString();
        }
        return null;
    }

    /**
     * 以谓语开头的,或者以代词开头的，这是确实主语的情况
     * @param current
     * @param last
     * @return
     */
    public String predicateStart(List<Phrase> current, List<Phrase> last){
        if (current==null || current.size()==0||last==null|| last.size()==0) return null;
        int subjectIndex = -1,predicateIndex= -1;
        for (int i=0;i<last.size();i++){
            if (typeIsIncluded(PhraseTypeClassify.entityType,last.get(i).getTypeSet())){
                subjectIndex = i;break;
            }
        }
        for (int i=0;i<current.size();i++){
            if (current.get(i).getTypeSet().contains("v")||current.get(i).getTypeSet().contains("vn")||current.get(i).getTypeSet().contains("a") || current.get(i).getTypeSet().contains("predicate")){
                predicateIndex = i;break;
            }
        }
        if (subjectIndex>=0 && predicateIndex>=0){
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<=subjectIndex;i++){
                sb.append(last.get(i).getValue());
            }
            if (subjectIndex<last.size() && last.get(subjectIndex+1).getTypeSet().contains("u")){
                sb.append("的");
            }
            boolean hasAsk = false;
            for (int i=predicateIndex;i<current.size();i++){
               sb.append(current.get(i).getValue());
               if (current.get(i).getTypeSet().contains("ask")) hasAsk = true;
            }
            if (current.get(current.size()-1).getTypeSet().contains("ask")) return sb.toString();
            if (!hasAsk && typeIsIncluded(PhraseTypeClassify.endEntityType,last.get(last.size()-1).getTypeSet())) sb.append("的"+last.get(last.size()-1).getValue()).toString();
            if (!hasAsk && last.get(last.size()-1).getTypeSet().contains("ask")) sb.append(last.get(last.size()-1).getValue()).toString();
            return sb.toString();
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
}
