package com.aispeech.segment;

import com.aispeech.segment.framework.BeanContainer;
import com.aispeech.segment.segment.seg.Tokenizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SegmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SegmentApplication.class, args);
    }

}
