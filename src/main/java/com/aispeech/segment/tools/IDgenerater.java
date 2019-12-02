package com.aispeech.segment.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class IDgenerater {

    /**
     * 用UUID生成唯一key
     * @return
     */
    public static String create(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * MD5加密，作为ID
     * @param sourceStr
     * @return
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");//前面补0
                buf.append(Integer.toHexString(i));//转换成16进制编码
            }
            result = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("1===="+MD5("马不停蹄"));
        System.out.println("2===="+MD5("马不停蹄"));
    }
}
