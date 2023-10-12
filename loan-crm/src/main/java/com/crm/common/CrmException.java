package com.crm.common;

public class CrmException extends RuntimeException {

    private static String model = "{\"code\":\"%s\",\"message\":\"%s\",\"e\":\"%s\"}";

    private int code;

    private String message;

    private Throwable e;

    public static String getModel() {
        return model;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Throwable getE() {
        return e;
    }

    public CrmException(Throwable e){
        super();
    }

    public CrmException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public CrmException(int code, String message, Throwable e){
        super(message,e);
        this.code = code;
        this.message = message;
        this.e = e;
    }

    @Override
    public String toString() {
        if(null == this)
            return null;
        return String.format(model,this.code,this.message,null == e?"":e.getMessage());
    }
}
