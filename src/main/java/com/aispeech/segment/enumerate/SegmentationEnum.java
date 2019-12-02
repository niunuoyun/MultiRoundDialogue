
package com.aispeech.segment.enumerate;

/**
 * 中文分词算法
 * @author huihua.niu
 */
public enum SegmentationEnum {
    /**
     * 正向最大匹配算法
     */
    MaximumMatching("正向最大匹配算法"),
    /**
     * 逆向最大匹配算法
     */
    ReverseMaximumMatching("逆向最大匹配算法"),
    /**
     * 正向最小匹配算法
     */
    MinimumMatching("正向最小匹配算法"),
    /**
     * 逆向最小匹配算法
     */
    ReverseMinimumMatching("逆向最小匹配算法"),
    /**
     * 双向最大匹配算法
     */
    BidirectionalMaximumMatching("双向最大匹配算法"),
    /**
     * 双向最小匹配算法
     */
    BidirectionalMinimumMatching("双向最小匹配算法"),
    /**
     * 双向最大最小匹配算法
     */
    BidirectionalMaximumMinimumMatching("双向最大最小匹配算法"),
    /**
     * 全切分算法
     */
    FullSegmentation("全切分算法"),

    /**
     * 最少词数算法
     */
    MinimalWordCount("最少词数算法"),

    /**
     * 简单分词算法，词语最长匹配
     */
    ALGORITHM_SIMPLE("词语最长匹配"),

    /**
     * 针对纯英文文本的分词算法
     */
    PureEnglish("针对纯英文文本的分词算法");

    private SegmentationEnum(String des){
        this.des = des;
    }
    private final String des;
    public String getDes() {
        return des;
    }
}
