package org.example.psychologyback.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.psychologyback.DTO.response.ConsultationMessageResponseDTO;
import org.example.psychologyback.entity.ConsultationMessage;
import org.example.psychologyback.entity.ConsultationSession;
import org.example.psychologyback.mapper.ConsultationMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.psychologyback.service.convert.ConsultationMessageConvert;
import java.time.LocalDateTime;

@Service
public class ConsultationMessageService {
    @Autowired
    private ConsultationMessageMapper messageMapper;
    public  ConsultationMessage saveUserMessage(Long sessionId, String content, String emotion_tag) {
        ConsultationMessage message=ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .emotionTag(emotion_tag)
                .createdAt(LocalDateTime.now())
                .build();
        messageMapper.insert(message);
        return message;
    }
    public ConsultationMessage saveAiMessage(Long sessionId, String content, String aiModel) {
        ConsultationMessage message=ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(2)
                .messageType(1)
                .content(content)
                .emotionTag("null")
                .createdAt(LocalDateTime.now())
                .build();
        messageMapper.insert(message);
        return message;
    }
    public  Integer GetMessageCountBysessionId(Long sessionId){
        LambdaQueryWrapper<ConsultationMessage> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ConsultationMessage::getSessionId,sessionId);
        return messageMapper.selectCount(wrapper).intValue();
    }
    public ConsultationMessageResponseDTO getMessage(Long sessionId){
        LambdaQueryWrapper<ConsultationMessage> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ConsultationMessage::getSessionId,sessionId)
                .orderByDesc(ConsultationMessage::getCreatedAt)
                .last("limit 1");
      ConsultationMessage message= messageMapper.selectOne(wrapper);
      if(message != null){
          return ConsultationMessageConvert.consultationMessageResponseConvert(message);
      }
      return null;
    }
}
