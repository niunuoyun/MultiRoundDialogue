package com.aispeech.segment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Huihua Niu
 * on 2019/12/9 10:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "phrase_classify")
public class PhraseClassify {
    // 词性
    private String phraseType;
    // 词性标签
    private String label;
    // 在三元组中的成分
    private String tripleElement;
    // 是否是关键类型
    private boolean isKeyType;
    // 描述
    private String description;
}
