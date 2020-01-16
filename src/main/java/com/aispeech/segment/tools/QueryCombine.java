package com.aispeech.segment.tools;

import com.aispeech.segment.constant.PhraseTypeClassify;
import com.aispeech.segment.entity.EntityRelationMapping;
import com.aispeech.segment.entity.Phrase;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Huihua Niu
 * on 2019/9/3 19:52
 */
@Component
public class QueryCombine {

    public  void listDirectory(File dir, List<String> fileList)throws IOException {
        if(!dir.exists())
            throw new IllegalArgumentException("目录："+dir+"不存在.");
        if(!dir.isDirectory()){
            throw new IllegalArgumentException(dir+"不是目录。");
        }
        //如果要遍历子目录下的内容就需要构造File对象做递归操作，File提供了直接返回File对象的API
        File[] files=dir.listFiles();
        if(files!=null&&files.length>0){
            for(File file:files){
                if(file.isDirectory())
                    //递归
                    listDirectory(file,fileList);
                else
                    fileList.add(file.getPath());
            }
        }
    }
    public  String questionRelatingToAbove(List<Phrase> currentResult,  List<Phrase> lastResult) {
        //词性匹配查询
        // List<Phrase> LastStandardWord = converStandardTool.getStandardWords(lastResult);
        List<Phrase> LastStandardWord = cleanData(lastResult);
        currentResult = fixData(currentResult);
        if (currentResult.size()==0) return null;
        Phrase currentFirst =  currentResult.get(0);
        //对上一轮数据清理噪音
        String combineQuery ;
        if (currentFirst.getTypeSet().contains("predicate")||(currentFirst.getTypeSet().contains("ask")&& currentResult.size()<=2)){
            combineQuery = predicateStart(currentResult,LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        if (currentFirst.getTypeSet().contains("r")){
            combineQuery =  pronounsStart(currentResult,LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        if (currentFirst.getTypeSet().contains("v")||currentFirst.getTypeSet().contains("vn") ||(currentFirst.getTypeSet().contains("ask")&& currentResult.size()<=2)){
            combineQuery = verbStart(currentResult,LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        combineQuery = getCombineResult(currentResult,LastStandardWord);
        return combineQuery;
    }


    public static List<Phrase> cleanData(List<Phrase> phrases){
        if (phrases==null || phrases.size()==0) return phrases;
        List<Phrase> newPhrase = new ArrayList<>();
        int predicateIndex = -1;
        for (int i=0;i<phrases.size();i++){
            if (phrases.get(i).getTypeSet().contains("predicate")){
                if (predicateIndex>=0){
                    newPhrase.get(predicateIndex).getTypeSet().remove("predicate");
                }
                predicateIndex = i;
            }
            newPhrase.add(phrases.get(i));
        }
        return newPhrase;
    }
    public static List<Phrase> fixData(List<Phrase> phrases){
        if (phrases==null || phrases.size()==0) return phrases;
        List<Phrase> newPhrase = new ArrayList<>();
        if (typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(0).getTypeSet())) return phrases;
        if (phrases.size()==2){
            Phrase phrase;
            if ((phrases.get(0).getTypeSet().contains("n") && phrases.get(1).getTypeSet().contains("m"))
                    ||(phrases.get(0).getTypeSet().contains("m") && phrases.get(1).getTypeSet().contains("a"))
                    ||((phrases.get(0).getTypeSet().contains("a")||phrases.get(0).getTypeSet().contains("predicate"))&&(typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(1).getTypeSet())||phrases.get(1).getTypeSet().contains("predicate")))){
                phrase = new Phrase();
            } else {
                return phrases;
            }
            if (phrase!=null){
                phrase.setValue(phrases.get(0).getValue()+phrases.get(1).getValue());
                Set<String> type = new HashSet<>();
                type.add("predicate");
                phrase.setTypeSet(type);
                phrase.setFrequence(500);
                newPhrase.add(phrase);
            }
            return newPhrase;
        }else if (phrases.size()==1){
            if (phrases.get(0).getTypeSet().contains("a")){
                phrases.get(0).getTypeSet().add("predicate");
                newPhrase.add( phrases.get(0));
                return newPhrase;
            }
        }
        return phrases;
    }

    /**
     * 获取词性
     *
     * @return
     */
    public String getType(Set<String> typeSet ) {
        boolean first = true;
        StringBuilder builder;
        builder = new StringBuilder();
        for (String val : typeSet) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(val);
        }

        return builder.toString();
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
     * 指代词处理，包含人称的
     * 如果当前一轮有代词，上一轮的内容有人名，动物名，地名，汽车品牌名，如果遇到这些词他的下一个词是uj（的）后面在跟一个名词，就将这三个词拼接到一起
     * 否则只找出相对应的词性的数据
     * @param current
     * @param last
     * @return
     */
    public String pronounsStart(List<Phrase> current, List<Phrase> last){
        Phrase currentFirst =  current.get(0);
        List<Integer> LastPredicateIndex = findPredicate(last,0);
        List<Integer> currentPredicateIndex = findPredicate(current,0);
        //判断第一轮的词性为代词
        if (currentFirst.getTypeSet().contains("r")){
            if (LastPredicateIndex.size()>0 && currentPredicateIndex.size()>0){
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<LastPredicateIndex.get(0);i++){
                    sb.append(last.get(i).getValue());
                }
                for (int i=currentPredicateIndex.get(0);i<current.size();i++){
                    sb.append(current.get(i).getValue());
                }
                if (current.get(current.size()-1).getTypeSet().contains("ask")) return sb.toString();
                if (last.get(last.size()-1).getTypeSet().contains("ask")) return sb.append(last.get(last.size()-1).getValue()).toString();
                return sb.toString();
            }
        }
        return null;
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
     * 以动词或者动名词开头的，用主语替换
     * 如果上一轮内容的分词少于4个，并且当前一轮是根以动词或者动名词开头的
     * 将上一轮的主语拼接到当前内容里面
     * @param current
     * @param last
     * @return
     */
    public String verbStart(List<Phrase> current, List<Phrase> last){
        Set<String> lastFirst =  last.get(0).getTypeSet();
        StringBuilder sb = new StringBuilder();
        //上一轮的分词结果少于4个词
        if (last.size()<=3){
            //第一个词的词性是名词或公司或组织或者是人名，直接拼接到第一个词上去
            if (typeIsIncluded(PhraseTypeClassify.entityType,lastFirst)){
                sb.append(last.get(0).getValue());
            }
            //如果第二个词也是名词或者公司组织人名之类的也可以拼接上去
            if (last.size()>2 &&typeIsIncluded(PhraseTypeClassify.entityType,last.get(1).getTypeSet())) sb.append(last.get(1).getValue());
            //将拼接的词做主语，在拼接上以动词开头的所有内容
            current.stream().forEach(val->sb.append(val.getValue()));
            return sb.toString();
        }else {
            //上一轮的分词结果大于3个
            int uj=0;
            //找到连词的位置
            for (int i=0;i<last.size()-1;i++){
                if (last.get(i).getTypeSet().contains("u")) {
                    uj = i;
                    break;
                }
            }
            //连词的位置在0-最后一个中间
            if (uj>0 && uj<last.size()-1){
                //将连词之间的内容拼接起来，做主语
                for (int i=0;i<=uj+1;i++){
                    sb.append(last.get(i).getValue());
                }
                //将主语和谓语拼接
                for (int j=0;j<current.size();j++){
                    sb.append(current.get(j).getValue());
                }
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * 以谓语开头的，或者形容词开头的都可以作为谓语参与替换当中
     * 如果上一轮内容的分词少于4个，并且当前一轮是根以动词或者动名词开头的
     * 将上一轮的主语拼接到当前内容里面
     * @param current
     * @param last
     * @return
     */
    public String predicateStart(List<Phrase> current, List<Phrase> last){
        Set<String> lastFirst =  last.get(0).getTypeSet();
        StringBuilder sb = new StringBuilder();
        //上一轮的分词结果少于4个词
        //第一个词的词性是名词或公司或组织或者是人名，直接拼接到第一个词上去
        if (last.size()<=3){
            if (typeIsIncluded(PhraseTypeClassify.entityType,lastFirst)){
                sb.append(last.get(0).getValue());
            }
            //如果第二个词也是名词或者公司组织人名之类的也可以拼接上去
            if (last.size()>2 &&typeIsIncluded(PhraseTypeClassify.entityType,last.get(1).getTypeSet())) sb.append(last.get(1).getValue());
            //将拼接的词做主语，在拼接上以动词开头的所有内容
            current.stream().forEach(val->sb.append(val.getValue()));
            return sb.toString();
        }else {
            //上一轮的分词结果大于3个
            List<Integer> predicate = findPredicate(last,0);
            //连词的位置在0-最后一个中间
            if (predicate.size()==1){
                //将连词之间的内容拼接起来，做主语
                for (int i=0;i<=predicate.get(0);i++){
                    sb.append(last.get(i).getValue());
                }
                //将主语和谓语拼接
                for (int j=0;j<current.size();j++){
                    sb.append(current.get(j).getValue());
                }
                return sb.toString();
            }
        }
        return null;
    }
    //查询谓语，有主语且带有谓语
    private List<Integer> findPredicate(List<Phrase> phrases,int fromIndex){
        List<Integer> predicate=new ArrayList<>();
        boolean subjectHas = false;
        //找到连词的位置
        for (int i=fromIndex;i<phrases.size();i++){
            if (!subjectHas){
                if (typeIsIncluded(PhraseTypeClassify.entityType,phrases.get(i).getTypeSet())||phrases.get(i).getTypeSet().contains("r")){
                    subjectHas = true;
                }else {
                    continue;
                }
            }else {
                if (phrases.get(i).getTypeSet().contains("predicate")) {
                    predicate.add(i);
                }
                if (phrases.get(i).getTypeSet().contains("u") && i>fromIndex && fromIndex>=0 && phrases.get(i-1).getTypeSet().contains("a")) {
                    predicate.add(i-1);
                }
            }
        }
        return predicate;
    }

    private static boolean typeIsIncluded(String[] basicType,Collection<String> wordType){
        if(basicType==null || wordType==null) return false;
        List<String> basicTypes = Arrays.asList(basicType);
        List<String> commonDataSet = wordType.stream().filter(val->basicTypes.contains(val)).collect(Collectors.toList());
        if (commonDataSet.size()>0) return true;
        return false;
    }

}
