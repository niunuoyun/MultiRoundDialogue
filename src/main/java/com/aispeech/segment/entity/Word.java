package com.aispeech.segment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Created by Huihua Niu
 * on 2019/7/19 15:18
 */
@Data
public class Word{
    public Word(String value,String typeSet,double frequence){
        this.value = value;
        this.typeSet = typeSet;
        this.frequence = frequence;
    }
    /** 词语内容 */
    private String value;

    /** 词性 */
    private String typeSet;

    /** 词频 */
    private double frequence;
    /** 短语位置和长度 */
    private int position, length;
}
