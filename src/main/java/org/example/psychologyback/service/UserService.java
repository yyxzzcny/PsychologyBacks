package org.example.psychologyback.service;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.jdbc.Null;
import org.example.psychologyback.DTO.comand.UserLoginCommandDTO;
import org.example.psychologyback.DTO.comand.UserRegisterCommandDTO;
import org.example.psychologyback.DTO.response.UserLoginResponseDTO;
import org.example.psychologyback.common.Result;
import org.example.psychologyback.entity.User;
import org.example.psychologyback.enumClass.UserType;
import org.example.psychologyback.exception.BusinessException;
import org.example.psychologyback.mapper.UserMapper;
import org.example.psychologyback.service.convert.UserConvert;
import org.example.psychologyback.unit.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserDetailsPasswordService userDetailsPasswordService;
    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;

    public UserLoginResponseDTO Login(UserLoginCommandDTO userLoginDTO){
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,userLoginDTO.getUsername())
                .or()
                .eq(User::getEmail,userLoginDTO.getUsername());
        User user=userMapper.selectOne(queryWrapper);
        System.out.println(user);
        if(user==null){
            throw new BusinessException("用户不存在");
        }
        String inputPassword = userLoginDTO.getPassword().trim();
        if(!passwordEncoder.matches(inputPassword,user.getPassword())){
          throw new BusinessException("密码错误");
        }
        if(!user.isActive()) {
            throw new BusinessException("用户已经被禁用,请联系管理员");
        }
        String token = JwtTokenUtil.generateToken(user.getId(),user.getUsername(),user.getUserType());
        System.out.println("token:"+token);


        UserLoginResponseDTO.UserDetail userInfo= UserConvert.entityToDetailResponse(user);
        return UserConvert.buildLoginResponse(token,userInfo);

    }
    public UserLoginResponseDTO.UserDetail Register(UserRegisterCommandDTO userRegisterDTO){
     System.out.println(JSONUtil.parse(userRegisterDTO));
     //两次密码是否一致
     if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
         throw new BusinessException("输入的两次密码不一致");
     }
     //用户名是否存在
     LambdaQueryWrapper<User> UserNameQ=new LambdaQueryWrapper<>();
     UserNameQ.eq(User::getUsername,userRegisterDTO.getUsername());
     if(userMapper.selectCount(UserNameQ)>0){
         throw new BusinessException("用户名已注册");
     }
     LambdaQueryWrapper<User> EmiilQ=new LambdaQueryWrapper<>();
     EmiilQ.eq(User::getEmail,userRegisterDTO.getEmail());
     if(userMapper.selectCount(EmiilQ)>0){
         throw new BusinessException("邮箱已经注册");
        }
     //判断用户类型
        if(!UserType.isValidCode(userRegisterDTO.getUserType())){
            throw new BusinessException("当前注册的用户类型不存在");
        }
        //创建用户
        String password=userRegisterDTO.getPassword().trim();
        String encodedPassword=passwordEncoder.encode(password);
            User user=UserConvert.RegisterCommandtoEntity(userRegisterDTO,encodedPassword);
            userMapper.insert(user);

         return UserConvert.entityToDetailResponse(user);
    }
public UserLoginResponseDTO.UserDetail getUserInfo(Long id){
    User user=userMapper.selectById(id);
    if(user==null){
        throw new BusinessException("用户不存在");
    }
    return UserConvert.entityToDetailResponse(user);
}
}
