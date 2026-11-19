package org.example.psychologyback.Controller;

import org.example.psychologyback.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api")
@RestController
public class Psychology {
    @GetMapping("/test")
    public Result<String> test() {
       return Result.ok();
        //调用者决定输出报错信息
//       return Result.error("500","错误","死了");
    }
}
