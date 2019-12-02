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

/**
 * Created by Huihua Niu
 * on 2019/8/28 20:21
 */
@Component
public class StopWordResourceLoader implements ResourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWordResourceLoader.class);
    private static final Map<String,Word> stopwords = new HashMap<>();
    @Value("${stop.word.dictionary.dir}")
    private String fileName;

    @Override
   public void loadFile() {
        if (!StringUtils.isEmpty(fileName)){
            System.out.println("===============停用词加载");
            InputStream stream = WordsDictionaryLoader.class.getClassLoader().getResourceAsStream(fileName);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream,"utf-8"))) {
                br.lines().forEach(val->{
                    String[] phrase = val.split(":");
                    if (phrase.length>=2 && !isStopChar(phrase[0])) {
                        stopwords.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
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
        stopwords.clear();
    }

    @Override
    public void load(Map<String,Word> lines) {
        LOGGER.info("初始化停用词");
        for (Word line : lines.values()) {
            if (!isStopChar(line.getValue())) {
                stopwords.put(line.getValue(),line);
            }
        }
        LOGGER.info("停用词初始化完毕，停用词个数：" + stopwords.size());
    }

    @Override
    public void loadData(Set<String> lines) {
        for (String word : lines) {
            String[] phrase = word.split(":");
            if (phrase.length>=2 && !isStopChar(phrase[0])) {
                stopwords.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                continue;
            }
            LOGGER.error("词语添加有误{}",phrase[0]);
        }
    }

    @Override
    public void add(Word line) {
        if (!isStopChar(line.getValue())) {
            stopwords.put(line.getValue(),line);
        }
    }

    @Override
    public void remove(Word line) {
        if (!isStopChar(line.getValue())) {
            stopwords.remove(line);
        }
    }

    public static Map<String,Word> getPhrases() {
        return stopwords;
    }


    /**
     * 如果词的长度为一且不是中文字符和数字，则认定为停用词
     *
     * @param word
     * @return
     */
    private static boolean isStopChar(String word) {
        if (word.length() == 1) {
            char _char = word.charAt(0);
            if (_char < 48) {
                return true;
            }
            if (_char > 57 && _char < 19968) {
                return true;
            }
            if (_char > 40869) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断一个词是否是停用词
     *
     * @param word
     * @return
     */
    public static boolean is(String word) {
        if (word == null) {
            return false;
        }
        word = word.trim();
        return isStopChar(word) || stopwords.containsKey(word);
    }

    /**
     * 停用词过滤，删除输入列表中的停用词
     *
     * @param words 词列表
     */
    public static void filterStopWords(List<Word> words) {
        Iterator<Word> iter = words.iterator();
        while (iter.hasNext()) {
            Word word = iter.next();
            if (is(word.getValue())) {
                //去除停用词
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("去除停用词：" + word.getValue());
                }
                iter.remove();
            }
        }
    }

}
