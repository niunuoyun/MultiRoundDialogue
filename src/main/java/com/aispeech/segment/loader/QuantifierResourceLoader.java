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
 * on 2019/8/29 12:29
 */
@Component
public class QuantifierResourceLoader implements ResourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuantifierResourceLoader.class);
    private static final Map<String,Word> quantifiers=new HashMap<>();
    @Value("${quantifier.dictionary.dir}")
    private String fileName;
    @Override
    public void loadFile(){
            if (!StringUtils.isEmpty(fileName)){
                System.out.println("===============量词加载");
                InputStream stream = WordsDictionaryLoader.class.getClassLoader().getResourceAsStream(fileName);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream,"utf-8"))) {
                    br.lines().forEach(val->{
                        String[] phrase = val.split(":");
                        if (phrase.length>=2) {
                            quantifiers.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
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
        quantifiers.clear();
    }

    @Override
    public void load(Map<String,Word> lines) {
        LOGGER.info("初始化数量词");
        for(Word line : lines.values()){
            if(line.getValue().length() == 1){
                if(quantifiers.containsKey(line)){
                    LOGGER.info("配置文件有重复项："+line);
                }else{
                    quantifiers.put(line.getValue(),line);
                }
            }else{
                LOGGER.info("忽略不合法数量词："+line);
            }
        }
        LOGGER.info("数量词初始化完毕，数量词个数："+quantifiers.size());
    }

    @Override
    public void add(Word line) {
        if (line.getValue().length() == 1) {
            quantifiers.put(line.getValue(),line);
        } else {
            LOGGER.info("忽略不合法数量词：" + line);
        }
    }

    @Override
    public void remove(Word line) {
        if (line.getValue().length() == 1) {
            quantifiers.remove(line);
        } else {
            LOGGER.info("忽略不合法数量词：" + line);
        }
    }

    public static Map<String,Word> getPhrases() {
        return quantifiers;
    }

    @Override
    public void loadData(Set<String> lines) {
        for (String word : lines) {
            String[] phrase = word.split(":");
            if (phrase.length>=2 ) {
                quantifiers.put(phrase[0],new Word(phrase[0],phrase[1],phrase.length==3?Integer.parseInt(phrase[2]):5));
                continue;
            }
            LOGGER.error("词语添加有误{}",phrase[0]);
        }
    }
    public static boolean is(char _char){
        return quantifiers.containsKey(_char);
    }
}
