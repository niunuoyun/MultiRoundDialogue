package com.aispeech.segment.tools;

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

    @Autowired
    ConverStandardTool converStandardTool;
    @Autowired
    MatchRuleTool matchRuleTool;
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
        Phrase currentFirst =  currentResult.get(0);
        //词性匹配查询
        List<Phrase> LastStandardWord = converStandardTool.getStandardWords(lastResult);
        if (currentResult.size()>1){
            handlerPhrase(currentResult,LastStandardWord);
        }
        List<List<EntityRelationMapping>> matchRules = matchRuleTool.getMatchRules(currentResult);
        System.out.println(matchRules);

        String combineQuery ;
        if (matchRules.size()>0){
            combineQuery = getMatchResultQuery(LastStandardWord,currentResult,matchRules);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        if (currentFirst.getTypeSet().contains("r")){
            combineQuery =  pronounsStart(currentResult,LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        if (currentFirst.getTypeSet().contains("v")||currentFirst.getTypeSet().contains("vn") ||(currentFirst.getTypeSet().contains("ques")&& currentResult.size()<=2)){
            combineQuery = predicteStart(currentResult,LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        combineQuery = getCombineResult(currentResult,LastStandardWord);
        return combineQuery;
    }
    /**
     * 根据规则生成query
     * @param lastResult
     * @param rules
     */
    public String getMatchResultQuery(List<Phrase> lastResult,List<Phrase> currentResult,List<List<EntityRelationMapping>> rules) {

        if (currentResult.size()>0){
            //以代词开头的肯定是缺失主语
            Set<String> subjects = new HashSet<>();
            Set<String> predicate = new HashSet<>();
            rules.get(0).forEach(rule->{
                subjects.add(rule.getSubjectType());
                predicate.add(rule.getPredicateType());
            });
            List<Phrase> subjectPhrase = new ArrayList<>();
            lastResult.forEach(val->{
                if (TypeHandlerTool.hasSameTypeSet(val.getTypeSet(),subjects)){
                    subjectPhrase.add(val);
                }
            });

            //记录代词的位置 记录谓语词或者主语词的位置；
            int pronounsIndex = -1, subjectIndex = -1, predicateIndex = -1;
            boolean hasAsk = false;
            for (int i=0;i<currentResult.size();i++){
                if (TypeHandlerTool.hasSameTypeSet(currentResult.get(i).getTypeSet(),subjects)){
                    subjectIndex = i;
                }
                if (TypeHandlerTool.hasSameTypeSet(currentResult.get(i).getTypeSet(),predicate)){
                    predicateIndex = i;
                }
                if (currentResult.get(i).getTypeSet().contains("r") || currentResult.get(i).getTypeSet().contains("rr")){
                    if (pronounsIndex>=0) continue;
                    pronounsIndex = i;
                }
                if(currentResult.get(i).getTypeSet().contains("ask") || currentResult.get(i).getTypeSet().contains("ques")){
                    hasAsk = true;
                }
            }
            StringBuilder sb = new StringBuilder();
            //处理有代词的情况
            if (pronounsIndex>=0 && predicateIndex>=0 && subjectPhrase.size()==1){
                if (hasAsk){
                    for (int i=0;i<currentResult.size();i++){
                        if (i==pronounsIndex){
                            sb.append(subjectPhrase.get(0).getValue());
                        }else {
                            sb.append(currentResult.get(i).getValue());
                        }
                    }
                    return sb.toString();
                }else {
                    for (int i=0;i<currentResult.size();i++){
                        if (i==pronounsIndex){
                            sb.append(subjectPhrase.get(0).getValue());
                        }else {
                            sb.append(currentResult.get(i).getValue());
                        }
                    }
                    if (lastResult.get(lastResult.size()-1).getTypeSet().contains("ask")||lastResult.get(lastResult.size()-1).getTypeSet().contains("ques")){
                        sb.append(lastResult.get(lastResult.size()-1).getValue());
                    }
                    return sb.toString();
                }
            }
            //只有谓语的处理
            if (pronounsIndex<0 && predicateIndex>=0 && subjectPhrase.size()==1){
                if (hasAsk){
                    for (int i=0;i<currentResult.size();i++){
                        if (i==predicateIndex){
                            sb.append(subjectPhrase.get(0).getValue());
                        }
                        sb.append(currentResult.get(i).getValue());
                    }
                    return sb.toString();
                }else {
                    for (int i=0;i<currentResult.size();i++){
                        if (i==predicateIndex){
                            sb.append(subjectPhrase.get(0).getValue());
                        }
                        sb.append(currentResult.get(i).getValue());
                    }
                    if (lastResult.get(lastResult.size()-1).getTypeSet().contains("ask")||lastResult.get(lastResult.size()-1).getTypeSet().contains("ques")){
                        sb.append(lastResult.get(lastResult.size()-1).getValue());
                    }
                    return sb.toString();
                }
            }
            //有主语的处理
            if (subjectIndex>=0){
                int index = 0;boolean isReplace = false,isAppendSub = true;
                for (int i=0;i<lastResult.size();i++){
                    for(int j=index;j<currentResult.size();j++){
                        if (TypeHandlerTool.hasSameTypeSet(currentResult.get(j).getTypeSet(),lastResult.get(i).getTypeSet())){
                            sb.append(currentResult.get(j).getValue());
                            index = j+1;
                            isReplace = true;
                            isAppendSub = false;
                            break;
                        }
                    }
                    if (!isAppendSub) {
                        isAppendSub = true;
                        continue;
                    }
                    sb.append(lastResult.get(i).getValue());
                }
                if (isReplace) return sb.toString();
            }

        }
        return null;
    }


    public void handlerPhrase(List<Phrase> currentResult,  List<Phrase> lastResult){
        boolean isNum = false;
        for (int i=0;i<lastResult.size();i++) {
            if (lastResult.get(i).getTypeSet().contains("num") || lastResult.get(i).getTypeSet().contains("m")) {
                isNum = true;
                break;
            }
        }
        if (!isNum){
            for (int i=0;i<currentResult.size();i++) {
                if (i<currentResult.size()-1&&(currentResult.get(i).getTypeSet().contains("num") || currentResult.get(i).getTypeSet().contains("m"))) {
                   Phrase phrase = currentResult.get(i+1);
                   phrase.setValue(currentResult.get(i).getValue()+phrase.getValue());
                   currentResult.set(i,phrase);
                   currentResult.remove(i+1);
                   i+=1;
                    continue;
                }
                if (i==currentResult.size()-1&&(currentResult.get(i).getTypeSet().contains("num") || currentResult.get(i).getTypeSet().contains("m"))){
                    Phrase phrase = currentResult.get(i-1);
                    phrase.setValue(phrase.getValue()+currentResult.get(i).getValue());
                    if (phrase.getTypeSet().contains("v")){
                        phrase.getTypeSet().remove("v");
                        phrase.getTypeSet().add("a");
                    }
                    currentResult.set(i-1,phrase);
                    currentResult.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 获取词性
     *
     * @return
     */
    public String getType(Set<String> typeSet ) {
        boolean first = true;
        StringBuilder builder;

        try {
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
        } finally {
            builder = null;
        }
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
        List<Phrase> pronContent = new ArrayList<>();
        //判断第一轮的词性为代词
        if (currentFirst.getTypeSet().contains("rr")){
            for (int i=0;i<last.size();i++){
                Phrase val = last.get(i);
                //对上一轮内容进行遍历查询出：人名，动物名，公司，汽车品牌名词
                if (val.getTypeSet().contains("nr") || val.getTypeSet().contains("cn") || val.getTypeSet().contains("nam") || val.getTypeSet().contains("cb")) {
                    //从中间找出主语：马云的爸爸的年龄是多大 提取出主语是马云的爸爸（带连词的查找）
                    if (i < last.size() - 3 && last.get(i + 1).getTypeSet().contains("uj") && last.get(i + 2).getTypeSet().contains("n")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(val.getValue() + last.get(i + 1).getValue() + last.get(i + 2).getValue());
                        val.setValue(sb.toString());
                        pronContent.add(val);
                        i = +2;
                    }
                }
            }
            //如果带连词的没有找到，去过滤响应的实体
            if (pronContent.size()==0){
                //todo 最好加上多个个人名是否相连
                pronContent = last.stream().filter(val->val.getTypeSet().contains("nr") || val.getTypeSet().contains("cn") || val.getTypeSet().contains("nam") || val.getTypeSet().contains("cb")).collect(Collectors.toList());
            }
            //如果实体的个数一到两个，用逗号拼接
            if (pronContent.size()>0 && pronContent.size()<3){
                StringBuilder sb = new StringBuilder();
                pronContent.forEach(val->sb.append(val.getValue()).append(","));
                sb.deleteCharAt(sb.length()-1);
                //将当前的分词结果，除了代词拼接到当前句式中
                for (int i=1;i<current.size();i++){
                    sb.append(current.get(i).getValue());
                }
                //如果上一轮存在问句，当前轮不存在，就讲上一轮的问句拿过来拼接上去
                List<Phrase> lastQues = last.stream().filter(val->val.getTypeSet().contains("ques")).collect(Collectors.toList());
                List<Phrase> currentQues = current.stream().filter(val->val.getTypeSet().contains("ques")||val.getTypeSet().contains("r")).collect(Collectors.toList());
                if (currentQues.size()==0 && lastQues.size()>0){
                    sb.append(lastQues.get(lastQues.size()-1).getValue());
                }
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
                    penultimatePhrase.getTypeSet().add("adj");
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
   /* *//**
     * 匹配名词形容词
     * @param current
     * @param last
     * @return
     *//*
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
                    penultimatePhrase.getTypeSet().add("adj");
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
    }*/

    /**
     * 以动词或者动名词开头的，用主语替换
     * 如果上一轮内容的分词少于4个，并且当前一轮是根以动词或者动名词开头的
     * 将上一轮的主语拼接到当前内容里面
     * @param current
     * @param last
     * @return
     */
    public String predicteStart(List<Phrase> current, List<Phrase> last){
        Set<String> lastFirst =  last.get(0).getTypeSet();
        StringBuilder sb = new StringBuilder();
        //上一轮的分词结果少于4个词
        if (last.size()<=3){
            //第一个词的词性是名词或公司或组织或者是人名，直接拼接到第一个词上去
            if (lastFirst.contains("n")||lastFirst.contains("cn")||lastFirst.contains("nr")||lastFirst.contains("nz")){
                sb.append(last.get(0).getValue());
            }
            //如果第二个词也是名词或者公司组织人名之类的也可以拼接上去
            if (last.size()>2 &&(last.get(1).getTypeSet().contains("n")||last.get(1).getTypeSet().contains("cn")||
                    last.get(1).getTypeSet().contains("nr")||last.get(1).getTypeSet().contains("nz"))) sb.append(last.get(1).getValue());
            //将拼接的词做主语，在拼接上以动词开头的所有内容
            current.stream().forEach(val->sb.append(val.getValue()));
            return sb.toString();
        }else {
            //上一轮的分词结果大于3个
            int uj=0;
            //找到连词的位置
            for (int i=0;i<last.size()-1;i++){
                if (last.get(i).getTypeSet().contains("uj")||last.get(i).getTypeSet().contains("u")) {
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

    public List<Phrase> handlerPhrase(List<Phrase> phrases){
        List<Phrase> newPhrase = new ArrayList<>();
        for (int i=0;i<phrases.size();i++){
            if (phrases.get(i).getTypeSet().contains("n") && i+1<=phrases.size() && phrases.get(i).getTypeSet().contains("num")){

            }
        }
        return null;
    }

    /**
     * 处理的是映射关系，由主语找宾语或者有宾语找主语的过程
     * @param relationMappings
     * @param current
     * @param last
     * @return
     */
    private String relationMappingHandler(List<EntityRelationMapping> relationMappings,List<Phrase> current, List<Phrase> last){
        if (relationMappings == null && relationMappings.size()==0) return null;
        if (current==null|| last==null || current.size()==0|| last.size()==0) return null;
        Phrase currentFirst = current.get(0);
        int index = -1;EntityRelationMapping relation;
        for (int i=0;i<relationMappings.size();i++){
            relation = relationMappings.get(i);
            if (currentFirst.getTypeSet().contains(relation.getSubjectType())){
              index = hitIndex(last,relation,false);
              if (index>0) {
                  
              };
            }else if (currentFirst.getTypeSet().contains(relation.getPredicateType())){
                index = hitIndex(last,relation,true);
                if (index>0) break;
            }
        }
        return null;
    }

    private int hitIndex(List<Phrase> last,EntityRelationMapping relation,boolean isSubject){
      int index = -1;
       for (int i=0;i<last.size();i++){
           if(isSubject){
               if (last.get(i).getTypeSet().contains(relation.getSubjectType())){
                   index=i;break;
               }
           }else {
               if (last.get(i).getTypeSet().contains(relation.getPredicateType())){
                   index=i;break;
               }
           }
       }
       return index;
    }

    /**
     * 处理的是映射关系，有代词的情况，分析代词之后的内容
     * @param relationMappings
     * @param current
     * @param last
     * @return
     */
    private String pronounsRelationMappingHandler(List<EntityRelationMapping> relationMappings,List<Phrase> current, List<Phrase> last){
        if (relationMappings == null && relationMappings.size()==0) return null;
        if (current==null|| last==null || current.size()==0|| last.size()==0) return null;
        Phrase currentFirst = current.get(0);
        int index = -1;EntityRelationMapping relation;
        for (int i=0;i<relationMappings.size();i++){
            relation = relationMappings.get(i);
            if (currentFirst.getTypeSet().contains(relation.getSubjectType())){
                index = hitIndex(last,relation,false);
                if (index>0) {

                };
            }else if (currentFirst.getTypeSet().contains(relation.getPredicateType())){
                index = hitIndex(last,relation,true);
                if (index>0) break;
            }
        }
        return null;
    }
}
