package org.example.psychologyback.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.example.psychologyback.DTO.comand.ConsultationSessionCreateDTO;
import org.example.psychologyback.entity.ConsultationSession;
import org.example.psychologyback.entity.User;
import org.example.psychologyback.exception.BusinessException;
import org.example.psychologyback.mapper.ConsultationSessionMapper;
import org.example.psychologyback.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsultationSessionService {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private ConsultationSessionMapper consultationSessionMapper;

    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO){
       User user=userMapper.selectById(userId);
       if(user==null){
           throw new BusinessException("用户不存在");
       }
       if(user!=null){
           ConsultationSession session=ConsultationSession.builder()
                   .userId(userId)
                   .sessionTitle(createDTO.getSessionTitle())
                   .startedAt(LocalDateTime.now())
                   .build();
           if(StrUtil.isBlank(createDTO.getSessionTitle())){
               session.setSessionTitle(String.format("宁波AI助手 -"+ DateUtil.format(LocalDateTime.now(),"MM-dd HH:mm:ss")));
           }
           consultationSessionMapper.insert(session);
           return session;
       }
        return null;
    }
}
