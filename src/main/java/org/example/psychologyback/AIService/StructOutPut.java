package org.example.psychologyback.AIService;

public class StructOutPut {
    public record StreamChatSession(
        String sessionId,
        Long userHash,
        String initialMessage,
        Long startTime,
        Long expriryTime,
        Integer MessageCount,
        String status
    ){}
}
