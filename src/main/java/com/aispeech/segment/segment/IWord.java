package com.aispeech.segment.segment;

import java.util.Set;

/**
 * IPhrase
 * @description 分词短语接口
 * @author Eadwin 2015-8-4
 * @Modifier
 */
public interface IWord
{
    /**
     * 词语类型
     */
    public static final String TYPE_UNKNOWN       = "x";  // 未知词
    public static final String TYPE_CHARACTER     = "ch";

    public static final String TYPE_PUNCTUATION   = "po"; // 普通标点符号

    public static final String TYPE_DIGIT         = "dt"; // 数字
    public static final String TYPE_NUMERAL       = "m";  // 数词

    public static final String TYPE_QUANTITY      = "q";  // 量词 - 单位

    public static final String TYPE_ENGLISH       = "en"; // 英文
    public static final String TYPE_GREEK         = "gr"; // 希腊文

    public static final String TYPE_FOREIGN       = "fn"; // 外文

    public static final String TYPE_ORG_TYPE      = "ot"; // 组织形式
    public static final String TYPE_INDUSTRT_TYPE = "it"; // 行业性质

    public static final String TYPE_AREA          = "as"; // 地区

    public static final String TYPE_NOUN          = "n";  // 名词
    public static final String TYPE_PERSON_NAME   = "nr"; // 人名姓
    public static final String TYPE_FAMILY_NAME   = "nx"; // 人名姓

    public static final String TYPE_PREPOSITIONAL = "p";  // 介词
    public static final String TYPE_VERB          = "v";  // 名词

    /**
     * 获取短语类型集合
     * @return
     */
    public Set<String> getTypeSet();
    
    /**
     * 是否包含特定类型
     * @param type
     */
    public boolean hasType(String type);
    
    /**
     * 获取短语类型用","分割
     * @return
     */
    public String getType();
    
    /**
     * 获取短语内容
     * @return
     */
    public String getValue();
    
    /**
     * 获取短语在文章中的位置
     * @return
     */
    public int getPosition();
    
    /**
     * 获取短语长度
     * @return
     */
    public int length();
    
}
