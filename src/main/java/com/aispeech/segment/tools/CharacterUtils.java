package com.aispeech.segment.tools;


public class CharacterUtils
{
    /**
     * 全角转单角
     */
    public static int formatASCII(int codePoint)
    {
        if( (codePoint>=65296 && codePoint<=65305)           // ０-９
                || (codePoint>=65313 && codePoint<=65338)    // Ａ-Ｚ
                || (codePoint>=65345 && codePoint<=65370) )  // ａ-ｚ
        { 
            codePoint -= 65248;
        }
        
        return codePoint;
    }
    
    /**
     * 字符国籍类型
     */
    public final static int NATION_UNKNOWN = 0;
    public final static int NATION_EN      = 1; // 英文
    public final static int NATION_RA      = 2; // 俄文
    public final static int NATION_GE      = 3; // 希腊文
    
    /**
     * 获取字符国籍
     * @param codePoint
     * @return
     */
    public static int getNation(int codePoint)
    {
        int nation = NATION_UNKNOWN;
        
        if( isEnglishLetter(codePoint) )
        {
            nation = NATION_EN;
        }
        else if( isRussianLetter(codePoint) )
        {
            nation = NATION_RA;
        }
        else if( isGreekLetter(codePoint) )
        {
            nation = NATION_GE;
        }
        
        return nation;
    }
    
    /**
     * 字符是否为数字
     * @param codePoint
     * @return
     */
    public static boolean isDigit(int codePoint)
    {
        return (Character.getType(codePoint)==Character.DECIMAL_DIGIT_NUMBER);
    }
    
    /**
     * 是否为ASCII字母字符
     * @param codePoint
     * @return
     */
    public static boolean isEnglishLetter(int codePoint)
    {
        return (codePoint>='A' && codePoint<='Z') || (codePoint>='a' && codePoint<='z');
    }
    
    /**
     * 是否为俄文字母字符
     * @param codePoint
     * @return
     */
    public static boolean isRussianLetter(int codePoint)
    {
        return (codePoint >= 'А' && codePoint <= 'я') || codePoint == 'Ё' || codePoint == 'ё';
    }
    
    /**
     * 是否为希腊字母字符
     * @param codePoint
     * @return
     */
    public static boolean isGreekLetter(int codePoint)
    {
        return (codePoint >= 'Α' && codePoint <= 'Ω') || (codePoint >= 'α' && codePoint <= 'ω');
    }
    
    /**
     * 是否为中文字符
     * @param codePoint
     * @return
     */
    public static boolean isChineseLetter(int codePoint)
    {
        return (codePoint >= 0x4e00 && codePoint <= 0x9fbb);
    }
    
}
