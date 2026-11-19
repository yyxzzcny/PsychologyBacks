package org.example.psychologyback.common;
import lombok.Data;

@Data
public class Result<T>{
    private String code;
    private String message;
    private T data;

    public static<T> Result<T>ok(){
        Result<T>result=new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
}

    public static<T> Result<T>ok(T data){
        Result<T>result=ok();
        result.setData(data);
        return result;
    }
    public static<T> Result<T>error(){
        Result<T>result=new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(ResultCode.ERROR.getMessage());
        return result;
    }
    public static<T> Result<T>error(String code,String meg,T data){
      Result<T>result=new Result<>();
      result.setCode(code);
      result.setMessage(meg);
      result.setData(data);
      return result;
    }
}

