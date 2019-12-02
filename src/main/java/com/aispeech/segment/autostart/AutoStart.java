package com.aispeech.segment.autostart;/*
package com.aispeech.segment.autostart;

import com.aispeech.segment.segment.seg.Tokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

*/
/**
 * Created by Huihua Niu
 * on 2019/9/6 14:54
 *//*

@Component
public class AutoStart implements ApplicationRunner {
    @Autowired
   public Tokenizer tokenizer;

    @Override
    public void run(ApplicationArguments args){
        System.out.println("======词典构建中=====");
        System.out.println(tokenizer.getDoubleArrayTrie().parseText("开启分词"));
    }
}
*/
