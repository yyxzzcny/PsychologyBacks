package org.example.psychologyback.AIService;

import org.example.psychologyback.DTO.comand.ConsultationSessionCreateDTO;
import org.example.psychologyback.DTO.response.ConsultationMessageResponseDTO;
import org.example.psychologyback.entity.ConsultationMessage;
import org.example.psychologyback.entity.ConsultationSession;
import org.example.psychologyback.service.ConsultationMessageService;
import org.example.psychologyback.service.ConsultationSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class PsychologySupportService {
    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private ConsultationSessionService consultationSessionService;
    @Autowired
    private ConsultationMessageService consultationMessageService;
    public StructOutPut.StreamChatSession StartSession(Long userId, ConsultationSessionCreateDTO createDTO) {
         //创建数据库会话记录
        ConsultationSession session=consultationSessionService.createSession(userId,createDTO);
        //创建会话对应消息列表
        ConsultationMessage message=consultationMessageService.saveUserMessage(session.getId(),createDTO.getInitialMessage(),null);
        // 创建会话信息
        String sessionId = "session_" + session.getId();
        return new StructOutPut.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L, // 24小时
                1,
                "ACTIVE"
        );

    }
    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {

        // 创建响应流
        return Flux.create(sink -> {
            // sink.next("数据1") // 发布数据
            // sink.complete(); // 完成流
            // sink.error(exception); // 发布错误
            //生成会话ID
            String conversationId="conversation_"+sessionId;

            Long dbSessionId=extractSessionId(sessionId);
            if(dbSessionId==null){
                sink.error(new IllegalArgumentException("会话ID格式错误"));
                return;
            }
            Integer messageCount=consultationMessageService.GetMessageCountBysessionId(dbSessionId);
            Boolean isInitialMessage=false;
            if(messageCount>=1) {
                ConsultationMessageResponseDTO lastMessage = consultationMessageService.getMessage(dbSessionId);

                if (lastMessage != null && lastMessage.getSenderType() == 1 && lastMessage.equals(userMessage)) {
                    isInitialMessage = true;
                }
            }
                if (!isInitialMessage) {
                    consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
                }
                List<Message> userMessageList = new ArrayList<>();
                userMessageList.add(new UserMessage(userMessage));
                //添加用户消息到记忆体
                chatMemory.add(conversationId, userMessageList);
                //用于存储AI完成的响应
                StringBuilder fullResponse = new StringBuilder();

                Prompt prompt = new Prompt(List.of(
                        new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
                ));
                chatClient.prompt(prompt)
                        .user(userMessage)
                        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .stream()
                        .content()
                        .doOnNext(Fragment -> {
                            sink.next(Fragment);
                            fullResponse.append(Fragment);
                        })
                        .doOnComplete(() -> {
                            consultationMessageService.saveAiMessage(dbSessionId, fullResponse.toString(), "open-ai");
                            //添加ai回复到记忆体

                            // 添加AI回复到chatMemory
                            List<Message> aiMessages = new ArrayList<>();
                            aiMessages.add(new AssistantMessage(fullResponse.toString()));
                            chatMemory.add(conversationId, aiMessages);
                            sink.complete();
                        })
                        .doOnError(error -> {
                            sink.error(error);
                        })
                        .subscribe(); // 订阅并启动流

        });
    }
    // 方法：传入类似 "session_1001" 的字符串，提取后面数字，返回Long类型id
    public Long extractSessionId(String sessionId) {
        // 判空 + 判断前缀：不为null，并且以 session_ 开头
        if (sessionId != null && sessionId.startsWith("session_")) {
            // 截取：从 "session_" 这个字符串长度之后开始截取，拿到后面的数字字符串
            String idStr = sessionId.substring("session_".length());
            // 把数字字符串转成 Long 返回
            return Long.parseLong(idStr);
        }
        // 不满足条件（null / 前缀不对），返回 null
        return null;
    }
}
