package org.example.psychologyback.unit;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.example.psychologyback.common.Result;
import org.example.psychologyback.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    public static void writeError(HttpServletResponse response,  ResultCode resultCode){
        int status =switch(resultCode){
            case UNAUTHORIZED,ACCESS_UNAUTHORIZED,TOKEN_INVALID,TOKEN_BLOCKED -> HttpStatus.UNAUTHORIZED.value();
            case TOKEN_ACCESS_FORBIDDEN ->  HttpStatus.FORBIDDEN.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()){
            String jsonResponse = JSONUtil.toJsonStr(Result.error(resultCode.getCode(), resultCode.getMessage(),null));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        }catch (IOException e){
            System.out.println("写入相应失败"+e.getMessage());
        }
    }
}
