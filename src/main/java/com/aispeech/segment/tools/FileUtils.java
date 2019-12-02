package com.aispeech.segment.tools;

import com.aispeech.segment.entity.Phrase;
import lombok.val;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Huihua Niu
 * on 2019/9/3 19:52
 */
public class FileUtils {
    public static void listDirectory(File dir, List<String> fileList)throws IOException {
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
    public static String questionRelatingToAbove(List<Phrase> currentResult,  List<Phrase> lastResult) {
        Phrase currentFirst =  currentResult.get(0);
        if (currentResult.size()>1){
            handlerPhrase(currentResult,lastResult);
        }
        String combineQuery ;
        if (currentFirst.getTypeSet().contains("rr")){
            combineQuery =  pronounsStart(currentResult,lastResult);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        if (currentFirst.getTypeSet().contains("v")||currentFirst.getTypeSet().contains("vn") ||(currentFirst.getTypeSet().contains("ques")&& currentResult.size()<=2)){
            combineQuery = predicteStart(currentResult,lastResult);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        combineQuery = getCombineResult(currentResult,lastResult);
        return combineQuery;
    }


    public static void handlerPhrase(List<Phrase> currentResult,  List<Phrase> lastResult){
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
    public static String getType(Set<String> typeSet ) {
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

    public static boolean  hasSameTypeSet(Set<String> set1, Set<String> set2) {

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

        if ( (intersection.size()==set1.size() && set1.size() == set2.size())|| intersection.size()>=2 ){
            return true;
        }else return false;
    }

    /**
     * 指代词处理，包含人称的
     * 如果当前一轮有代词，上一轮的内容有人名，动物名，地名，汽车品牌名，如果遇到这些词他的下一个词是uj（的）后面在跟一个名词，就将这三个词拼接到一起
     * 否则只找出相对应的词性的数据
     * @param current
     * @param last
     * @return
     */
    public static String pronounsStart(List<Phrase> current, List<Phrase> last){
        Phrase currentFirst =  current.get(0);
        List<Phrase> pronContent = new ArrayList<>();
        if (currentFirst.getTypeSet().contains("rr")){
            for (int i=0;i<last.size();i++){
                Phrase val = last.get(i);
                if (val.getTypeSet().contains("nr") || val.getTypeSet().contains("cn") || val.getTypeSet().contains("nam") || val.getTypeSet().contains("cb")) {
                    if (i < last.size() - 3 && last.get(i + 1).getTypeSet().contains("uj") && last.get(i + 2).getTypeSet().contains("n")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(val.getValue() + last.get(i + 1).getValue() + last.get(i + 2).getValue());
                        val.setValue(sb.toString());
                        pronContent.add(val);
                        i = +2;
                    }
                }
            }
            if (pronContent.size()==0){
                pronContent = last.stream().filter(val->val.getTypeSet().contains("nr") || val.getTypeSet().contains("cn") || val.getTypeSet().contains("nam") || val.getTypeSet().contains("cb")).collect(Collectors.toList());
            }
            if (pronContent.size()>0 && pronContent.size()<3){
                StringBuilder sb = new StringBuilder();
                pronContent.forEach(val->sb.append(val.getValue()).append(","));
                sb.deleteCharAt(sb.length()-1);
                for (int i=1;i<current.size();i++){
                    sb.append(current.get(i).getValue());
                }
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
     * 匹配名词形容词
     * @param current
     * @param last
     * @return
     */
    public static String getCombineResult(List<Phrase> current, List<Phrase> last) {
        List<Integer> index = new ArrayList<>();
        int currentIndex = -1;
        //找出起始位置相同的地方
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
    public static String predicteStart(List<Phrase> current, List<Phrase> last){
        Set<String> lastFirst =  last.get(0).getTypeSet();
        StringBuilder sb = new StringBuilder();
        if (last.size()<=3){
            if (lastFirst.contains("n")||lastFirst.contains("cn")||lastFirst.contains("nr")||lastFirst.contains("nz")){
                sb.append(last.get(0).getValue());
            }
            if (last.size()>2 &&(last.get(1).getTypeSet().contains("n")||last.get(1).getTypeSet().contains("cn")||
                    last.get(1).getTypeSet().contains("nr")||last.get(1).getTypeSet().contains("nz"))) sb.append(last.get(1).getValue());
            current.stream().forEach(val->sb.append(val.getValue()));
            return sb.toString();
        }else {
            int uj=0;
            for (int i=0;i<last.size()-1;i++){
                if (last.get(i).getTypeSet().contains("uj")) {
                    uj = i;
                    break;
                }
            }
            if (uj>0 && uj<last.size()-1){
                for (int i=0;i<=uj+1;i++){
                    sb.append(last.get(i).getValue());
                }
                for (int j=0;j<current.size();j++){
                    sb.append(current.get(j).getValue());
                }
                return sb.toString();
            }
        }
        return null;
    }
}
