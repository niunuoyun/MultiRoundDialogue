package com.aispeech.segment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Set;

/**
 * on 2019/12/6 10:59
 * @author  Huihua Niu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "convert_standard")
public class ConvertStandard implements Serializable {

    // 主语类型
    private Set<String> preType;
    // 非标准词
    private String nonStandardWord;
    // 非标准词类型
    private Set<String> nonStandardWordType;
    // 标准词
    private String standardWord;
    // 标准词类型
    private Set<String> standardWordType;
}
