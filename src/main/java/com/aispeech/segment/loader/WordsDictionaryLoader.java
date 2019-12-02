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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 词典工具
 * 1、把多个词典合并为一个并规范清理
 * 词长度：只保留大于等于2并且小于等于4的长度的词
 * 识别功能： 移除能识别的词
 * 移除非中文词：防止大量无意义或特殊词混入词典
 * 2、移除词典中的短语结构
 * @author huihua.niu
 */
@Component
public final class WordsDictionaryLoader implements ResourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordsDictionaryLoader.class);
    /** 词 */
    private static Map<String,Word> wordList = new HashMap<>();
    @Value("${dictionary.dir}")
    private String fileName;

    @Override
    public void loadFile(){
        if (!StringUtils.isEmpty(fileName)){
            System.out.println("=============词库加载======");
            InputStream stream = WordsDictionaryLoader.class.getClassLoader().getResourceAsStream(fileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream,"utf-8"))) {
                br.lines().forEach(val->{
                    String[] phrase = val.split(":");
                    if (phrase.length>=2) {
                        wordList.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
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
        wordList.clear();
    }
    @Override
    public void load(Map<String,Word> lines) {
        LOGGER.info("初始化词典");
        long start = System.currentTimeMillis();
        wordList.putAll(lines);
        System.gc();
        LOGGER.info("词典初始化完毕，耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
    }

    @Override
    public void loadData(Set<String> lines) {
        for (String word : lines) {
           String[] phrase = word.split(":");
            if (phrase.length>=2) {
                wordList.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                continue;
            }
            LOGGER.error("词语添加有误{}",phrase[0]);
        }
    }

    @Override
    public void add(Word line) {
        wordList.put(line.getValue(),line);
    }

    @Override
    public void remove(Word line) {
        wordList.remove(line);
    }

    public static Map<String,Word> getPhrases() {
        return wordList;
    }

    public static void main(String[] args) {
        WordsDictionaryLoader wordsDictionaryLoader = new WordsDictionaryLoader();
       // wordsDictionaryLoader.loadFile();
        System.out.println("======");

    }
}
