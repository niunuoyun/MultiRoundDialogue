package com.aispeech.segment.enumerate;

public enum TripleElementEnum {

    /**
     * 以前是通用的状态码返回
     */
    SUBJECT(1,"subject"),
    PREDICATE(2,"predicate");


    private String message;

    private Integer code;

    TripleElementEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }

    public void setCode(Integer code){
      this.code = code;
    }

    public void setMessage(String message){
        this.message = message;
    }

}
