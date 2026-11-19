package org.example.psychologyback.service.convert;

import org.example.psychologyback.DTO.response.ConsultationMessageResponseDTO;
import org.example.psychologyback.entity.ConsultationMessage;


public class ConsultationMessageConvert {
    public  static ConsultationMessageResponseDTO consultationMessageResponseConvert(ConsultationMessage consultationMessage) {
     ConsultationMessageResponseDTO responseDTO = ConsultationMessageResponseDTO.builder()
               .id(consultationMessage.getId())
               .sessionId(consultationMessage.getSessionId())
               .senderType(consultationMessage.getSenderType())
               .messageType(consultationMessage.getMessageType())
               .content(consultationMessage.getContent())
               .emotionTag(consultationMessage.getEmotionTag())
               .aiModel(consultationMessage.getAiModel())
               .createdAt(consultationMessage.getCreatedAt())
               .build();
     responseDTO.calculateContentLength();
     return responseDTO;
    }
}
