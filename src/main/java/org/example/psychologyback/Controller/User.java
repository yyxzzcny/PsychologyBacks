package org.example.psychologyback.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.psychologyback.DTO.comand.UserLoginCommandDTO;
import org.example.psychologyback.DTO.comand.UserRegisterCommandDTO;
import org.example.psychologyback.DTO.response.UserLoginResponseDTO;
import org.example.psychologyback.common.Result;
import org.example.psychologyback.service.UserService;
import org.example.psychologyback.unit.JwtTokenUtil;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;

@RestController
@RequestMapping("/api/user")
public class User
    {
         @Resource
         private UserService userService;
        @PostMapping("/login")
        public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO userLoginDTO)
        {
            System.out.println(userLoginDTO.getUsername());
            System.out.println(userLoginDTO.getPassword());
            UserLoginResponseDTO result= userService.Login(userLoginDTO);
            return Result.ok(result);
        }
        @PostMapping("/add")
        public Result<UserLoginResponseDTO.UserDetail> add(@Valid @RequestBody UserRegisterCommandDTO userRegisterCommandDTO )
        {
          UserLoginResponseDTO.UserDetail result=userService.Register(userRegisterCommandDTO);
          return Result.ok(result);
        }
        //获取当前用户
        @GetMapping("/current")
        public Result<UserLoginResponseDTO.UserDetail> getCurrentUser(){
            String token = JwtTokenUtil.getCurrentToken();
            DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
            Long userId = jwt.getClaim("userId").asLong();
            UserLoginResponseDTO.UserDetail result= userService.getUserInfo(userId);
            return Result.ok(result);
        }
}
