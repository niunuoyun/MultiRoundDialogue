package com.aispeech.segment.loader;

import com.aispeech.segment.entity.Word;
import com.aispeech.segment.segment.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Huihua Niu
 * on 2019/8/29 10:42
 */
@Component
public class PersonNameResourceLoader implements ResourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonNameResourceLoader.class);
    private static final Map<String,Word> SURNAME_1 =new HashMap<>();
    private static final Map<String,Word> SURNAME_2 =new HashMap<>();
    @Value("${surnames.dictionary.dir}")
    private String fileName;
    @Override
    public void loadFile(){
        if (!StringUtils.isEmpty(fileName)){
            InputStream stream = WordsDictionaryLoader.class.getClassLoader().getResourceAsStream(fileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream,"utf-8"))) {
                br.lines().forEach(val->{
                    String[] phrase = val.split(":");
                    if (phrase[0].length() == 1) {
                        SURNAME_1.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                    } else if (phrase[0].length() == 2) {
                        SURNAME_2.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                    }else {
                        LOGGER.error("词语添加有误{}",phrase[0]);
                    }
                });
            }catch (Exception e){
                LOGGER.error("lockSkillTriggerWord.txt read exception, {}", e);
            }
        }
    }

    @Override
    public void clear() {
        SURNAME_1.clear();
        SURNAME_2.clear();
    }

    @Override
    public void load(Map<String,Word> lines) {
        LOGGER.info("初始化百家姓");
        for (Word word : lines.values()) {
            if (word.getValue().length() == 1) {
                SURNAME_1.put(word.getValue(),word);
            } else if (word.getValue().length() == 2) {
                SURNAME_2.put(word.getValue(),word);
            } else {
                LOGGER.error("错误的姓：" + word.getValue());
            }
        }
        LOGGER.info("百家姓初始化完毕，单姓个数：" + SURNAME_1.size() + "，复姓个数：" + SURNAME_2.size());
    }

    @Override
    public void loadData(Set<String> lines) {
        for (String word : lines) {
            String[] phrase = word.split(":");
            if (phrase[0].length() == 1) {
                SURNAME_1.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                continue;
            } else if (phrase[0].length() == 2) {
                SURNAME_2.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                continue;
            }
            LOGGER.error("错误的姓{}",phrase[0]);
        }
    }

    @Override
    public void add(Word word) {
        if (word.getValue().length() == 1) {
            SURNAME_1.put(word.getValue(),word);
        } else if (word.getValue().length() == 2) {
            SURNAME_2.put(word.getValue(),word);
        } else {
            LOGGER.error("错误的姓：" + word.getValue());
        }
    }

    @Override
    public void remove(Word word) {
        if (word.getValue().length() == 1) {
            SURNAME_1.remove(word.getValue());
        } else if (word.getValue().length() == 2) {
            SURNAME_2.remove(word.getValue());
        }  else {
            LOGGER.error("错误的姓：" + word.getValue());
        }
    }

    public static Map<String,Word> getPhrases() {
        SURNAME_1.putAll(SURNAME_2);
        return SURNAME_1;
    }
    /**
     * 对分词结果进行处理，识别人名
     * @param words 待识别分词结果
     * @return 识别后的分词结果
     */
    public static List<Word> recognize(List<Word> words){
        int len = words.size();
        if(len < 2){
            return words;
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("人名识别：" + words);
        }
        List<List<Word>> select = new ArrayList<>();
        List<Word> result = new ArrayList<>();
        for(int i=0; i<len-1; i++){
            String word = words.get(i).getValue();
            if(isSurname(word)){
                result.addAll(recognizePersonName(words.subList(i, words.size())));
                select.add(result);
                result = new ArrayList<>(words.subList(0, i+1));
            }else{
                result.add(new Word(word,"",0));
            }
        }
        if(select.isEmpty()){
            return words;
        }
        if(select.size()==1){
            return select.get(0);
        }
        return selectBest(select);
    }
    /**
     * 判断文本是不是百家姓
     * @param text 文本
     * @return 是否
     */
    public static boolean isSurname(String text){
        return SURNAME_1.containsKey(text) || SURNAME_2.containsKey(text);
    }
    /**
     * 使用词性序列从多个人名中选择一个最佳的
     * @param candidateWords
     * @return
     */
    private static List<Word> selectBest(List<List<Word>> candidateWords){
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("开始从多个识别结果中选择一个最佳的结果:{}", candidateWords);
        }
        Map<List<Word>, Integer> map = new ConcurrentHashMap<>();
        AtomicInteger i = new AtomicInteger();
        candidateWords.stream().forEach(candidateWord -> {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(i.incrementAndGet() + "、开始处理：" + candidateWord);
            }
            //根据词性标注的结果进行评分
            StringBuilder seq = new StringBuilder();
            candidateWord.forEach(word -> seq.append(word.getTypeSet()).append(" "));
            String seqStr = seq.toString();
            AtomicInteger score = new AtomicInteger();
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("词序列：{} 的词性序列：{}", candidateWord, seqStr);
            }
            score.addAndGet(-candidateWord.size());
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("长度的负值也作为分值：" + (-candidateWord.size()));
                LOGGER.debug("评分结果：" + score.get());
            }
            map.put(candidateWord, score.get());
        });
        //选择分值最高的
        List<Word> result = map.entrySet().stream().sorted((a,b)->b.getValue().compareTo(a.getValue())).map(e->e.getKey()).collect(Collectors.toList()).get(0);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("选择结果："+result);
        }
        return result;
    }
    private static List<Word> recognizePersonName(List<Word> words){
        int len = words.size();
        if(len < 2){
            return words;
        }
        List<Word> result = new ArrayList<>();
        for(int i=0; i<len-1; i++){
            String second = words.get(i+1).getValue();
            if(second.length() > 1){
                result.add(words.get(i));
                result.add(words.get(i+1));
                i++;
                if(i == len-2){
                    result.add(words.get(i+1));
                }
                continue;
            }
            String first = words.get(i).getValue();
            if(isSurname(first)){
                String third = "";
                if(i+2 < len && words.get(i+2).getValue().length()==1){
                    third = words.get(i+2).getValue();
                }
                String text = first+second+third;
                if(is(text)){
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("识别到人名：" + text);
                    }
                    words.get(i).setTypeSet("nr");
                    words.get(i).setValue(text);
                    result.add(words.get(i));
                    i++;
                    if(!"".equals(third)){
                        i++;
                    }
                }else{
                    result.add(words.get(i));
                }
            }else{
                result.add(words.get(i));
            }
            if(i == len-2){
                result.add(words.get(i+1));
            }
        }
        return result;
    }

    /**
     * 人名判定
     * @param text 文本
     * @return 是或否
     */
    public static boolean is(String text){
        int len = text.length();
        //单姓为二字或三字
        //复姓为三字或四字
        if(len < 2){
            //长度小于2肯定不是姓名
            return false;
        }
        if(len == 2){
            //如果长度为2，则第一个字符必须是姓
            return SURNAME_1.containsKey(text.substring(0, 1));
        }
        if(len == 3){
            //如果长度为3
            //要么是单姓
            //要么是复姓
            return SURNAME_1.containsKey(text.substring(0, 1)) || SURNAME_2.containsKey(text.substring(0, 2));
        }
        if(len == 4){
            //如果长度为4，只能是复姓
            return SURNAME_2.containsKey(text.substring(0, 2));
        }
        return false;
    }
}
