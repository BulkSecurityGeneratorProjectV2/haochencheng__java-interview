package pers.interview.springboot.common;

import lombok.Data;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-07-11 00:49
 **/
@Data
public class ResponseResult<T> {

    private final static Object EMPTY=new Object();
    public static final int SUCCESS_CODE = 0;

    private ResponseResult(){

    }

    private ResponseResult(int code,T data){
        this.code=code;
        this.data=data;
        this.serialNo=SerialNoHolder.getSerialNo();
    }

    private ResponseResult(int code,String msg,T data){
        this.code=code;
        this.msg=msg;
        this.data=data;
        this.serialNo=SerialNoHolder.getSerialNo();
    }

    private String serialNo;

    private int code= SUCCESS_CODE;

    private String msg;

    private T data;

    public static ResponseResult successful(){
        return new ResponseResult();
    }

    public static <T> ResponseResult successful(T data){
        return new ResponseResult(SUCCESS_CODE,data);
    }



    public static <T> ResponseResult error(String msg,T data){
        return new ResponseResult(-1,msg,data);
    }

    public static <T> ResponseResult error(int code,String msg){
        return new ResponseResult(code,msg,EMPTY);
    }

    public static <T> ResponseResult error(String msg){
        return new ResponseResult(-1,msg,EMPTY);
    }

    public boolean isSuccess(){
        return this.code==SUCCESS_CODE;
    }

}
